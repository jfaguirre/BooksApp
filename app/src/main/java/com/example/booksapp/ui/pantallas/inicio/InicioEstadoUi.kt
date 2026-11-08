package com.example.booksapp.ui.pantallas.inicio

import com.example.booksapp.datos.modelos.CategoriaModelo
import com.example.booksapp.datos.modelos.LibroModelo

/**
 * Representa los estados de la interfaz de la pantalla de Inicio siguiendo MVVM.
 */
sealed interface InicioEstadoUi {

    /**
     * Estado inicial cuando se están consultando los servicios de Open Library.
     */
    data object Cargando : InicioEstadoUi

    /**
     * Estado exitoso con los datos listos para mostrar en la interfaz.
     */
    data class Exito(
        val saludo: String = "¡Hola, Sofía!",
        val estaApiConectada: Boolean = true,
        val textoBusqueda: String = "",
        val categorias: List<CategoriaModelo> = emptyList(),
        val libroDelMomento: LibroModelo? = null,
        val librosFiccionYFantasia: List<LibroModelo> = emptyList(),
        val librosSciFiYDistopia: List<LibroModelo> = emptyList(),
        val tendenciasComunidad: List<LibroModelo> = emptyList()
    ) : InicioEstadoUi

    /**
     * Estado de error cuando falla la carga y no se pudo obtener información.
     */
    data class Error(val mensaje: String) : InicioEstadoUi
}
