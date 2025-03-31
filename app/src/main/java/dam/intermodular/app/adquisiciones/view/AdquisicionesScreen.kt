package dam.intermodular.app.adquisiciones.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.gson.Gson
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.productos.model.Producto
import dam.intermodular.app.productos.viewModel.ProductosViewModel
import dam.intermodular.app.adquisiciones.model.Adquisicion
import dam.intermodular.app.adquisiciones.viewmodel.AdquisicionesViewModel
import dam.intermodular.app.ui.theme.Purple40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdquisicionesScreen(
    navController: NavHostController,
    adquisicionViewModel: AdquisicionesViewModel = hiltViewModel(),
    productosViewModel: ProductosViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) } // Inicializar DataStoreManager
    val userId by dataStoreManager.getIdProfile().collectAsState(initial = null) // Obtener el ID del usuario logueado
    val adquisiciones by adquisicionViewModel.adquisiciones.collectAsStateWithLifecycle()
    val productos by productosViewModel.productos.collectAsStateWithLifecycle() // Asumiendo que tienes un productoViewModel

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "Ejecutando AdquisicionesScreen()")
        adquisicionViewModel.getAllAdquisiciones()
    }

    // Filtrar adquisiciones solo para el usuario actual
    val adquisicionesUsuario = adquisiciones.filter { it.idUsu == userId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Historial de Adquisiciones",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF9800) // Color naranja
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .size(56.dp) // Tamaño del círculo
                    .background(Color(0xFF757575), shape = CircleShape) // Círculo de color gris claro
            ) {
                IconButton(
                    onClick = { navController.navigate("main_screen") },
                    modifier = Modifier.align(Alignment.Center) // Centra el ícono dentro del círculo
                ) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = Color(0xFFFF9800) // El color del ícono será blanco
                    )
                }
            }
        },
        containerColor = Color(0xFFD3D3D3)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (userId == null) {
                Text(
                    text = "Cargando usuario...",
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentSize()
                )
            } else if (adquisicionesUsuario.isEmpty()) {
                Text(
                    text = "No has realizado ninguna adquisicion",
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth().wrapContentSize()
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(adquisicionesUsuario) { adquisicion ->
                        val producto = productos.find { it._id == adquisicion.idPro } // Busca el producto con idPro de la adquisición

                        if (producto != null) {
                            AdquisicionItem(
                                adquisicion = adquisicion,
                                producto = producto, // Pasa el producto aquí
                                onInfoClick = {
                                    val adquisicionJson = Gson().toJson(adquisicion)
                                    navController.navigate("infoAdquisicion/$adquisicionJson")
                                },
                                onModifyClick = {
                                    val adquisicionJson = Gson().toJson(adquisicion)
                                    navController.navigate("modificarAdquisicion/$adquisicionJson")
                                },
                                onConfirmClick = { id, idProd, puntos ->
                                    adquisicionViewModel.confirmarOcancelarAdquisicion(id,
                                        idProd.toString(), puntos, "Confirmada",
                                        onSuccess = { /* Mostrar mensaje de éxito */ },
                                        onError = { /* Mostrar mensaje de error */ })
                                },
                                onCancelClick = { id, idProd, puntos ->
                                    adquisicionViewModel.confirmarOcancelarAdquisicion(id,
                                        idProd.toString(), puntos, "Cancelada",
                                        onSuccess = { /* Mostrar mensaje de éxito */ },
                                        onError = { /* Mostrar mensaje de error */ })
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}

fun calcularPuntos(gastoTotal: Double): Int {
    return when {
        gastoTotal <= 100 -> 120  // Hasta 100€ => 120 puntos
        gastoTotal in 101.0..200.0 -> 250  // Entre 101€ y 200€ => 250 puntos
        else -> 315  // Más de 200€ => 315 puntos
    }
}

@Composable
fun AdquisicionItem(adquisicion: Adquisicion, producto: Producto, onInfoClick: (String) -> Unit, onModifyClick: (String) -> Unit, onConfirmClick: (String, Any?, Int) -> Unit, onCancelClick: (String, Any?, Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(5.dp, Color.DarkGray, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Adquisicion ID: ${adquisicion.id}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Purple40
            )
            Text(
                text = "Producto: ${producto.nombre}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Purple40
            )
            val gastoTotal = adquisicion.cantidad * producto.precio
            Text(
                text = "Gasto Total: $gastoTotal €",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Purple40
            )
            Text(
                text = "Estado: ${adquisicion.estado}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = when (adquisicion.estado) {
                    "Pendiente" -> Color.Blue
                    "Confirmada" -> Color.Green
                    "Cancelada" -> Color.Red
                    else -> Color.Gray
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botón de información
                IconButton(
                    onClick = { onInfoClick(adquisicion.id) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "Información", tint = Purple40)
                }

                // Botón para confirmar (solo si está "Pendiente")
                IconButton(
                    onClick = {
                        // Calcular gasto total
                        val gastoTotal = adquisicion.cantidad * producto.precio

                        // Calcular puntos
                        val puntos = calcularPuntos(gastoTotal)

                        // Llamar a la función para confirmar la adquisición y actualizar los puntos
                        onConfirmClick(adquisicion.id, adquisicion.idPro, puntos)
                    },
                    enabled = adquisicion.estado == "Pendiente",
                    modifier = Modifier
                        .size(48.dp)
                        .border(BorderStroke(3.dp, Purple40))
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Confirmar",
                        tint = if (adquisicion.estado == "Pendiente") Color.Green else Color.Gray
                    )
                }

                // Botón para cancelar (solo si está "Pendiente")
                IconButton(
                    onClick = { onCancelClick(adquisicion.id, adquisicion.idPro, 0) },
                    enabled = adquisicion.estado == "Pendiente",
                    modifier = Modifier
                        .size(48.dp)
                        .border(BorderStroke(3.dp, Purple40))
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cancelar",
                        tint = if (adquisicion.estado == "Pendiente") Color.Red else Color.Gray
                    )
                }

                // Botón para modificar (solo si está "Pendiente")
                IconButton(
                    onClick = { onModifyClick(adquisicion.id) },
                    enabled = adquisicion.estado == "Pendiente",
                    modifier = Modifier
                        .size(48.dp)
                        .border(BorderStroke(3.dp, Purple40))
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Modificar",
                        tint = if (adquisicion.estado == "Pendiente") Color.Gray else Color.Gray
                    )
                }

            }
        }
    }
}