package com.example.booksapp.datos.modelos

/**
 * Modelo de datos detallado de una obra literaria para su pantalla de detalle.
 */
data class DetalleObraModelo(
    val id: String,
    val claveCompleta: String,
    val titulo: String,
    val descripcion: String,
    val urlPortada: String,
    val anioPublicacion: String? = null,
    val temas: List<String> = emptyList(),
    val nombresAutores: List<String> = emptyList(),
    val clavesAutores: List<String> = emptyList(),
    val calificacion: Double? = null,
    val editorial: String = "",
    val esGuardado: Boolean = false,
    val obrasRelacionadasAutor: List<LibroModelo> = emptyList()
) {
    /**
     * Retorna los nombres de los autores formateados para visualización.
     */
    fun obtenerTextoAutores(): String {
        return if (nombresAutores.isEmpty()) "Autor Desconocido" else nombresAutores.joinToString(", ")
    }

    /**
     * Retorna el identificador del primer autor limpio de prefijos "/authors/".
     */
    fun obtenerClavePrimerAutor(): String {
        val clave = clavesAutores.firstOrNull() ?: ""
        return clave.removePrefix("/authors/").removePrefix("authors/")
    }
}

