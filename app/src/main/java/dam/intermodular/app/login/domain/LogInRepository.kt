package dam.intermodular.app.login.domain

import android.util.Log
import dam.intermodular.app.core.api.ApiService
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.login.data.LoginResponse
import dam.intermodular.app.login.presentation.model.LogInModel
import dam.intermodular.app.verifyProfile.data.PasswordRequest
import javax.inject.Inject

class LogInRepository @Inject constructor (
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
){
    suspend fun loginUserRepository(logInModel: LogInModel): Result<LoginResponse> {
        return  try{
            val response = apiService.apiLogIn(logInModel)
            if (response.isSuccessful){
                Log.d("Repository: Api Response","${response.body()}")
                val loginResponse = response.body()
                if(loginResponse != null){
                    dataStoreManager.guardarTokens(
                        loginResponse.data.token,
                        loginResponse.data.appToken,
                        loginResponse.data.user.rol,
                        loginResponse.data.user._id
                    )
                    Result.success(loginResponse)
                }else{
                    Log.e("Login Error", "Respuesta vacía recibida del servidor")
                    Result.failure(Exception("Respuesta vacía recibido del servidor"))
                }
            }else{
                Log.e("Login Error", "Error de respuesta del servidor: Código ${response.code()}, Mensaje: ${response.message()}")
                Result.failure(Exception("Respuesta vacía recibido del servidor"))
            }
        }catch (e: Exception){
            Log.e("Login Error", "Error en la llamada a la API: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun changePasswordRepository(passwordRequest: PasswordRequest): Result<Unit> {
        return try {
            // Log para verificar que los parámetros se están pasando correctamente
            Log.d("ChangePasswordRepository", "Requesting password change for: ${passwordRequest.email}")

            // Llamar al servicio de la API con el objeto PasswordRequest
            val response = apiService.changePassword(passwordRequest)

            // Log de la respuesta de la API
            Log.d("ChangePasswordRepository", "API Response Code: ${response.code()}")

            // Verificar si la respuesta fue exitosa
            if (response.isSuccessful) {
                Log.d("ChangePasswordRepository", "Password change successful")
                Result.success(Unit)
            } else {
                // Si la respuesta no fue exitosa, logueamos el código de error
                Log.e("ChangePasswordRepository", "Error in password change: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error en el cambio de contraseña: Código ${response.code()}"))
            }
        } catch (e: Exception) {
            // Log de cualquier excepción que ocurra
            Log.e("ChangePasswordRepository", "Exception during password change: ${e.message}", e)
            Result.failure(e)
        }
    }

}