package dam.intermodular.app.adquisiciones.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam.intermodular.app.adquisiciones.api.ConfirmarCancelarData
import kotlinx.coroutines.launch
import dam.intermodular.app.adquisiciones.model.Adquisicion
import dam.intermodular.app.adquisiciones.api.RetrofitClient
import dam.intermodular.app.adquisiciones.model.NuevaAdquisicion
import dam.intermodular.app.verifyProfile.domain.VerifyProfileRepository
import dam.intermodular.app.verifyProfile.presentation.viewModel.VerifyProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdquisicionesViewModel @Inject constructor(
    private val verifyProfileRepository: VerifyProfileRepository  // Inyectar el VerifyProfileViewModel
) : ViewModel() {
    private val _adquisiciones = MutableStateFlow<List<Adquisicion>>(emptyList())
    val adquisiciones: StateFlow<List<Adquisicion>> = _adquisiciones


    init {
        getAllAdquisiciones()
    }

    fun getAllAdquisiciones() {
        viewModelScope.launch {
            try {
                Log.d("API_CALL", "Llamando a la API para obtener adquisiciones...")
                val response = RetrofitClient.adquisicionApiService.getAllAdquisiciones()
                Log.d("API_RESPONSE", "Adquisiciones recibidas: $response")

                if (response.adquisiciones.isNotEmpty()) {
                    Log.d("API_RESPONSE", "Lista de adquisiciones obtenida: ${response.adquisiciones}")
                    _adquisiciones.value = response.adquisiciones
                } else {
                    Log.d("API_RESPONSE", "La API devolvió una lista vacía")
                }
            } catch (e: Exception) {
                Log.e("ReservaViewModel", "Error al obtener reservas: ${e.message}", e)
            }
        }
    }

    fun updateAdquisicion(
        adquisicionId: String,
        idUser: String,
        idProd: String,
        fechaEntrada: String,
        cantidad: Int,
        estado: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val updatedAdquisicion = Adquisicion(adquisicionId, idUser, idProd, fechaEntrada, cantidad, estado)
                Log.d("UPDATE_ADQUISICION", "Enviando actualización: $updatedAdquisicion")

                val response = RetrofitClient.adquisicionApiService.updateAdquisicion(adquisicionId, updatedAdquisicion)
                Log.d("UPDATE_ADQUISICION_API", "Respuesta de la API: $response")

                if (response.isSuccessful) {
                    Log.d("UPDATE_ADQUISICION_SUCCESS", "Adquisicion actualizada correctamente: ${response.body()}")
                    onSuccess()
                } else {
                    Log.e("UPDATE_ADQUISICION_ERROR", "Error en la API: ${response.code()} - ${response.message()}")
                    onError()
                }
            } catch (e: Exception) {
                Log.e("UPDATE_ADQUISICION_ERROR", "Excepción en la actualización: ${e.message}", e)
                onError()
            }
        }
    }

    fun createAdquisicion(
        id: String,
        idUsu: String,
        idProd: String,
        fechaEntrada: String,
        cantidad: Int,
        estado: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            Log.d("USER_ID", "ID del usuario obtenido: $idUsu") // Depuración

            try {
                val nuevaAdquisicion = NuevaAdquisicion(id, idUsu, idProd, fechaEntrada, cantidad, estado)
                val response = RetrofitClient.adquisicionApiService.createAdquisicion(nuevaAdquisicion)
                Log.d("API_RESPONSE", "Adquisicion creada: $response")
                onSuccess()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al crear adquisicion: ${e.message}", e)
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    fun confirmarOcancelarAdquisicion(
        id: String,
        idProd: String,
        puntos: Int,
        accion: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val data = ConfirmarCancelarData(id, idProd, puntos, accion)
                val response = RetrofitClient.adquisicionApiService.confirmarOcancelarAdquisicion(data)

                if (response.isSuccessful) {
                    Log.d("API_RESPONSE", "Adquisición ${accion.lowercase(Locale.ROOT)} exitosamente: ${response.body()}")

                    onSuccess()
                } else {
                    Log.e("API_ERROR", "Error en la API: ${response.code()} - ${response.message()}")
                    onError("Error en la API")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Excepción en la confirmación o cancelación: ${e.message}", e)
                onError(e.message ?: "Error desconocido")
            }
        }
    }

}