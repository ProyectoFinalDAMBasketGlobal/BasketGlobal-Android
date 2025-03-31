package dam.intermodular.app.login.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.core.navigation.Home
import dam.intermodular.app.login.data.LoginResponse
import dam.intermodular.app.login.domain.LogInRepository
import dam.intermodular.app.login.presentation.model.LogInModel
import dam.intermodular.app.verifyProfile.data.PasswordRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repositoryLogin: LogInRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    fun setUserId(id: String) {
        _userId.value = id
    }

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> = _authState

    private val _isCheckingToken = MutableStateFlow(true)
    val isCheckingToken: StateFlow<Boolean> = _isCheckingToken


    private val _isLoginView = MutableStateFlow<Boolean>(true)
    val isLoginView: StateFlow<Boolean> = _isLoginView

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {

        viewModelScope.launch {
            // Cargar el token desde DataStore al iniciar la aplicación
            Log.d("Init VM_Login","Se inicio viewModel")
            dataStoreManager.getAccessToken().collect(){ tokenAlmacenado ->
                _token.value = tokenAlmacenado // Actualizar el token en memoria
                if(!tokenAlmacenado.isNullOrEmpty()){
                    Log.d("Init VM_Login","Token valido encontrado")
                    _authState.value = "Token valido encontrado"
                }else{
                    Log.d("Init VM_Login","No hay token")
                    _authState.value = "No hay token"
                }
            }
            val idProfile = dataStoreManager.getIdProfile().firstOrNull() // Recupera el ID del usuario
            if (!idProfile.isNullOrEmpty()) {
                _userId.value = idProfile // Actualiza el `StateFlow`
                Log.d("LoginViewModel", "ID del usuario cargado: $idProfile")
            } else {
                Log.e("LoginViewModel", "No se encontró un ID de usuario en DataStore")
            }
        }
    }

    fun onLoginViewChange(newIsLoginView: Boolean) {
        _isLoginView.value = newIsLoginView
    }
    fun onLoginChange(email: String, password: String){
        _email.value = email
        _password.value = password
    }


    fun loginUser(navigateTo: NavController) = viewModelScope.launch{
        try{
            // Crear el modelo con los valores actuales de email y password
           val user = LogInModel(_email.value, _password.value)
            Log.d("Login", "Iniciando sesión con: Email=${user.email}, Password=${user.password}")
            // Llamar al repositorio
            val result = repositoryLogin.loginUserRepository(user)
            if (result.isSuccess){
                val loginResponse = result.getOrNull()
                if(loginResponse != null){
                    Log.d("ViewMoidel: Login Exitoso", "Datos del usuario: ${loginResponse.data}")
                    dataStoreManager.guardarTokens(
                        loginResponse.data.token,
                        loginResponse.data.appToken,
                        loginResponse.data.user.rol,
                        loginResponse.data.user._id
                    )
                    _token.value = loginResponse.data.token
                    _authState.value = "Login exitoso"
                    val tokenDataStore = dataStoreManager.getAccessToken().first()
                    val tokenAppToken = dataStoreManager.getApplicationToken().first()
                    val permisosRol = dataStoreManager.getRole().first()
                    val idProfile = dataStoreManager.getIdProfile().first()

                    Log.d("ViewModel: Token","$tokenDataStore")
                    Log.d("ViewModel: AppToken","$tokenAppToken") 
                    Log.d("ViewModel: Rol","$permisosRol")
                    Log.d("ViewModel: Id Perfil", "$idProfile ")
                    navigateTo.navigate("main_screen")

                }else{
                    _authState.value = result.exceptionOrNull()?.localizedMessage ?: "Error desconocido"
                    Log.e("Login Error", "El servidor devolvió una respuesta exitosa, pero el cuerpo está vacío.  ${result.exceptionOrNull()?.localizedMessage}")
                }
            }else{
                val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Error desconocido"
                Log.e("Login Error", "Error durante el login: $errorMessage")
                _authState.value = result.exceptionOrNull()?.localizedMessage ?: "Error desconocido"
            }
        }catch (e: Exception){
            _authState.value = e.localizedMessage ?: "Error de red"
            Log.e("Login Error", "Error inesperado: ${e.message}", e)
        }
    }

    fun changePassword(passwordRequest: PasswordRequest, navController: NavController) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Log para ver qué parámetros estamos enviando al repositorio
                Log.d("ChangePassword", "Requesting password change for: ${passwordRequest.email}")

                // Llamamos al repositorio con el objeto PasswordRequest
                val result = repositoryLogin.changePasswordRepository(passwordRequest)

                // Comprobamos si la operación fue exitosa
                if (result.isSuccess) {
                    Log.d("ChangePassword", "Contraseña cambiada exitosamente")
                    _authState.value = "Contraseña cambiada"
                    navController.navigate("login_screen")
                } else {
                    // Si no fue exitoso, mostramos el mensaje de error
                    _authState.value = result.exceptionOrNull()?.localizedMessage ?: "Error desconocido"
                    Log.e("ChangePassword Error", "Fallo en cambio de contraseña: ${result.exceptionOrNull()?.localizedMessage}")
                }
            } catch (e: Exception) {
                // Si ocurre una excepción durante el proceso, mostramos el mensaje
                _authState.value = e.localizedMessage ?: "Error inesperado"
                Log.e("ChangePassword Error", "Error: ${e.message}", e)
            } finally {
                // Finalmente, establecemos que la carga ha terminado
                _isLoading.value = false
            }
        }
    }

    fun checkAuthStatus(){
        viewModelScope.launch {
            _isCheckingToken.value = true

            dataStoreManager.getAccessToken().collect{ tokenAlmacenado ->
                Log.d("AuthStatus", "Access Token: $tokenAlmacenado")
                if(!tokenAlmacenado.isNullOrEmpty()){
                    Log.d("CheckAuthStatus","Token valido encontrado")
                    _token.value = tokenAlmacenado
                    _authState.value = "Token valido encontrado"
                }else{
                    Log.d("CheckAuthStatus","No hay token valido")
                    _token.value = null
                    _authState.value = "No hay token valido"
                }
                _isCheckingToken.value = false

            }
        }
    }
}