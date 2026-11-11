package com.example.booksapp.datos.modelos

/**
 * Modelo que representa una categoría o etiqueta temática de libros.
 */
data class CategoriaModelo(
    val id: String,
    val nombre: String,
    val claveMateriaApi: String,
    val estaSeleccionada: Boolean = false
) {
    companion object {
        /**
         * Clave especial de materia que representa la vista general sin filtro de categoría.
         */
        const val CLAVE_MATERIA_TODAS = "all"
    }
}
