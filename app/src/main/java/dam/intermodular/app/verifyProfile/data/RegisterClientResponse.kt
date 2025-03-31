package dam.intermodular.app.verifyProfile.data

import kotlinx.serialization.SerialName
import java.util.Date

data class RegisterClientResponse(
    val status : String,
    val message : String,
    val data : RegisterClientData
)

data class RegisterClientData(
    val user : SaverUser
)

data class SaverUser (
    val _id: String,
    val idUsuario: String,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val rol: String,
    val date: String,
    val ciudad: String,
    val sexo: String,
    val registro: Date,
    val rutaFoto: String,
    val baja: Date,
)

data class ClienteRequest(
    val _id: String
)

data class PasswordRequest(
    val email: String,
    val password: String
)

data class Cliente(
    @SerialName("_id") val _id: String,
    @SerialName("idUsuario") val idUsuario: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("apellido") val apellido: String,
    @SerialName("dni") val dni: String,
    @SerialName("rol") val rol: String,
    @SerialName("date") val date: String,
    @SerialName("ciudad") val ciudad: String,
    @SerialName("sexo") val sexo: String,
    @SerialName("tarjetaPuntos") val tarjetaPuntos: Number,
    @SerialName("rutaFoto") val rutaFoto: String,
    @SerialName("registro") val registro: Date
)

data class Usuario(
    @SerialName("_id") val _id: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("registro") val registro: Date,
    @SerialName("verificationCode") val verificationCode: String,
    @SerialName("codeExpiresAt") val codeExpiresAt: Date,
    @SerialName("isVerified") val isVerified: Boolean,
    @SerialName("privileges") val privileges: Boolean,
    @SerialName("emailApp") val emailApp: String
)