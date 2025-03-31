package dam.intermodular.app.verifyProfile.presentation.model

data class VerifyProfileModel(
    val idUsuario : String,
    val nombre : String,
    val apellido : String,
    val rol : String,
    val dni : String,
    val date : String,
    val ciudad : String,
    val sexo : String,
    val tarjetaPuntos: Number
    //val picture: Queda definir el tipo de dato
)