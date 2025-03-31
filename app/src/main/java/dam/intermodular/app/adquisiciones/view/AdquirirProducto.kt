package dam.intermodular.app.adquisiciones.view

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.adquisiciones.viewmodel.AdquisicionesViewModel
import dam.intermodular.app.login.presentation.viewModel.LoginViewModel
import dam.intermodular.app.ui.theme.Purple40
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdquirirProductoScreen(
    navController: NavHostController,
    productoId: String,
    productoNombre: String,
    precio: String,
    stockProducto: Int,
    usuarioId: String,
    reservaViewModel: AdquisicionesViewModel = hiltViewModel(),
    userViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }


    var cantidad by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val userId by dataStoreManager.getIdProfile().collectAsState(initial = null)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaEntrada = dateFormat.format(Date())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Adquirir Producto",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Purple40)
            )
        },
        containerColor = Color(0xFFF2F2F2) // Fondo gris claro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información de la habitación en recuadros con bordes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "ID Usuario: $userId",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "ID Producto: $productoId",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Nombre: $productoNombre",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(text = "Precio: $precio€",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Stock: $stockProducto",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            OutlinedTextField(
                value = fechaEntrada,
                onValueChange = { }, // No permite cambios
                label = { Text("Fecha de Entrada (yyyy-MM-dd)") },
                readOnly = true, // Evita edición manual
                enabled = false, // Deshabilita el campo completamente
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cantidad,
                onValueChange = { newValue ->
                    cantidad = newValue // Actualiza el valor ingresado
                    val parsedValue = newValue.toIntOrNull() // Intenta convertirlo a Int

                    errorMessage = if (parsedValue == null || parsedValue !in 1..stockProducto) {
                        "Debe ser un número entre 1 y $stockProducto"
                    } else {
                        "" // Borra el error si el valor es válido
                    }
                },
                label = { Text("Número de huéspedes (Máximo: $stockProducto)") },
                singleLine = true,
                isError = errorMessage.isNotEmpty(), // Muestra error si hay mensaje
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // Asegura entrada numérica
                ),
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (fechaEntrada.isEmpty() || stockProducto <= 0) {
                            Toast.makeText(
                                context,
                                "Todos los campos son obligatorios",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (userId == null) {
                            Toast.makeText(
                                context,
                                "No se pudo obtener el ID del usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isSaving = true

                        reservaViewModel.createAdquisicion(
                            id = "",
                            idUsu = userId.toString(),
                            idProd = productoId,
                            fechaEntrada = fechaEntrada,
                            cantidad = cantidad.toInt(),
                            estado = "Pendiente",
                            onSuccess = {
                                isSaving = false
                                Toast.makeText(
                                    context,
                                    "Adquisicion creada con éxito",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("adquisiciones_screen")
                            },
                            onError = { errorMsg ->
                                isSaving = false
                                Toast.makeText(
                                    context,
                                    "Error al crear la adquisicion: $errorMsg",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Confirmar Adquisicion", color = Color.White)
                    }
                }

                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}