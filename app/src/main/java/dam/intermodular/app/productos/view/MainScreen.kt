package dam.intermodular.app.productos.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dam.intermodular.app.R
import dam.intermodular.app.productos.model.Producto
import dam.intermodular.app.productos.viewModel.ProductosViewModel
import dam.intermodular.app.verifyProfile.presentation.viewModel.VerifyProfileViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        val decodedString = Base64.getDecoder().decode(base64String)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        decodedBitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    productosViewModel: ProductosViewModel,
    onClick: () -> Unit
) {
    val isFavorite by productosViewModel.isFavorite(producto).collectAsState(initial = false)

    val imageBitmap = remember(producto.imagenBase64) {
        base64ToImageBitmap(producto.imagenBase64)
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(235.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Image(
                bitmap = imageBitmap ?: ImageBitmap.imageResource(R.drawable.room_image),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Box(modifier = Modifier.fillMaxSize()) {
                // Content before the heart icon
                Column(
                    modifier = Modifier
                        .padding(16.dp) // Adjust the padding as needed
                        .align(Alignment.TopStart) // Positioning the content normally (top left, etc.)
                ) {
                    // Titulo de la habitacion
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))  // Espacio entre nombre y precios

                    // Mostrar el precio original con texto, tachado
                    Text(
                        text = "Original: ${"%.2f".format(producto.precio_original ?: 0.0)}€", // Precio original en euros
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = Color.Red
                    )

                    // Mostrar el precio por noche con texto
                    Text(
                        text = "Actual: ${"%.2f".format(producto.precio)}€", // Precio por noche en euros
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))  // Espacio entre precios y opciones
                }

                // Heart icon positioned at the bottom-right corner
                IconButton(
                    onClick = { productosViewModel.toggleFavorite(producto) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(navController: NavHostController, productosViewModel: ProductosViewModel = viewModel()) {
    //val habitaciones by habitacionesViewModel.habitaciones.collectAsState()
    val filteredProductos by productosViewModel.filteredProductos.collectAsState()
    //val favoritos by habitacionesViewModel.favoritos.collectAsState()

    val verifyProfileViewModel: VerifyProfileViewModel = hiltViewModel()
    val idUsuario by verifyProfileViewModel.id.collectAsState()

    val showFilterDialog = remember { mutableStateOf(false) }
    var showNotification by remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(searchQuery.value) {
        productosViewModel.filterByName(searchQuery.value)
    }

    LaunchedEffect(Unit) {
        productosViewModel.loadProductos()
    }

    val categorias = listOf(
        "Productos recomendados",
        "Balones de baloncesto",
        "Ropa deportiva",
        "Merchandising",
        "Otros"
    )

    // Función que aplica los filtros
    val applyFilters =
        { priceRange: String?, capacity: String?, roomType: String?, origen: String? ->
            productosViewModel.applyFilters(priceRange, capacity, roomType, origen)
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        // Título que aparece en el TopAppBar
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Location",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                                )
                                Text(
                                    text = "BasketGlobal",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }

                            IconButton(
                                onClick = {
                                    showNotification = true
                                }, // Al hacer clic, se abre la notificación
                                modifier = Modifier
                                    .padding(end = 5.dp, top = 15.dp)
                                    .align(Alignment.TopEnd) // Posiciona el icono en la esquina superior derecha
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificaciones"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFFF9800) // Color naranja
                    )
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    IconButton(onClick = { navController.navigate("main_screen") }) {
                        Icon(Icons.Filled.Home, contentDescription = "Home")
                    }

                    IconButton(onClick = { navController.navigate("adquisiciones_screen") }) {
                        Icon(Icons.Filled.Search, contentDescription = "Historial")
                    }

                    IconButton(
                        onClick = {
                            // Mostrar mensaje (Toast) cada vez que se pulse el botón
                            Toast.makeText(
                                context,
                                "¡BIENVENIDOS A NIGHT DAYS!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Info")
                    }

                    IconButton(onClick = { navController.navigate("favorites_screen") }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
                    }

                    IconButton(onClick = {
                        if (idUsuario.isNotEmpty()) {  // Verifica que idUsuario no esté vacío
                            navController.navigate("profile_screen/$idUsuario") // Pasar el ID real
                        } else {
                            Log.e("Navigation Error", "El idUsuario está vacío o nulo.")
                        }
                    }) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                    }

                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 72.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        placeholder = { Text(text = "Buscar producto por tipo") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent)
                    )
                    IconButton(onClick = { showFilterDialog.value = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Filter")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredProductos.isEmpty()) {
                    Text(
                        text = "No hay productos disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        categorias.forEach { categoria ->
                            item {
                                Text(
                                    text = categoria,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            item {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val productosCategoria = filteredProductos.filter {
                                        categoria == "Productos recomendados" || it.categoria == categoria
                                    }
                                    items(productosCategoria) { producto ->
                                        ProductoCard(
                                            producto = producto,
                                            productosViewModel = productosViewModel,
                                            onClick = {
                                                try {
                                                    val encodedNombre = URLEncoder.encode(
                                                        producto.nombre,
                                                        StandardCharsets.UTF_8.toString()
                                                    )
                                                    val encodedDescripcion = URLEncoder.encode(
                                                        producto.descripcion,
                                                        StandardCharsets.UTF_8.toString()
                                                    )
                                                    val encodedImagenBase64 =
                                                        producto.imagenBase64.let {
                                                            URLEncoder.encode(
                                                                it,
                                                                StandardCharsets.UTF_8.toString()
                                                            )
                                                        } ?: ""
                                                    val formattedPrecio =
                                                        String.format("%.2f", producto.precio)
                                                    val stock = URLEncoder.encode(
                                                        producto.stock.toString(),
                                                        StandardCharsets.UTF_8.toString()
                                                    )
                                                    val roomId = producto._id
                                                    navController.navigate("producto_details_screen/$roomId/$encodedNombre/$encodedDescripcion/$formattedPrecio/$stock/$encodedImagenBase64/main_screen")
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                    Toast.makeText(
                                                        context,
                                                        "Error al abrir los detalles",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }

            // Mostrar el cuadro de diálogo de filtro
            FilterFragment(
                isVisible = showFilterDialog.value,
                onDismiss = { showFilterDialog.value = false },
                applyFilters = { priceRange, capacity, roomType, origen ->
                    applyFilters(priceRange, capacity, roomType, origen)
                    showFilterDialog.value =
                        false // Cerrar el diálogo después de aplicar los filtros
                }
            )

            // Mostrar el cuadro de notificaciones (en este caso son notificaciones permanentes)
            Notification(
                isVisible = showNotification,
                onDismiss = { showNotification = false }
            )

        }
    }
}
