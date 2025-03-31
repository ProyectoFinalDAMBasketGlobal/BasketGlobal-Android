package dam.intermodular.app.core.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Register

@Serializable
object MainScreen

@Serializable
object Home

@Serializable
data class Detail(val name: String)

@Serializable
data class Settings(val info: SettingsInfo)

@Parcelize
@Serializable
data class SettingsInfo(
    val name: String,
    val id: Int,
    val darkMode:Boolean
) : Parcelable

@Serializable
data class VerificationCode(val verificationData: VerificationData)

@Parcelize
@Serializable
data class VerificationData(
    val email: String,
    val emailApp: String,
    val idUser: String
) : Parcelable

@Serializable
data class VerifyProfile(val verifyData: VerifyData)

@Parcelize
@Serializable
data class VerifyData(
    val emailApp: String,
    val idUser: String,
) : Parcelable