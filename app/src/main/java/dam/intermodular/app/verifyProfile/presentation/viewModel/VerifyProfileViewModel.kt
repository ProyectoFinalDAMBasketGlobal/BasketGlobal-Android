package dam.intermodular.app.verifyProfile.presentation.viewModel


import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.core.navigation.Login
import dam.intermodular.app.verifyProfile.domain.VerifyProfileRepository
import dam.intermodular.app.verifyProfile.presentation.model.VerifyProfileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Locale
import java.text.ParseException


@HiltViewModel
class VerifyProfileViewModel @Inject constructor(
    private val repositoryVerifyProfile: VerifyProfileRepository,
    private val dataStoreManager: DataStoreManager,
    private val application: Application
): ViewModel() {

    private val _id = MutableStateFlow<String>("")
    val id: StateFlow<String> = _id

    private val _nombre = MutableStateFlow<String>("")
    val nombre : StateFlow<String> = _nombre

    private val _apellido = MutableStateFlow<String>("")
    val apellido : StateFlow<String> = _apellido

    private val _rol = "Cliente"

    private val _dni = MutableStateFlow<String>("")
    val dni : StateFlow<String> = _dni

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date

    private val _dateFormatted = MutableStateFlow("")
    val dateFormatted: StateFlow<String> = _dateFormatted

    private val _ciudad = MutableStateFlow<String>("")
    val ciudad : StateFlow<String> = _ciudad

    private val _sexo = MutableStateFlow<String>("")
    val sexo : StateFlow<String> = _sexo

    private val _tarjetaPuntos = MutableStateFlow<Number>(0)
    val tarjetaPuntos: StateFlow<Number> = _tarjetaPuntos

    private val _picture = MutableStateFlow<Uri?>(null)
    val picture : StateFlow<Uri?> = _picture

    // Flag to check if the user profile has been loaded
    private var isProfileLoaded = false

    init {
        viewModelScope.launch {
            // Recuperamos el idUsuario desde DataStore
            val idUsuarioStored = dataStoreManager.getIdProfile().first()
            if (!idUsuarioStored.isNullOrEmpty()) {
                _id.value = idUsuarioStored
                Log.d("VerifyProfileViewModel", "idUsuario recuperado: $idUsuarioStored")

                // Only call getUserProfile once
                if (!isProfileLoaded) {
                    getUserProfile()
                    isProfileLoaded = true
                }
            } else {
                Log.e("VerifyProfileViewModel", "No se encontró un idUsuario en DataStore")
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _nombre.value = nombre
        Log.d("ViewModel", "onChange nombre: $nombre")
    }
    fun onApellidoChange(apellido: String) {
        _apellido.value = apellido
        Log.d("ViewModel", "onChange apellido: $apellido")
    }
    fun onDniChange(dni: String) {
        _dni.value = dni
        Log.d("ViewModel", "onChange dni: $dni")
    }
    fun onDateChange(date: String) {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault()) // Entrada del usuario
        val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Para el campo
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Para el servidor

        try {
            val parsedDate = inputFormat.parse(date)
            if (parsedDate != null) {
                _date.value = outputFormat.format(parsedDate) // Formato para el servidor
                _dateFormatted.value = displayFormat.format(parsedDate) // Formato visible en UI
                Log.d("ViewModel", "Fecha convertida: ${_date.value}")
            }
        } catch (e: ParseException) {
            Log.e("ViewModel", "Error al parsear la fecha: $date")
        }
    }
    fun onCiudadChange(ciudad: String) {
        _ciudad.value = ciudad
        Log.d("ViewModel", "onChange dni: $ciudad")
    }
    fun onSexoChange(sexo: String) {
        _sexo.value = sexo
        Log.d("ViewModel", "onChange dni: $sexo")
    }
    fun onPictureSelected(uri: Uri?) { _picture.value = uri }

   private fun getFileFromUri(uri: Uri): File? {
        val fileName = "picture_${System.currentTimeMillis()}.jpg"
        val file = File(application.cacheDir,fileName)
       return try {
           val inputStream = application.contentResolver.openInputStream(uri)
           val outputStream = FileOutputStream(file)
           inputStream?.copyTo(outputStream)
           inputStream?.close()
           outputStream.close()
           file
       }catch (e:Exception){
           Log.e("File Conversion", "Error converting Uri to File", e)
           null
       }
    }


    fun sendRegisterProfile(navigateTo: NavController,idUsuario:String) = viewModelScope.launch {
        Log.d("ViewModel", "sendRegisterProfile ha sido llamada")
        withContext(Dispatchers.IO){

            Log.d("ViewModel", "Entrando a sendRegisterProfile con idUsuario: $idUsuario")

            val imageFile = _picture.value?.let { getFileFromUri(it) }
            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull()) // ← Corrección aquí
                MultipartBody.Part.createFormData("picture",it.name,requestFile)
            }

            Log.d("Data viewModel: ","$_nombre")
            Log.d("Data viewModel: ","$_apellido")
            Log.d("Data viewModel: ", _rol)
            Log.d("Data viewModel: ","$_dni")
            Log.d("Data viewModel: ","$_date")
            Log.d("Data viewModel: ","$_ciudad")
            Log.d("Data viewModel: ","$_sexo")
            val user = VerifyProfileModel(idUsuario,_nombre.value,apellido.value,_rol,_dni.value,_date.value,_ciudad.value,sexo.value,tarjetaPuntos.value)
            val result = repositoryVerifyProfile.sendVerifyProfileRepository(user,imagePart)
            result
                .onSuccess {
                    //Hacer algo con la peticion.
                    Log.d("API Response", "Registro exitoso")
                    withContext(Dispatchers.Main) {  // 🚀 Cambia al hilo principal para la navegación
                        navigateTo.navigate("login_screen"){
                            popUpTo<Login>{inclusive = true}
                        }
                    }
                }
                .onFailure {
                    Log.e("API Error", "Error en el registro: ${it.message}")
                }
        }
    }

    private fun getUserProfile() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val idUsuarioStored = _id.value
            if (idUsuarioStored.isNotEmpty()) {
                // Llamada a la API para obtener el perfil de un solo cliente
                val response = repositoryVerifyProfile.getUserProfile(idUsuarioStored)

                response.onSuccess { cliente ->
                    // Aquí 'cliente' es un solo objeto VerifyProfilModel
                    withContext(Dispatchers.Main) {
                        _nombre.value = cliente.nombre
                        _apellido.value = cliente.apellido
                        _dni.value = cliente.dni
                        _date.value = cliente.date
                        _ciudad.value = cliente.ciudad
                        _sexo.value = cliente.sexo
                        _tarjetaPuntos.value = cliente.tarjetaPuntos
                    }

                    Log.d("VerifyProfileViewModel", "Perfil cargado: Nombre: ${cliente.nombre}")
                }.onFailure {
                    Log.e("API Error", "Error al obtener perfil: ${it.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("VerifyProfileViewModel", "Error al obtener perfil: ${e.message}")
        }
    }
}