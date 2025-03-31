package dam.intermodular.app.adquisiciones.api

import dam.intermodular.app.adquisiciones.model.Adquisicion
import dam.intermodular.app.adquisiciones.model.NuevaAdquisicion
import retrofit2.Response
import retrofit2.http.*

interface AdquisicionService {
    @GET("Adquisicion/getAll")
    suspend fun getAllAdquisiciones(): AdquisicionResponse

    @PUT("Adquisicion/modificarAdquisicion/{id}")
    suspend fun updateAdquisicion(@Path("id") id: String, @Body adquisicion: Adquisicion): Response<Adquisicion>

    @POST("Adquisicion/crearAdquisicion")
    suspend fun createAdquisicion(@Body adquisicion: NuevaAdquisicion): AdquisicionResponse

    // Endpoint para confirmar o cancelar adquisición
    @POST("Adquisicion/confirmarOcancelarAdquisicion")
    suspend fun confirmarOcancelarAdquisicion(@Body data: ConfirmarCancelarData): Response<AdquisicionResponse>
}

data class ConfirmarCancelarData(
    val _id: String,
    val id_prod: String,
    val tarjetaPuntos: Number,
    val accion: String // Puede ser "Confirmada" o "Cancelada"
)