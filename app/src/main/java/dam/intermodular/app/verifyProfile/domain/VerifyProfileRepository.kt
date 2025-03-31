package dam.intermodular.app.verifyProfile.domain

import android.util.Log
import dam.intermodular.app.core.api.ApiService
import dam.intermodular.app.verifyProfile.data.Cliente
import dam.intermodular.app.verifyProfile.data.ClienteRequest
import dam.intermodular.app.verifyProfile.data.RegisterClientResponse
import dam.intermodular.app.verifyProfile.presentation.model.VerifyProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class VerifyProfileRepository @Inject constructor(
    private val apiService: ApiService,
){
    suspend fun sendVerifyProfileRepository(
        user: VerifyProfileModel,
        picture: MultipartBody.Part?
    ) : Result<RegisterClientResponse>{
        return  try {

            Log.d("Repostory ON","Entro")
            val idUsuario = user.idUsuario.toRequestBody("text/plain".toMediaTypeOrNull())
            val nombre = user.nombre.toRequestBody("text/plain".toMediaTypeOrNull())
            val apellido = user.apellido.toRequestBody("text/plain".toMediaTypeOrNull())
            val rol = user.rol.toRequestBody("text/plain".toMediaTypeOrNull())
            val dni = user.dni.toRequestBody("text/plain".toMediaTypeOrNull())
            val date = user.date.toRequestBody("text/plain".toMediaTypeOrNull())
            val ciudad = user.ciudad.toRequestBody("text/plain".toMediaTypeOrNull())
            val sexo = user.sexo.toRequestBody("text/plain".toMediaTypeOrNull())
            val tarjetaPuntos = user.tarjetaPuntos.toString().toRequestBody("text/plain".toMediaTypeOrNull())


            Log.d("API_CALL", "idUsuario: ${idUsuario.contentType()} - ${idUsuario.contentLength()}")
            Log.d("API_CALL", "nombre: ${nombre.contentType()} - ${nombre.contentLength()}")
            Log.d("API_CALL", "apellido: ${apellido.contentType()} - ${apellido.contentLength()}")
            Log.d("API_CALL", "Imagen: ${picture?.body?.contentType()} - ${picture?.body?.contentLength()}")
            val response = apiService.apiRegisterProfile(idUsuario, nombre, apellido, dni,rol, date, ciudad, sexo, tarjetaPuntos, picture)
            if(response.isSuccessful){
                Log.d("Repository: VerifyProfile","${response.body()}")
                val registerClientResponse = response.body()

                if(registerClientResponse != null){
                    Result.success(registerClientResponse)
                }else{
                    Log.e("VerificationCode Error", "Respuesta vacía recibida del servidor")
                    Result.failure(Exception("Respuesta vacía recidida del seervidor"))
                }
            }else{
                Log.e("VerifyProfile Error", "Error de respuesta del servidor: Código ${response.code()}, Mensaje: ${response.message()}")
                Result.failure(Exception("Respuesta vacía recibido del servidor"))
            }
        }catch (e: Exception){
            Log.e("VerifyProfile Error", "Error en la llamada a la API: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(id: String): Result<VerifyProfileModel> {
        Log.d("API", "ID de usuario enviado: $id")

        // Crear el objeto ClienteRequest con el idUsuario
        val clienteRequest = ClienteRequest(_id = id)

        return try {
            // Realizamos la llamada POST con el objeto ClienteRequest
            val response = apiService.getCliente(clienteRequest)

            // Verificamos si la respuesta fue exitosa
            if (response.isSuccessful) {
                Log.d("API", "Respuesta exitosa: ${response.body()}")

                // Comprobamos si la respuesta no es nula
                response.body()?.let { cliente: Cliente -> // Especificamos explícitamente el tipo
                    Log.d("API", "Datos del cliente obtenidos: $cliente")

                    // Construimos el perfil
                    val perfil = VerifyProfileModel(
                        idUsuario = cliente.idUsuario,
                        nombre = cliente.nombre,
                        apellido = cliente.apellido,
                        rol = cliente.rol,
                        dni = cliente.dni,
                        date = cliente.date,
                        ciudad = cliente.ciudad,
                        sexo = cliente.sexo,
                        tarjetaPuntos = cliente.tarjetaPuntos
                    )

                    // Retornamos el perfil con éxito
                    Result.success(perfil)
                } ?: run {
                    // Si el body de la respuesta es nulo
                    Log.e("API", "Respuesta vacía")
                    Result.failure(Exception("Respuesta vacía"))
                }
            } else {
                // Si la respuesta no fue exitosa
                Log.e("API", "Error en la API: ${response.message()}")
                Result.failure(Exception("Error en la API: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Manejo de excepción
            Log.e("API", "Excepción al obtener perfil: ${e.message}")
            Result.failure(e)
        }
    }

}