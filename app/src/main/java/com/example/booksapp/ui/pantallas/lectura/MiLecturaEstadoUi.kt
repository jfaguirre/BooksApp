package com.example.booksapp.ui.pantallas.lectura

import com.example.booksapp.datos.modelos.LibroModelo

/**
 * Estados de la UI de la pantalla «Mi Lectura».
 */
sealed interface MiLecturaEstadoUi {

    /** Cargando datos iniciales desde la base de datos. */
    data object Cargando : MiLecturaEstadoUi

    /** Datos listos para mostrarse. */
    data class Exito(
        val todosLosLibros: List<LibroModelo> = emptyList(),
        val librosLeyendo: List<LibroModelo> = emptyList(),
        val librosPorLeer: List<LibroModelo> = emptyList(),
        val librosLeidos: List<LibroModelo> = emptyList(),
        val totalLibros: Int = 0,
        val totalLeyendo: Int = 0,
        val totalPorLeer: Int = 0,
        val totalLeidos: Int = 0,
        val mensajeSnackbar: String? = null
    ) : MiLecturaEstadoUi {

        /** Porcentaje de libros terminados respecto al total de la biblioteca. */
        val porcentajeTerminados: Float
            get() = if (totalLibros > 0) (totalLeidos.toFloat() / totalLibros.toFloat()) * 100f else 0f

        /** Indica si la biblioteca está completamente vacía. */
        val bibliotecaVacia: Boolean
            get() = totalLibros == 0
    }
}

