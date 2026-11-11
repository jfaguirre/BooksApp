package com.example.booksapp.datos.modelos

/**
 * Modelo de datos con la información biográfica y catálogo del autor.
 */
data class DetalleAutorModelo(
    val id: String,
    val claveCompleta: String,
    val nombre: String,
    val biografia: String,
    val urlFoto: String,
    val fechaNacimiento: String? = null,
    val fechaMuerte: String? = null,
    val obras: List<LibroModelo> = emptyList()
) {
    /**
     * Retorna el periodo de años de vida formateado.
     */
    fun obtenerPeriodoVida(): String {
        return when {
            fechaNacimiento != null && fechaMuerte != null -> "$fechaNacimiento — $fechaMuerte"
            fechaNacimiento != null -> "Nacido(a) en $fechaNacimiento"
            else -> ""
        }
    }
}

