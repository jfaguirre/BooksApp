package com.example.booksapp.ui.pantallas.detalleObra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.local.AccionesBiblioteca
import com.example.booksapp.datos.local.AccionesBibliotecaProvisional
import com.example.booksapp.datos.modelos.DetalleObraModelo
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import com.example.booksapp.datos.repositorios.LibroRepositorio
import com.example.booksapp.datos.repositorios.LibroRepositorioImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel que gestiona la carga del detalle de una obra, obras del autor y guardado en biblioteca,
 * manteniendo sincronizados favoritos y biblioteca personal en Room.
 */
class DetalleObraViewModel(
    private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
    private val bibliotecaRepositorio: BibliotecaRepositorio? = null,
    private val accionesBiblioteca: AccionesBiblioteca = AccionesBibliotecaProvisional.instancia
) : ViewModel() {

    private val _estadoUi = MutableStateFlow<DetalleObraEstadoUi>(DetalleObraEstadoUi.Cargando)
    val estadoUi: StateFlow<DetalleObraEstadoUi> = _estadoUi.asStateFlow()

    private var idObraActual: String = ""

    /**
     * Carga la información completa de la obra a partir de su ID (ej. "OL27479W").
     */
    fun cargarDetalleObra(idObra: String) {
        val idLimpio = idObra.removePrefix("/works/").removePrefix("works/")
        idObraActual = idLimpio
        _estadoUi.value = DetalleObraEstadoUi.Cargando

        viewModelScope.launch {
            try {
                val resultado = repositorio.obtenerDetalleObra(idLimpio)
                resultado.onSuccess { obra ->
                    val repo = bibliotecaRepositorio
                    val guardado = if (repo != null) {
                        repo.estaGuardado(idLimpio) || repo.estaGuardado(obra.claveCompleta)
                    } else {
                        accionesBiblioteca.esLibroGuardado(idLimpio)
                    }
                    val libroEnBd = if (repo != null) {
                        repo.obtenerLibroPorId(idLimpio) ?: repo.obtenerLibroPorId(obra.claveCompleta)
                    } else null
                    val esFavorito = libroEnBd?.esFavorito ?: false

                    _estadoUi.value = DetalleObraEstadoUi.Exito(
                        obra = obra,
                        estaGuardado = guardado,
                        esFavorito = esFavorito
                    )
                }.onFailure { error ->
                    val esSinInternet = error is IOException
                    _estadoUi.value = DetalleObraEstadoUi.Error(
                        mensaje = error.localizedMessage ?: "Error al cargar la obra",
                        esErrorRed = esSinInternet
                    )
                }
            } catch (httpEx: HttpException) {
                _estadoUi.value = DetalleObraEstadoUi.Error(
                    mensaje = "Error del servidor Open Library (HTTP ${httpEx.code()})",
                    esErrorRed = false
                )
            } catch (ioEx: IOException) {
                _estadoUi.value = DetalleObraEstadoUi.Error(
                    mensaje = "Sin conexión a internet",
                    esErrorRed = true
                )
            } catch (excepcion: Exception) {
                _estadoUi.value = DetalleObraEstadoUi.Error(
                    mensaje = excepcion.localizedMessage ?: "Error inesperado",
                    esErrorRed = false
                )
            }
        }
    }

    /**
     * Alterna el estado de favorito de la obra actual sincronizándolo con Room.
     */
    fun alternarFavorito() {
        val estado = _estadoUi.value
        if (estado is DetalleObraEstadoUi.Exito) {
            val nuevoEstado = !estado.esFavorito
            _estadoUi.update {
                (it as DetalleObraEstadoUi.Exito).copy(esFavorito = nuevoEstado)
            }
            val libroParaGuardar = convertirObraALibroModelo(estado.obra, nuevoEstado)
            val repo = bibliotecaRepositorio
            if (repo != null) {
                viewModelScope.launch {
                    repo.alternarFavorito(libroParaGuardar)
                }
            }
        }
    }

    /**
     * Guarda la obra actual en la biblioteca local mediante Room o mecanismo provisional.
     */
    fun guardarEnBiblioteca(obra: DetalleObraModelo) {
        viewModelScope.launch {
            val estado = _estadoUi.value
            val esFav = (estado as? DetalleObraEstadoUi.Exito)?.esFavorito ?: false
            val libroParaGuardar = convertirObraALibroModelo(obra, esFav).copy(esGuardado = true)

            val repo = bibliotecaRepositorio
            if (repo != null) {
                repo.guardarLibro(libroParaGuardar)
            } else {
                accionesBiblioteca.guardarLibro(libroParaGuardar)
            }

            if (estado is DetalleObraEstadoUi.Exito) {
                _estadoUi.update {
                    (it as DetalleObraEstadoUi.Exito).copy(estaGuardado = true)
                }
            }
        }
    }

    private fun convertirObraALibroModelo(obra: DetalleObraModelo, esFavorito: Boolean): LibroModelo {
        return LibroModelo(
            id = obra.claveCompleta.ifEmpty { "/works/${obra.id}" },
            titulo = obra.titulo,
            autores = obra.nombresAutores,
            descripcion = obra.descripcion,
            urlPortada = obra.urlPortada,
            editorial = obra.editorial,
            anioPublicacion = obra.anioPublicacion?.toIntOrNull(),
            temas = obra.temas,
            clavesAutores = obra.clavesAutores,
            calificacion = obra.calificacion ?: 0.0,
            esGuardado = true,
            esFavorito = esFavorito
        )
    }

    /**
     * Reintenta cargar la obra actual si ocurrió un error previo.
     */
    fun reintentar() {
        if (idObraActual.isNotEmpty()) {
            cargarDetalleObra(idObraActual)
        }
    }

    /**
     * Factoría para instanciar [DetalleObraViewModel] con dependencias Room.
     */
    class Factory(
        private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
        private val bibliotecaRepositorio: BibliotecaRepositorio
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetalleObraViewModel(
                repositorio = repositorio,
                bibliotecaRepositorio = bibliotecaRepositorio
            ) as T
        }
    }
}
