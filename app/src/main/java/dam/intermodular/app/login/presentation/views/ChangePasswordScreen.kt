package dam.intermodular.app.login.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dam.intermodular.app.login.presentation.viewModel.LoginViewModel
import dam.intermodular.app.verifyProfile.data.PasswordRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {

    // Campos para nueva contraseña, confirmación y email de la cuenta a cambiar
    var emailToChange by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = dam.intermodular.app.R.drawable.fondo), // Replace with your image
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Cambiar contraseña",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el email de la cuenta cuya contraseña se cambiará
            OutlinedTextField(
                value = emailToChange,
                onValueChange = { emailToChange = it },
                label = { Text("Email de la cuenta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Black,
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                ),
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para la nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva Contraseña") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Black,
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                ),
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para confirmar la nueva contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Nueva Contraseña") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Black,
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color(0xFFFF9800),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                ),
                textStyle = TextStyle(color = Color.White)
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

            // Botón para volver al login
            Button(onClick = { navController.navigate("login_screen") }) {
                Text("Volver al Login")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
