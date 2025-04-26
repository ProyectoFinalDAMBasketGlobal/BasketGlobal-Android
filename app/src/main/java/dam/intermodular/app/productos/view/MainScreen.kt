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
                    .align(Alignment.CenterHorizontally)
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
        "Balones baloncesto",
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
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(69.dp),
                    containerColor = Color.Black // Fondo negro como en tu ejemplo
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround, // Espaciado entre íconos
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController.navigate("main_screen") }) {
                                Icon(
                                    Icons.Filled.Home,
                                    contentDescription = "Home",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White // Íconos blancos
                                )
                            }

                            IconButton(onClick = { navController.navigate("adquisiciones_screen") }) {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Historial",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }

                            IconButton(onClick = {
                                Toast.makeText(
                                    context,
                                    "¡BIENVENIDOS A NIGHT DAYS!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }

                            IconButton(onClick = { navController.navigate("favorites_screen") }) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = "Favorite",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }

                            IconButton(onClick = {
                                if (idUsuario.isNotEmpty()) {
                                    navController.navigate("profile_screen/$idUsuario")
                                } else {
                                    Log.e("Navigation Error", "El idUsuario está vacío o nulo.")
                                }
                            }) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 5.dp),
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

                Spacer(modifier = Modifier.height(15.dp))

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
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
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
                                                    val encodedNombre = encodeForNav(producto.nombre)
                                                    val encodedDescripcion = encodeForNav(producto.descripcion)
                                                    val encodedImagenBase64 = encodeForNav(producto.imagenBase64)
                                                    val formattedPrecio = String.format("%.2f", producto.precio)
                                                    //val encodedStock = encodeForNav(producto.stock.toString())
                                                    val encodedStock = URLEncoder.encode(
                                                        producto.stock.toString(),
                                                        StandardCharsets.UTF_8.toString()
                                                    )
                                                    val encodedPreviousScreen = encodeForNav("main_screen")
                                                    val roomId = producto._id

                                                    navController.navigate("producto_details_screen/$roomId/$encodedNombre/$encodedDescripcion/$formattedPrecio/$encodedStock/$encodedImagenBase64/$encodedPreviousScreen")

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

fun encodeForNav(value: String): String {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20")
}
