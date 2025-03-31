package dam.intermodular.app.productos.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dam.intermodular.app.productos.viewModel.ProductosViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun FavoritesScreen(navController: NavController, productosViewModel: ProductosViewModel) {
    val favoritos by productosViewModel.favoritos.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Productos Favoritos",
                        fontSize = 28.sp,
                        color = Color.Magenta,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("main_screen")
                    }) {
                        Icon(
                            modifier = Modifier.padding(start = 16.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF9800) // Color naranja
                )
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally) {
            if (favoritos.isEmpty()) {
                Text(text = "No hay productos favoritos.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(favoritos) { producto ->
                        ProductoCard(
                            producto = producto,
                            productosViewModel = productosViewModel,
                            onClick = {
                                try {
                                    val encodedNombre = URLEncoder.encode(producto.nombre ?: "Sin nombre", StandardCharsets.UTF_8.toString())
                                    val encodedDescripcion = URLEncoder.encode(producto.descripcion ?: "Sin descripción", StandardCharsets.UTF_8.toString())
                                    val encodedImagenBase64 = producto.imagenBase64.let {
                                        URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                                    } ?: ""
                                    val formattedPrecio = String.format("%.2f", producto.precio ?: 0.0)
                                    val stock = producto.stock ?: "0"
                                    val roomId = producto._id ?: "0"
                                    navController.navigate("producto_details_screen/$roomId/$encodedNombre/$encodedDescripcion/$formattedPrecio/$stock/$encodedImagenBase64/favorites_screen")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Error al abrir los detalles", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
