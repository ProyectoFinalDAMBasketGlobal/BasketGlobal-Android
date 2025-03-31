package dam.intermodular.app.registro.data

data class RegisterResponse (
    val status : String,
    val message : String,
    val data : DataRegister
)

data class DataRegister(
    val email : String,
    val emailApp : String,
    val id : String,
    val privileges : Boolean
)