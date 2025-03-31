package dam.intermodular.app.productos.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Perfil del Usuario", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Nombre: $nombre", fontSize = 18.sp)
            Text(text = "Apellido: $apellido", fontSize = 18.sp)
            Text(text = "DNI: $dni", fontSize = 18.sp)
            Text(text = "Fecha de Nacimiento: $date", fontSize = 18.sp)
            Text(text = "Ciudad: $ciudad", fontSize = 18.sp)
            Text(text = "Sexo: $sexo", fontSize = 18.sp)
            Text(text = "Tarjeta de Puntos: $tarjetaPuntos", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navigateTo.popBackStack() }) {
                Text(text = "Volver")
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