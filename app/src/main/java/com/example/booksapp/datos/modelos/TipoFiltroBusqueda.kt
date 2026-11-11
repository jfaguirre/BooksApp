package com.example.booksapp.datos.modelos

/**
 * Filtros de búsqueda disponibles según el diseño de Stitch.
 */
enum class TipoFiltroBusqueda(val titulo: String) {
    TODOS("Todos"),
    TITULO("Por Título"),
    AUTOR("Por Autor"),
    CATEGORIA("Por Categoría")
}

