package dam.intermodular.app.verificationCode.presentation.model

data class VerificationCodeModel(
    val emailApp: String,
    val verificationCode: String,
    val _id: String
)