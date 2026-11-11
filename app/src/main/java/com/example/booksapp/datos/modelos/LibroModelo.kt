package com.example.booksapp.datos.modelos

/**
 * Modelo de datos que representa un libro dentro de la aplicación.
 */
data class LibroModelo(
    val id: String,
    val titulo: String,
    val autores: List<String> = emptyList(),
    val descripcion: String = "",
    val urlPortada: String = "",
    val anioPublicacion: Int? = null,
    val calificacion: Double = 4.8,
    val cantidadCalificaciones: String = "(12.4k)",
    val etiqueta: String? = null,
    val lectores: String? = null,
    val esFavorito: Boolean = false,
    val editorial: String = "",
    val clavesAutores: List<String> = emptyList(),
    val temas: List<String> = emptyList(),
    val tieneCalificacion: Boolean = true,
    val esGuardado: Boolean = false
) {
    /**
     * Retorna los nombres de los autores formateados como una sola cadena de texto.
     */
    fun obtenerTextoAutores(): String {
        return if (autores.isEmpty()) "Autor Desconocido" else autores.joinToString(", ")
    }

    /**
     * Retorna el identificador limpio de la obra sin el prefijo "/works/".
     */
    fun obtenerIdLimpio(): String {
        return id.removePrefix("/works/").removePrefix("works/")
    }
}
