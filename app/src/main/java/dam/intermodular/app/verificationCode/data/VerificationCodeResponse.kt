package dam.intermodular.app.verificationCode.data


data class VerificationCodeResponse (
    val status : String,
    val message : String,
    val data : DataVerificationResponse
)

data class DataVerificationResponse(
    val emailApp: String,
    val idUsuario: String,
    val temporalToken: String,
    val privileges: String
)