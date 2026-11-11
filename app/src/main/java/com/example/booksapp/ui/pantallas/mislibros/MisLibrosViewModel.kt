package com.example.booksapp.ui.pantallas.mislibros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla "Mis Libros".
 * Gestiona la biblioteca personal del usuario: guardar, eliminar, favoritos y estados de lectura.
 */
class MisLibrosViewModel(
    private val repositorio: BibliotecaRepositorio
) : ViewModel() {

    private val _estadoUi = MutableStateFlow<MisLibrosEstadoUi>(MisLibrosEstadoUi.Cargando)
    val estadoUi: StateFlow<MisLibrosEstadoUi> = _estadoUi.asStateFlow()

    init {
        observarBiblioteca()
    }

    /**
     * Combina todos los flows reactivos de Room en un único estado de UI.
     */
    private fun observarBiblioteca() {
        viewModelScope.launch {
            combine(
                repositorio.observarBiblioteca(),
                repositorio.observarFavoritos(),
                repositorio.observarPorEstado(EstadoLectura.POR_LEER),
                repositorio.observarPorEstado(EstadoLectura.LEYENDO),
                repositorio.observarPorEstado(EstadoLectura.LEIDO)
            ) { todos, favoritos, porLeer, leyendo, leidos ->
                val estadoActual = (_estadoUi.value as? MisLibrosEstadoUi.Exito)
                MisLibrosEstadoUi.Exito(
                    pestañaActual = estadoActual?.pestañaActual ?: PestañaBiblioteca.TODOS,
                    todosLosLibros = todos,
                    favoritos = favoritos,
                    porLeer = porLeer,
                    leyendo = leyendo,
                    leidos = leidos,
                    totalLibros = todos.size,
                    totalLeidos = leidos.size,
                    totalLeyendo = leyendo.size
                )
            }.collect { nuevoEstado ->
                _estadoUi.value = nuevoEstado
            }
        }
    }

    // ─── ACCIONES DE USUARIO ─────────────────────────────────────────────────

    /**
     * Cambia la pestaña activa en la pantalla de Mis Libros.
     */
    fun seleccionarPestaña(pestaña: PestañaBiblioteca) {
        val estadoActual = _estadoUi.value as? MisLibrosEstadoUi.Exito ?: return
        _estadoUi.update { estadoActual.copy(pestañaActual = pestaña) }
    }

    /**
     * Guarda un libro en la biblioteca con el estado indicado.
     */
    fun guardarLibro(libro: LibroModelo, estado: EstadoLectura = EstadoLectura.POR_LEER) {
        viewModelScope.launch {
            repositorio.guardarLibro(libro, estado)
            mostrarSnackbar("\"${libro.titulo}\" añadido a tu biblioteca")
        }
    }

    /**
     * Elimina un libro de la biblioteca.
     */
    fun eliminarLibro(libro: LibroModelo) {
        viewModelScope.launch {
            repositorio.eliminarLibro(libro.id)
            val estadoActual = _estadoUi.value as? MisLibrosEstadoUi.Exito ?: return@launch
            _estadoUi.update {
                estadoActual.copy(
                    libroEliminado = libro,
                    mensajeSnackbar = "\"${libro.titulo}\" eliminado de tu biblioteca"
                )
            }
        }
    }

    /**
     * Restaura un libro eliminado (deshacer).
     */
    fun restaurarLibroEliminado() {
        val estadoActual = _estadoUi.value as? MisLibrosEstadoUi.Exito ?: return
        val libro = estadoActual.libroEliminado ?: return
        viewModelScope.launch {
            repositorio.guardarLibro(libro, EstadoLectura.POR_LEER)
            _estadoUi.update { estadoActual.copy(libroEliminado = null, mensajeSnackbar = null) }
        }
    }

    /**
     * Alterna el estado de favorito de un libro.
     */
    fun alternarFavorito(libro: LibroModelo) {
        viewModelScope.launch {
            val nuevoEstado = repositorio.alternarFavorito(libro)
            val mensaje = if (nuevoEstado) "Añadido a favoritos" else "Eliminado de favoritos"
            mostrarSnackbar(mensaje)
        }
    }

    /**
     * Cambia el estado de lectura de un libro.
     */
    fun cambiarEstadoLectura(libro: LibroModelo, nuevoEstado: EstadoLectura) {
        viewModelScope.launch {
            repositorio.cambiarEstadoLectura(libro.id, nuevoEstado)
            val etiqueta = when (nuevoEstado) {
                EstadoLectura.POR_LEER -> "Por leer"
                EstadoLectura.LEYENDO -> "Leyendo"
                EstadoLectura.LEIDO -> "Leído"
            }
            mostrarSnackbar("\"${libro.titulo}\" marcado como $etiqueta")
        }
    }

    /**
     * Limpia el mensaje del snackbar después de mostrarlo.
     */
    fun limpiarSnackbar() {
        val estadoActual = _estadoUi.value as? MisLibrosEstadoUi.Exito ?: return
        _estadoUi.update { estadoActual.copy(mensajeSnackbar = null, libroEliminado = null) }
    }

    private fun mostrarSnackbar(mensaje: String) {
        val estadoActual = _estadoUi.value as? MisLibrosEstadoUi.Exito ?: return
        _estadoUi.update { estadoActual.copy(mensajeSnackbar = mensaje) }
    }

    // ─── FACTORY ─────────────────────────────────────────────────────────────

    /**
     * Factory para instanciar el ViewModel inyectando el repositorio.
     */
    class Factory(private val repositorio: BibliotecaRepositorio) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MisLibrosViewModel::class.java)) {
                return MisLibrosViewModel(repositorio) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}

