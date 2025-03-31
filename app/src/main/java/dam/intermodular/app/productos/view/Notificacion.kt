package dam.intermodular.app.productos.view


import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun Notification(
    isVisible: Boolean,
    onDismiss: () -> Unit,
) {
    val notificaciones = listOf(
        "Gracias por confiar en nosotros y usar nuestros servicios.",
        "Oferta limitada: Algunas habitaciones cuentan con oferta.",
        "Actualización disponible en los próximos días.",
        "Verificación en dos pasos estará disponible muy pronto."
    )

    // Estado para el color de fondo cuando el ratón pasa por encima
    var hoveredIndex by remember { mutableIntStateOf(-1) }


    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Notificaciones", color = MaterialTheme.colorScheme.primary)},
            text = {
                Box(modifier = Modifier.height(500.dp)) { // Aumenta la altura del contenedor
                    Column(modifier = Modifier) {
                        notificaciones.forEachIndexed { index, notification ->

                            val borderColor = if (hoveredIndex == index) {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) // Color de borde cuando está hovered
                            } else {
                                Color(0xFFD1A7E3) // Morado claro como color de borde cuando no está hovered
                            }

                            Text(
                                text = notification,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(width = 2.dp, color = borderColor)
                                    .padding(5.dp)
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                hoveredIndex = if (event.changes.any { it.pressed }) {
                                                    index
                                                } else {
                                                    -1
                                                }
                                            }
                                        }
                                    },
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            // Espacio reducido entre los comentarios, puedes ajustarlo según sea necesario
                            Spacer(modifier = Modifier.height(15.dp)) // Espacio más pequeño entre comentarios
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
