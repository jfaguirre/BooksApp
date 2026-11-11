package com.example.booksapp.ui.pantallas.mislibros

import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.LibroModelo

/**
 * Pestaña activa en la pantalla de Mis Libros.
 */
enum class PestañaBiblioteca {
    TODOS, FAVORITOS, POR_LEER, LEYENDO, LEIDO
}

/**
 * Estados de la UI de la pantalla Mis Libros.
 */
sealed interface MisLibrosEstadoUi {

    /** Cargando datos iniciales desde la base de datos. */
    data object Cargando : MisLibrosEstadoUi

    /** Datos listos para mostrarse. */
    data class Exito(
        val pestañaActual: PestañaBiblioteca = PestañaBiblioteca.TODOS,
        val todosLosLibros: List<LibroModelo> = emptyList(),
        val favoritos: List<LibroModelo> = emptyList(),
        val porLeer: List<LibroModelo> = emptyList(),
        val leyendo: List<LibroModelo> = emptyList(),
        val leidos: List<LibroModelo> = emptyList(),
        val totalLibros: Int = 0,
        val totalLeidos: Int = 0,
        val totalLeyendo: Int = 0,
        val libroEliminado: LibroModelo? = null,  // Para snackbar de deshacer
        val mensajeSnackbar: String? = null
    ) : MisLibrosEstadoUi {

        /** Lista que se muestra según la pestaña activa. */
        val librosMostrados: List<LibroModelo>
            get() = when (pestañaActual) {
                PestañaBiblioteca.TODOS -> todosLosLibros
                PestañaBiblioteca.FAVORITOS -> favoritos
                PestañaBiblioteca.POR_LEER -> porLeer
                PestañaBiblioteca.LEYENDO -> leyendo
                PestañaBiblioteca.LEIDO -> leidos
            }
    }
}

