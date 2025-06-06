package dam.intermodular.app.productos.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dam.intermodular.app.R


@Composable
fun ProductoDetailsFragment(
    navController: NavHostController,
    roomId: String,
    roomName: String,
    roomDescription: String,
    roomPrice: String,
    stock: Int,
    roomImage: String,
    previousScreen: String
) {
    //val context = LocalContext.current
    val defaultImage = painterResource(id = R.drawable.room_image) // Imagen por defecto
    val imageBitmap = base64ToImageBitmap(roomImage)

    Column(
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
            .padding(25.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                when (previousScreen) {
                    "main_screen" -> navController.navigate("main_screen") {
                        popUpTo("main_screen") {
                            inclusive = true
                        }
                    }

                    "favorites_screen" -> navController.navigate("favorites_screen") {
                        popUpTo("favorites_screen") {
                            inclusive = true
                        }
                    }

                    else -> navController.popBackStack()
                }
            }) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Detalles del Producto",
                fontSize = 25.sp,
                color = Color.Magenta,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Imagen de la habitación
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Imagen del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = defaultImage,
                contentDescription = "Imagen por defecto del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append("Nombre del producto: ")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(roomName)
                }
            },
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Descripción
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append("Descripción: ")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(roomDescription)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Precio de la habitación
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append("Precio: ")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("$roomPrice €")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Precio de la habitación
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append("Stock: ")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("$stock productos")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val usuarioId = "A-002"
                navController.navigate("adquirir_producto/$roomId/$roomName/$roomPrice/$stock/$usuarioId")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Comprar ahora",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho
                    .padding(5.dp) // Espacio alrededor
                    .height(50.dp) // Altura del texto
            )
        }
    }
}
