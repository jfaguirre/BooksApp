package com.example.booksapp.ui.navegacion

/**
 * Rutas de navegación principales de la aplicación.
 */
sealed class Ruta(val ruta: String) {
    data object Splash : Ruta("splash")
    data object Principal : Ruta("principal")

    data object DetalleObra : Ruta("detalle_obra/{idObra}") {
        fun crearRuta(idObra: String): String {
            val idLimpio = idObra.removePrefix("/works/").removePrefix("works/")
            return "detalle_obra/$idLimpio"
        }
    }

    data object DetalleAutor : Ruta("detalle_autor/{idAutor}") {
        fun crearRuta(idAutor: String): String {
            val idLimpio = idAutor.removePrefix("/authors/").removePrefix("authors/")
            return "detalle_autor/$idLimpio"
        }
    }
}
