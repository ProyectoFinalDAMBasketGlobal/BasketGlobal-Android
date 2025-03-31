package dam.intermodular.app.adquisiciones.model

import com.google.gson.annotations.SerializedName

data class NuevaAdquisicion(
    @SerializedName("_id") val id: String,
    @SerializedName("id_usu") val idUsu: String,
    @SerializedName("id_prod") val idPro: String,
    @SerializedName("fecha_adquisicion") val fechaAdquisicion: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("estado_adquisicion") val estado: String
)