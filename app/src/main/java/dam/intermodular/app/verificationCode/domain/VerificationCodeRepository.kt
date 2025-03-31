package dam.intermodular.app.verificationCode.domain

import android.util.Log
import dam.intermodular.app.core.api.ApiService
import dam.intermodular.app.verificationCode.data.VerificationCodeResponse
import dam.intermodular.app.verificationCode.presentation.model.VerificationCodeModel
import javax.inject.Inject

class VerificationCodeRepository @Inject constructor(
    private val apiService : ApiService
) {

    suspend fun sendVerificationCodeRepository(verificationCodeModel: VerificationCodeModel):Result<VerificationCodeResponse>{
        return try {
            val response = apiService.apiVerificationCode(verificationCodeModel)
            if (response.isSuccessful){
                Log.d("Repository: Api Response","${response.body()}")
                val verificationCodeResponse = response.body()
                if(verificationCodeResponse != null){
                    Result.success(verificationCodeResponse)
                }else{
                    Log.e("VerificationCode Error", "Respuesta vacía recibida del servidor")
                    Result.failure(Exception("Respuesta vacía recidida del seervidor"))
                }
            }
            else{
                Log.e("VerificationCode Error", "Error de respuesta del servidor: Código ${response.code()}, Mensaje: ${response.message()}")
                Result.failure(Exception("Respuesta vacía recibido del servidor"))
            }
        }catch (e: Exception){
            Log.e("VerificationCode Error", "Error en la llamada a la API: ${e.message}", e)
            Result.failure(e)
        }
    }

}