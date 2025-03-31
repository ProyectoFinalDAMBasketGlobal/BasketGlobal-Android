package dam.intermodular.app.registro.domain

import android.util.Log
import dam.intermodular.app.core.api.ApiService
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.registro.data.RegisterResponse
import dam.intermodular.app.registro.presentation.model.RegisterModel
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun registerUserRepository(registerModel: RegisterModel):Result<RegisterResponse>{
        return try {
            val response = apiService.apiAddUser(registerModel)

            if(response.isSuccessful){

                Log.d("Repository: Api Response","${response.body()}")
                val responseSuccess = response.body()
                if(responseSuccess != null){
                    Result.success(responseSuccess)
                }else{
                    Log.e("Register Error","Respuesta vacía recibida del servidor")
                    Result.failure(Exception("Respuesta vacía recibido del servidor"))
                }

            }else{
                Log.e("Register Error",
                        "Error de respuesta del servidor: Código ${response.code()}," +
                            " Mensaje: ${response.message()}, " +
                            "Content ${response.errorBody()}")
                Result.failure(Exception("Respuesta vacía recidida del servidor"))
            }

        }catch (e:Exception){
            Log.e("Register error","Error en la llamada a la API: ${e.message}\", e")
            Result.failure(e)
        }
    }


}