package com.example.booksapp.ui.pantallas.lectura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla «Mi Lectura».
 * Gestiona la consulta y administración de la actividad de lectura del usuario.
 */
class MiLecturaViewModel(
    private val repositorio: BibliotecaRepositorio
) : ViewModel() {

    private val _estadoUi = MutableStateFlow<MiLecturaEstadoUi>(MiLecturaEstadoUi.Cargando)
    val estadoUi: StateFlow<MiLecturaEstadoUi> = _estadoUi.asStateFlow()

    init {
        observarDatosLectura()
    }

    /**
     * Combina los flows reactivos de Room para construir el estado de UI completo.
     */
    private fun observarDatosLectura() {
        viewModelScope.launch {
            combine(
                repositorio.observarBiblioteca(),
                repositorio.observarPorEstado(EstadoLectura.LEYENDO),
                repositorio.observarPorEstado(EstadoLectura.POR_LEER),
                repositorio.observarPorEstado(EstadoLectura.LEIDO)
            ) { todos, leyendo, porLeer, leidos ->
                val estadoActual = (_estadoUi.value as? MiLecturaEstadoUi.Exito)
                MiLecturaEstadoUi.Exito(
                    todosLosLibros = todos,
                    librosLeyendo = leyendo,
                    librosPorLeer = porLeer,
                    librosLeidos = leidos,
                    totalLibros = todos.size,
                    totalLeyendo = leyendo.size,
                    totalPorLeer = porLeer.size,
                    totalLeidos = leidos.size,
                    mensajeSnackbar = estadoActual?.mensajeSnackbar
                )
            }.collect { nuevoEstado ->
                _estadoUi.value = nuevoEstado
            }
        }
    }

    // ─── ACCIONES DE USUARIO ─────────────────────────────────────────────────

    /**
     * Cambia el estado de lectura de un libro a LEYENDO (iniciar lectura).
     */
    fun iniciarLectura(libroId: String, tituloLibro: String) {
        viewModelScope.launch {
            repositorio.cambiarEstadoLectura(libroId, EstadoLectura.LEYENDO)
            mostrarSnackbar("\"$tituloLibro\" movido a lecturas en curso")
        }
    }

    /**
     * Marca un libro como LEIDO (completado).
     */
    fun marcarComoLeido(libroId: String, tituloLibro: String) {
        viewModelScope.launch {
            repositorio.cambiarEstadoLectura(libroId, EstadoLectura.LEIDO)
            mostrarSnackbar("\"$tituloLibro\" marcado como completado")
        }
    }

    /**
     * Cambia el estado de un libro a POR_LEER.
     */
    fun marcarComoPorLeer(libroId: String, tituloLibro: String) {
        viewModelScope.launch {
            repositorio.cambiarEstadoLectura(libroId, EstadoLectura.POR_LEER)
            mostrarSnackbar("\"$tituloLibro\" movido a próximas lecturas")
        }
    }

    /**
     * Limpia el mensaje del snackbar después de mostrarlo.
     */
    fun limpiarSnackbar() {
        val estadoActual = _estadoUi.value as? MiLecturaEstadoUi.Exito ?: return
        _estadoUi.update { estadoActual.copy(mensajeSnackbar = null) }
    }

    private fun mostrarSnackbar(mensaje: String) {
        val estadoActual = _estadoUi.value as? MiLecturaEstadoUi.Exito ?: return
        _estadoUi.update { estadoActual.copy(mensajeSnackbar = mensaje) }
    }

    // ─── FACTORY ─────────────────────────────────────────────────────────────

    /**
     * Factory para instanciar el ViewModel inyectando el repositorio.
     */
    class Factory(private val repositorio: BibliotecaRepositorio) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MiLecturaViewModel::class.java)) {
                return MiLecturaViewModel(repositorio) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}

