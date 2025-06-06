package dam.intermodular.app.productos.view


import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

// Generador de notificaciones promocionales y persuasivas
fun generarNotificacion(): String {

    val promociones = listOf(
        "Nuevas camisetas retro de leyendas del baloncesto.",
        "Balones oficiales con descuento.",
        "Zapatillas de alto rendimiento recién llegadas.",
        "Merchandising exclusivo de equipos NBA.",
        "Sudaderas y gorras con diseños únicos"
    )

    val llamados = listOf(
        "Explora ahora en la tienda.",
        "No dejes pasar esta oportunidad.",
        "Equípate como un profesional.",
        "La oferta expira pronto."
    )

    return "${promociones.random()} ${llamados.random()}"
}

@Composable
fun Notification(
    isVisible: Boolean,
    onDismiss: () -> Unit,
) {
    // Regenerar notificaciones cada vez que se muestra el diálogo
    val notificaciones = remember { mutableStateListOf<String>() }
    var wasVisible by remember { mutableStateOf(false) }
    val hasClosed = remember { mutableStateOf(false) }

    // Este efecto detecta cambios en la visibilidad
    LaunchedEffect(isVisible) {
        if (!isVisible && notificaciones.isEmpty()) {
            // La ventana se cerró y estaba vacía
            hasClosed.value = true
        }
        if (isVisible && notificaciones.isEmpty()) {
            // La ventana se abrió y no hay notificaciones
            if (hasClosed.value) {
                repeat(3) { notificaciones.add(generarNotificacion()) }
                hasClosed.value = false
            }
        }
        // Actualizamos el estado anterior
        wasVisible = isVisible
    }

    val hoveredIndex by remember { mutableIntStateOf(-1) }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Anuncios", color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                Box(modifier = Modifier.height(400.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        notificaciones.toList().forEachIndexed { index, notification ->
                            val borderColor = if (hoveredIndex == index) {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            } else {
                                Color(0xFFD1A7E3)
                            }

                            var offsetX by remember { mutableFloatStateOf(0f) }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .border(
                                        width = 2.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .pointerInput(notification) {
                                        detectHorizontalDragGestures { _, dragAmount ->
                                            offsetX += dragAmount
                                            if (kotlin.math.abs(offsetX) > 100f) {
                                                notificaciones.remove(notification)
                                            }
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                            ) {
                                Text(
                                    text = notification,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        if (notificaciones.isEmpty()) {
                            Text(
                                "¡Has eliminado todas las notificaciones!",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cerrar")
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
