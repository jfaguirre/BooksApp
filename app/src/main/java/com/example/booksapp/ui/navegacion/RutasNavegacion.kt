package com.example.booksapp.ui.navegacion

/**
 * Rutas de navegación principales de la aplicación.
 */
sealed class Ruta(val ruta: String) {
    data object Splash : Ruta("splash")
    data object Principal : Ruta("principal")
    data object Lector : Ruta("lector")
}
