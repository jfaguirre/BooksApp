package com.example.booksapp.ui.pantallas.busqueda

import com.example.booksapp.datos.modelos.LibroModelo

/**
 * Estados de la interfaz para la pantalla de búsqueda de libros siguiendo MVVM.
 */
sealed interface BuscarLibrosEstadoUi {

    /**
     * Estado de carga mientras se consultan los servicios de Open Library.
     */
    data object Cargando : BuscarLibrosEstadoUi

    /**
     * Estado exitoso con las coincidencias encontradas en el catálogo.
     */
    data class Exito(
        val libros: List<LibroModelo>,
        val cantidadResultados: Int,
        val consultaActual: String = ""
    ) : BuscarLibrosEstadoUi

    /**
     * Estado cuando no se encontraron libros para el término consultado.
     */
    data class Vacio(
        val consulta: String = ""
    ) : BuscarLibrosEstadoUi

    /**
     * Estado de error cuando falla la petición de red o el servidor HTTP.
     */
    data class Error(
        val mensaje: String,
        val esErrorRed: Boolean = false
    ) : BuscarLibrosEstadoUi
}

