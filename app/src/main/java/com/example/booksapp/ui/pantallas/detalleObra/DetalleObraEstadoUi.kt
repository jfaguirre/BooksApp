package com.example.booksapp.ui.pantallas.detalleObra

import com.example.booksapp.datos.modelos.DetalleObraModelo

/**
 * Estados de la interfaz para la pantalla de detalle de obra literaria.
 */
sealed interface DetalleObraEstadoUi {

    /**
     * Estado de carga inicial mientras se obtiene la obra de Open Library.
     */
    data object Cargando : DetalleObraEstadoUi

    /**
     * Estado exitoso con la información completa de la obra.
     */
    data class Exito(
        val obra: DetalleObraModelo,
        val esFavorito: Boolean = false,
        val estaGuardado: Boolean = false
    ) : DetalleObraEstadoUi

    /**
     * Estado cuando no se encuentra la obra solicitada.
     */
    data object Vacio : DetalleObraEstadoUi

    /**
     * Estado de error cuando falla la petición de red o el servidor.
     */
    data class Error(
        val mensaje: String,
        val esErrorRed: Boolean = false
    ) : DetalleObraEstadoUi
}
