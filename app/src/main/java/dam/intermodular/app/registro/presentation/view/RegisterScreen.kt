package dam.intermodular.app.registro.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.intermodular.app.login.presentation.composables.EmailTextFiel
import dam.intermodular.app.login.presentation.composables.PasswordTextField
import dam.intermodular.app.registro.presentation.viewModel.RegisterViewModel


@Composable
fun RegisterScreen(navigateTo: NavController,viewModel: RegisterViewModel){

    val email: String by viewModel.email.collectAsState()
    val password:  String by viewModel.password.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = dam.intermodular.app.R.drawable.fondo), // Replace with your image
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment =  Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Registrar",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Email",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            EmailTextFiel(email) { newEmail ->
                viewModel.onRegisterChange(newEmail, password)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            PasswordTextField (password) { newPassword ->
                viewModel.onRegisterChange(email,newPassword)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {  viewModel.registerUser(navigateTo) }
            ) {
                Text(
                    text = "Register",
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))


        }
    }
}
