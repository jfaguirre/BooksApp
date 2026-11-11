package com.example.booksapp.ui.pantallas.busqueda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.local.AccionesBiblioteca
import com.example.booksapp.datos.local.AccionesBibliotecaProvisional
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.modelos.TipoFiltroBusqueda
import com.example.booksapp.datos.repositorios.LibroRepositorio
import com.example.booksapp.datos.repositorios.LibroRepositorioImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * ViewModel que gestiona la lógica de presentación, filtros y búsqueda de libros con debounce.
 */
@OptIn(FlowPreview::class)
class BuscarLibrosViewModel(
    private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
    private val accionesBiblioteca: AccionesBiblioteca = AccionesBibliotecaProvisional.instancia
) : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("Gabriel García Márquez")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _filtroSeleccionado = MutableStateFlow(TipoFiltroBusqueda.TODOS)
    val filtroSeleccionado: StateFlow<TipoFiltroBusqueda> = _filtroSeleccionado.asStateFlow()

    private val _estadoUi = MutableStateFlow<BuscarLibrosEstadoUi>(BuscarLibrosEstadoUi.Cargando)
    val estadoUi: StateFlow<BuscarLibrosEstadoUi> = _estadoUi.asStateFlow()

    val idsLibrosGuardados: StateFlow<Set<String>> = accionesBiblioteca.idsLibrosGuardados

    private var busquedaJob: Job? = null

    init {
        // Debounce de 500 ms para el campo de búsqueda
        viewModelScope.launch {
            _textoBusqueda
                .debounce(500L)
                .distinctUntilChanged()
                .collect { consulta ->
                    if (consulta.isNotBlank()) {
                        ejecutarBusqueda(consulta, _filtroSeleccionado.value)
                    } else {
                        _estadoUi.value = BuscarLibrosEstadoUi.Vacio(consulta = "")
                    }
                }
        }

        // Búsqueda inicial automática para presentar el catálogo con datos
        ejecutarBusqueda(_textoBusqueda.value, _filtroSeleccionado.value)
    }

    /**
     * Actualiza el texto ingresado en el campo de búsqueda.
     */
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }

    /**
     * Limpia el texto de búsqueda y cancela cualquier consulta en curso.
     */
    fun limpiarBusqueda() {
        busquedaJob?.cancel()
        _textoBusqueda.value = ""
        _estadoUi.value = BuscarLibrosEstadoUi.Vacio(consulta = "")
    }

    /**
     * Cambia el filtro actual (Todos, Por Título, Por Autor, Por Categoría) y relanza la búsqueda.
     */
    fun seleccionarFiltro(nuevoFiltro: TipoFiltroBusqueda) {
        if (_filtroSeleccionado.value != nuevoFiltro) {
            _filtroSeleccionado.value = nuevoFiltro
            val consultaActual = _textoBusqueda.value
            if (consultaActual.isNotBlank()) {
                ejecutarBusqueda(consultaActual, nuevoFiltro)
            }
        }
    }

    /**
     * Ejecuta una búsqueda directa a partir de una tarjeta de la sección "Explorar Géneros".
     */
    fun seleccionarGenero(nombreGenero: String) {
        _filtroSeleccionado.value = TipoFiltroBusqueda.CATEGORIA
        _textoBusqueda.value = nombreGenero
        ejecutarBusqueda(nombreGenero, TipoFiltroBusqueda.CATEGORIA)
    }

    /**
     * Guarda un libro en la biblioteca local mediante [AccionesBiblioteca].
     */
    fun guardarLibro(libro: LibroModelo) {
        viewModelScope.launch {
            accionesBiblioteca.guardarLibro(libro)
        }
    }

    /**
     * Realiza la consulta al repositorio y actualiza el estado de la UI según el resultado,
     * cancelando cualquier búsqueda previa aún en ejecución para evitar condiciones de carrera.
     */
    fun ejecutarBusqueda(
        consulta: String = _textoBusqueda.value,
        filtro: TipoFiltroBusqueda = _filtroSeleccionado.value
    ) {
        busquedaJob?.cancel()

        val consultaLimpia = consulta.trim()
        if (consultaLimpia.isEmpty()) {
            _estadoUi.value = BuscarLibrosEstadoUi.Vacio(consulta = "")
            return
        }

        _estadoUi.value = BuscarLibrosEstadoUi.Cargando

        busquedaJob = viewModelScope.launch {
            try {
                val resultado = repositorio.buscarLibrosPorFiltro(
                    consulta = consultaLimpia,
                    tipoFiltro = filtro,
                    limite = 20
                )

                resultado.onSuccess { librosEncontrados ->
                    if (librosEncontrados.isEmpty()) {
                        _estadoUi.value = BuscarLibrosEstadoUi.Vacio(consulta = consultaLimpia)
                    } else {
                        _estadoUi.value = BuscarLibrosEstadoUi.Exito(
                            libros = librosEncontrados,
                            cantidadResultados = librosEncontrados.size,
                            consultaActual = consultaLimpia
                        )
                    }
                }.onFailure { error ->
                    val esSinInternet = error is IOException
                    _estadoUi.value = BuscarLibrosEstadoUi.Error(
                        mensaje = error.localizedMessage ?: "Error desconocido al buscar libros",
                        esErrorRed = esSinInternet
                    )
                }
            } catch (httpEx: HttpException) {
                _estadoUi.value = BuscarLibrosEstadoUi.Error(
                    mensaje = "Error del servidor Open Library (HTTP ${httpEx.code()})",
                    esErrorRed = false
                )
            } catch (ioEx: IOException) {
                _estadoUi.value = BuscarLibrosEstadoUi.Error(
                    mensaje = "Sin conexión a internet",
                    esErrorRed = true
                )
            } catch (excepcion: Exception) {
                _estadoUi.value = BuscarLibrosEstadoUi.Error(
                    mensaje = excepcion.localizedMessage ?: "Ocurrió un error inesperado",
                    esErrorRed = false
                )
            }
        }
    }
}

