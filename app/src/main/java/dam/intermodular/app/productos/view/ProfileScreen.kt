package dam.intermodular.app.productos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.intermodular.app.verifyProfile.presentation.viewModel.VerifyProfileViewModel

@Composable
fun ProfileScreen(viewModel: VerifyProfileViewModel, navigateTo: NavController) {
    val nombre by viewModel.nombre.collectAsState()
    val apellido by viewModel.apellido.collectAsState()
    val dni by viewModel.dni.collectAsState()
    val date by viewModel.date.collectAsState()
    val formattedDate = date.split("-").reversed().joinToString("-")
    val ciudad by viewModel.ciudad.collectAsState()
    val sexo by viewModel.sexo.collectAsState()
    val tarjetaPuntos by viewModel.tarjetaPuntos.collectAsState()

    val allFieldsReady = listOf(nombre, apellido, dni, date, ciudad, sexo).all { it.isNotEmpty() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE0B2), // Claro arriba
                        Color(0xFFFFCC80)  // Más oscuro abajo
                    )
                )
            )
            .padding(16.dp)
    ) {
        if (allFieldsReady) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tarjeta de información del perfil
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Perfil del Usuario",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        val infoStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )

                        Text(text = "👤 Nombre: $nombre", style = infoStyle)
                        Text(text = "👤 Apellido: $apellido", style = infoStyle)
                        Text(text = "🆔 DNI: $dni", style = infoStyle)
                        Text(text = "🎂 Fecha de Nacimiento: $formattedDate", style = infoStyle)
                        Text(text = "🌆 Ciudad: $ciudad", style = infoStyle)
                        Text(text = "⚧ Sexo: $sexo", style = infoStyle)
                        Text(text = "💳 Tarjeta de Puntos: $tarjetaPuntos", style = infoStyle)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navigateTo.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Volver",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Cargando
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cargando perfil...",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
