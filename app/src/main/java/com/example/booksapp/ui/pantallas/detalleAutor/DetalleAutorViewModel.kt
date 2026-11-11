package com.example.booksapp.ui.pantallas.detalleAutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.repositorios.LibroRepositorio
import com.example.booksapp.datos.repositorios.LibroRepositorioImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel que gestiona la carga biográfica y el catálogo de obras del autor.
 */
class DetalleAutorViewModel(
    private val repositorio: LibroRepositorio = LibroRepositorioImpl()
) : ViewModel() {

    private val _estadoUi = MutableStateFlow<DetalleAutorEstadoUi>(DetalleAutorEstadoUi.Cargando)
    val estadoUi: StateFlow<DetalleAutorEstadoUi> = _estadoUi.asStateFlow()

    private var idAutorActual: String = ""

    /**
     * Carga el perfil biográfico y las obras del autor a partir de su ID (ej. "OL23919A").
     */
    fun cargarDetalleAutor(idAutor: String) {
        val idLimpio = idAutor.removePrefix("/authors/").removePrefix("authors/")
        idAutorActual = idLimpio
        _estadoUi.value = DetalleAutorEstadoUi.Cargando

        viewModelScope.launch {
            try {
                val resultado = repositorio.obtenerDetalleAutor(idLimpio)
                resultado.onSuccess { autor ->
                    _estadoUi.value = DetalleAutorEstadoUi.Exito(autor = autor)
                }.onFailure { error ->
                    val esSinInternet = error is IOException
                    _estadoUi.value = DetalleAutorEstadoUi.Error(
                        mensaje = error.localizedMessage ?: "Error al cargar autor",
                        esErrorRed = esSinInternet
                    )
                }
            } catch (httpEx: HttpException) {
                _estadoUi.value = DetalleAutorEstadoUi.Error(
                    mensaje = "Error del servidor Open Library (HTTP ${httpEx.code()})",
                    esErrorRed = false
                )
            } catch (ioEx: IOException) {
                _estadoUi.value = DetalleAutorEstadoUi.Error(
                    mensaje = "Sin conexión a internet",
                    esErrorRed = true
                )
            } catch (excepcion: Exception) {
                _estadoUi.value = DetalleAutorEstadoUi.Error(
                    mensaje = excepcion.localizedMessage ?: "Error inesperado",
                    esErrorRed = false
                )
            }
        }
    }

    /**
     * Reintenta la consulta del autor si falló previamente.
     */
    fun reintentar() {
        if (idAutorActual.isNotEmpty()) {
            cargarDetalleAutor(idAutorActual)
        }
    }
}
