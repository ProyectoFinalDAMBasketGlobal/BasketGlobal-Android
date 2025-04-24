package dam.intermodular.app.productos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.intermodular.app.verifyProfile.presentation.viewModel.VerifyProfileViewModel

@Composable
fun ProfileScreen(viewModel: VerifyProfileViewModel, navigateTo: NavController) {
    // Observamos el estado de los valores
    val nombre by viewModel.nombre.collectAsState()
    val apellido by viewModel.apellido.collectAsState()
    val dni by viewModel.dni.collectAsState()
    val date by viewModel.date.collectAsState()
    val ciudad by viewModel.ciudad.collectAsState()
    val sexo by viewModel.sexo.collectAsState()
    val tarjetaPuntos by viewModel.tarjetaPuntos.collectAsState()

    // Comprobamos si los valores del perfil están disponibles antes de mostrar la interfaz
    if (nombre.isNotEmpty() && apellido.isNotEmpty() && dni.isNotEmpty() && date.isNotEmpty() && ciudad.isNotEmpty() && sexo.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Perfil del Usuario",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Información del usuario
            val infoStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(text = "Nombre: $nombre", style = infoStyle)
            Text(text = "Apellido: $apellido", style = infoStyle)
            Text(text = "DNI: $dni", style = infoStyle)
            Text(text = "Fecha de Nacimiento: $date", style = infoStyle)
            Text(text = "Ciudad: $ciudad", style = infoStyle)
            Text(text = "Sexo: $sexo", style = infoStyle)
            Text(text = "Tarjeta de Puntos: $tarjetaPuntos", style = infoStyle)

            Spacer(modifier = Modifier.height(24.dp))

            // Botón con estilo
            Button(
                onClick = { navigateTo.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),  // Color de fondo
                    contentColor = Color.White   // Color del texto
                )
            ) {
                Text(
                    text = "Volver",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    } else {
        // Si los valores aún están vacíos, mostramos un mensaje de carga
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Cargando perfil...", fontSize = 18.sp)
        }
    }
}
