package dam.intermodular.app.adquisiciones.api

import dam.intermodular.app.adquisiciones.model.Adquisicion

class AdquisicionResponse (
    val status: String,
    val message: String,
    val adquisiciones: List<Adquisicion>
)