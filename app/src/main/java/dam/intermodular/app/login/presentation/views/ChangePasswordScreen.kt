package dam.intermodular.app.login.presentation.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dam.intermodular.app.login.presentation.viewModel.LoginViewModel
import dam.intermodular.app.verifyProfile.data.PasswordRequest

@Composable
fun ChangePasswordScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {

    // Campos para nueva contraseña, confirmación y email de la cuenta a cambiar
    var emailToChange by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cambiar Contraseña", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para el email de la cuenta cuya contraseña se cambiará
        OutlinedTextField(
            value = emailToChange,
            onValueChange = { emailToChange = it },
            label = { Text("Email de la cuenta") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para la nueva contraseña
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para confirmar la nueva contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Nueva Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de error si las contraseñas no coinciden o algún otro error
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }

        // Botón para realizar el cambio de contraseña
        Button(
            onClick = {
                if (newPassword == confirmPassword) {
                    // Crear el objeto PasswordRequest con el email de la cuenta a cambiar y la nueva contraseña
                    val passwordRequest = PasswordRequest(email = emailToChange, password = newPassword)

                    // Llamar a la función changePassword del ViewModel pasando el objeto PasswordRequest
                    viewModel.changePassword(passwordRequest, navController)
                } else {
                    errorMessage = "Las contraseñas no coinciden"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar Contraseña")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para volver al login
        TextButton(onClick = { navController.navigate("login_screen") }) {
            Text("Volver al Login")
        }
    }
}