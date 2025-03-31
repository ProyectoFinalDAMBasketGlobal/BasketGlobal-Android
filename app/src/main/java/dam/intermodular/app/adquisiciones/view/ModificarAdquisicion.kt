package dam.intermodular.app.adquisiciones.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dam.intermodular.app.adquisiciones.model.Adquisicion
import dam.intermodular.app.adquisiciones.viewmodel.AdquisicionesViewModel
import dam.intermodular.app.ui.theme.Purple40
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarAdquisicionScreen(
    navController: NavHostController,
    adquisicion: Adquisicion,
    reservaViewModel: AdquisicionesViewModel = viewModel()
) {
    val context = LocalContext.current
    var fechaEntrada by remember { mutableStateOf(adquisicion.fechaAdquisicion) }
    var estado by remember { mutableStateOf(adquisicion.estado) }
    var isUpdating by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } // Para el menú desplegable

    val estados = listOf("Confirmada", "Pendiente", "Cancelada")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // DatePicker para Fecha de Entrada
    val calendarEntrada = Calendar.getInstance()
    val datePickerEntrada = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            fechaEntrada = dateFormat.format(calendarEntrada.apply {
                set(year, month, dayOfMonth)
            }.time)
        },
        calendarEntrada.get(Calendar.YEAR),
        calendarEntrada.get(Calendar.MONTH),
        calendarEntrada.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Modificar Adquisicion",
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
            Text(text = "ID: ${adquisicion.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Purple40)

            // Fecha de Entrada
            OutlinedTextField(
                value = fechaEntrada,
                onValueChange = { },
                label = { Text("Fecha de Entrada (yyyy-MM-dd)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerEntrada.show() }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Seleccionar Fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Menú desplegable para Estado de la Reserva
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = { },
                    label = { Text("Estado") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Desplegar menú")
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                estado = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isUpdating = true
                        reservaViewModel.updateAdquisicion(
                            adquisicion.id,
                            adquisicion.idUsu,
                            adquisicion.idPro,
                            fechaEntrada,
                            adquisicion.cantidad,
                            estado,
                            onSuccess = {
                                isUpdating = false
                                Toast.makeText(context, "Adquisicion actualizada con éxito", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = {
                                isUpdating = false
                                Toast.makeText(context, "Error al actualizar la reserva", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Guardar", color = Color.White)
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