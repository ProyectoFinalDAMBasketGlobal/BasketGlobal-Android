package dam.intermodular.app.productos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FilterFragment(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    applyFilters: (String?, String?, String?, String?) -> Unit
) {
    // Crear estado para cada campo del filtro
    var priceRange by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var origen by remember { mutableStateOf("") }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Aplicar filtros") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Filtro de rango de precio
                    Text("Rango de precio:")
                    TextField(
                        value = priceRange,
                        onValueChange = { priceRange = it },
                        label = { Text("Escribe rango de precio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtro de capacidad
                    Text("Capacidad:")
                    TextField(
                        value = capacity,
                        onValueChange = { capacity = it },
                        label = { Text("Escribe capacidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtro de tipo de habitación
                    Text("Tipo de producto:")
                    TextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Escribe tipo de producto") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtro de opciones
                    Text("Origen:")
                    TextField(
                        value = origen,
                        onValueChange = { origen = it },
                        label = { Text("Escribe Origen") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    applyFilters(priceRange, capacity, type, origen)
                    onDismiss() // Cerrar el dialogo
                }) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterFragmentPreview() {
    FilterFragment(isVisible = true, onDismiss = {}, applyFilters = { _, _, _, _ -> })
}
