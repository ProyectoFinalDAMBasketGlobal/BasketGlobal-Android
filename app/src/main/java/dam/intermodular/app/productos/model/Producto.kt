package dam.intermodular.app.productos.model

data class Producto(
    val _id: String,
    val nombre: String,
    val categoria: String,
    val marca: String,
    val esImportado: Boolean,
    val origen: String,
    val descripcion: String,
    val precio: Double,
    val precio_original: Double?,
    val tieneOferta: Boolean,
    val stock: Int,
    val estado: Boolean,
    val imagenBase64: String
)