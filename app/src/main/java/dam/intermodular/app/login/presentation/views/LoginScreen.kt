package dam.intermodular.app.login.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.intermodular.app.core.navigation.Register
import dam.intermodular.app.login.presentation.composables.EmailTextFiel
import dam.intermodular.app.login.presentation.composables.PasswordTextField
import dam.intermodular.app.login.presentation.viewModel.LoginViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LoginScreen(navigationTo : NavController, viewModel: LoginViewModel){


    val email: String by viewModel.email.collectAsState()
    val password:  String by viewModel.password.collectAsState()
    //val authState by viewModel.authState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = dam.intermodular.app.R.drawable.fondo), // Replace with your image
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment =  Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "BasketGlobal",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "Email",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            EmailTextFiel(email) { newEmail ->
                viewModel.onLoginChange(newEmail, password)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            PasswordTextField (password) { newPassword ->
                viewModel.onLoginChange(email,newPassword)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {  viewModel.loginUser(navigationTo) }
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Olvidé mi contraseña",
                fontSize = 16.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { navigationTo.navigate("change_password_screen") }
            )
            //Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "¿No tienes cuenta? Regístrate aquí",
                fontSize = 16.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { navigationTo.navigate(Register) }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}