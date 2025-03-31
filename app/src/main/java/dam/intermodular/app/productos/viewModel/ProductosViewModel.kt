package dam.intermodular.app.productos.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import dam.intermodular.app.core.api.ApiService
import dam.intermodular.app.core.dataStore.DataStoreManager
import dam.intermodular.app.productos.model.Producto
import javax.inject.Inject

@HiltViewModel
class ProductosViewModel @Inject constructor(
    application: Application,
    private val dataStoreManager: DataStoreManager,
    private val apiService: ApiService
) : AndroidViewModel(application) {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    private val _filteredProductos = MutableStateFlow<List<Producto>>(emptyList())
    private val _favoritos = MutableStateFlow<List<Producto>>(emptyList())

    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()
    val filteredProductos: StateFlow<List<Producto>> = _filteredProductos.asStateFlow()
    val favoritos: StateFlow<List<Producto>> = _favoritos.asStateFlow()

    init {
        loadProductos()
    }

    fun loadProductos() {
        viewModelScope.launch {
            try {
                val response = apiService.getProductos()
                if (response.isSuccessful) {
                    response.body()?.let { productos ->
                        // Filtramos las habitaciones cuyo estado es true
                        val productosFiltrados = productos.filter { it.estado }
                        _productos.value = productosFiltrados
                        _filteredProductos.value = productosFiltrados
                        loadFavoritos() // Solo ejecuta si hay datos
                    } ?: run {
                        Log.e("API_ERROR", "Respuesta vacía de la API")
                    }
                } else {
                    Log.e("API_ERROR", "Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Fallo al obtener habitaciones: ${e.localizedMessage}")
            }
        }
    }

    private fun loadFavoritos() {
        viewModelScope.launch {
            val favoritosIds = dataStoreManager.favoritos.first()
            _favoritos.value = _productos.value.filter { it._id in favoritosIds }
        }
    }

    fun toggleFavorite(producto: Producto) {
        viewModelScope.launch {
            dataStoreManager.toggleFavorite(producto._id)
            loadFavoritos()
        }
    }

    fun isFavorite(producto: Producto): StateFlow<Boolean> {
        return favoritos
            .map { favoritosList ->
                favoritosList.any { it._id == producto._id }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, false) // Valor inicial 'false'
    }

    fun filterByName(query: String) {
        _filteredProductos.update {
            if (query.isEmpty()) productos.value
            else productos.value.filter { it.nombre.startsWith(query, ignoreCase = true) }
        }
    }


    fun applyFilters(
        priceRange: String?,
        marca: String?,
        roomType: String?,
        origen: String?,
    ) {
        _filteredProductos.update { productos ->
            // Verificamos si hay filtros aplicados
            val isFiltering = priceRange != null || marca != null || roomType != null || origen != null

            if (!isFiltering) {
                // Si no hay filtros, mostrar todas las habitaciones
                return@update productos
            }

            // Filtrar las habitaciones
            val filtered = productos.filter { producto ->
                // Filtrar por precio (si el filtro es no nulo)
                val matchesPrice = priceRange?.let {
                    try {
                        val price = producto.precio.toInt()
                        val priceRangeParts = priceRange.split("-")
                        val minPrice = priceRangeParts[0].toIntOrNull() ?: 0
                        val maxPrice = priceRangeParts.getOrNull(1)?.toIntOrNull() ?: Int.MAX_VALUE
                        price in minPrice..maxPrice
                    } catch (e: Exception) {
                        false
                    }
                } ?: true // Si no hay filtro, pasa

                // Filtrar por capacidad (si el filtro es no nulo)
                val matchesCapacity = marca?.let {
                    producto.marca == marca
                } ?: true

                // Filtrar por tipo de habitación (si el filtro es no nulo)
                val matchesRoomType = roomType?.let {
                    producto.categoria.contains(roomType, ignoreCase = true)
                } ?: true

                // Filtrar por tipo de habitación (si el filtro es no nulo)
                val matchesOrigen = origen?.let {
                    producto.categoria.contains(origen, ignoreCase = true)
                } ?: true


                // Solo devolver habitaciones que coincidan con todos los filtros activos
                matchesPrice && matchesCapacity && matchesRoomType
            }

            // Verifica si no se encontraron habitaciones
            if (filtered.isEmpty()) {
                println("No rooms available for this filter.")
            }

            filtered
        }
    }
}
