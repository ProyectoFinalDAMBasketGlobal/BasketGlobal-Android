package dam.intermodular.app.login.presentation.model

data class LogInModel (
    val email : String,
    val password : String,
    val appType : String = "android"
)