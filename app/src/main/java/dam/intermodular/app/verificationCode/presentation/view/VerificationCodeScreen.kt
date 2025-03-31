package dam.intermodular.app.verificationCode.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.intermodular.app.core.navigation.VerificationData
import dam.intermodular.app.verificationCode.presentation.composables.VerificationTextFiel
import dam.intermodular.app.verificationCode.presentation.viewModel.VerificationCodeViewModel

@Composable
fun VerificationCodeScreen(verificationData: VerificationData,viewModel: VerificationCodeViewModel, navigateTo: NavController) {

    val code: String by viewModel.code.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Se ha enviado un código de verificación a... ", fontSize = 20.sp , softWrap = true)
        Text(text = "Email: ${verificationData.email}")
        Text(text = "ID Usuario: ${verificationData.idUser}")

        VerificationTextFiel(code){ newCode ->
            viewModel.onCodeChange(newCode)
        }

        Button(onClick = { viewModel.sendVerificationCode(navigateTo,verificationData.idUser,verificationData.emailApp) }) {
            Text("Confirmar")
        }
    }
}