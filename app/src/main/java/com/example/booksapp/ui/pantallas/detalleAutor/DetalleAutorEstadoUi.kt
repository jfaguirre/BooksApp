package com.example.booksapp.ui.pantallas.detalleAutor

import com.example.booksapp.datos.modelos.DetalleAutorModelo

/**
 * Estados de la interfaz para la pantalla de detalle de autor literario.
 */
sealed interface DetalleAutorEstadoUi {

    /**
     * Estado de carga inicial mientras se obtiene la biografía y obras del autor.
     */
    data object Cargando : DetalleAutorEstadoUi

    /**
     * Estado exitoso con el perfil biográfico y obras publicadas del autor.
     */
    data class Exito(
        val autor: DetalleAutorModelo
    ) : DetalleAutorEstadoUi

    /**
     * Estado cuando no se encuentra información del autor.
     */
    data object Vacio : DetalleAutorEstadoUi

    /**
     * Estado de error cuando falla la red o el servidor.
     */
    data class Error(
        val mensaje: String,
        val esErrorRed: Boolean = false
    ) : DetalleAutorEstadoUi
}

