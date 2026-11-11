package com.example.booksapp.ui.pantallas.busqueda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.local.AccionesBiblioteca
import com.example.booksapp.datos.local.AccionesBibliotecaProvisional
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.modelos.TipoFiltroBusqueda
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
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
 * ViewModel que gestiona la lógica de presentación, filtros y búsqueda de libros con debounce,
 * sincronizando favoritos y biblioteca personal en tiempo real con la base de datos Room.
 */
@OptIn(FlowPreview::class)
class BuscarLibrosViewModel(
    private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
    private val bibliotecaRepositorio: BibliotecaRepositorio? = null,
    private val accionesBiblioteca: AccionesBiblioteca = AccionesBibliotecaProvisional.instancia
) : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("Gabriel García Márquez")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _filtroSeleccionado = MutableStateFlow(TipoFiltroBusqueda.TODOS)
    val filtroSeleccionado: StateFlow<TipoFiltroBusqueda> = _filtroSeleccionado.asStateFlow()

    private val _estadoUi = MutableStateFlow<BuscarLibrosEstadoUi>(BuscarLibrosEstadoUi.Cargando)
    val estadoUi: StateFlow<BuscarLibrosEstadoUi> = _estadoUi.asStateFlow()

    private val _idsFavoritos = MutableStateFlow<Set<String>>(emptySet())
    val idsFavoritos: StateFlow<Set<String>> = _idsFavoritos.asStateFlow()

    private val _idsLibrosGuardados = MutableStateFlow<Set<String>>(emptySet())
    val idsLibrosGuardados: StateFlow<Set<String>> = _idsLibrosGuardados.asStateFlow()

    private var busquedaJob: Job? = null

    init {
        // Observar IDs favoritos y libros guardados de Room si bibliotecaRepositorio está disponible
        bibliotecaRepositorio?.let { repo ->
            viewModelScope.launch {
                repo.observarIdsFavoritos().collect { ids ->
                    _idsFavoritos.value = ids
                }
            }
            viewModelScope.launch {
                repo.observarBiblioteca().collect { lista ->
                    _idsLibrosGuardados.value = lista.map { it.id }.toSet()
                }
            }
        } ?: run {
            // Respaldo provisional en caso de no tener repositorio Room inyectado
            viewModelScope.launch {
                accionesBiblioteca.idsLibrosGuardados.collect { ids ->
                    _idsLibrosGuardados.value = ids
                }
            }
        }

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
     * Alterna el estado de favorito de un libro en Room Database.
     * Si no existe en la biblioteca, Room lo inserta marcado como favorito.
     * Si ya existe, invierte su estado esFavorito.
     */
    fun alternarFavorito(libro: LibroModelo) {
        val repo = bibliotecaRepositorio
        if (repo != null) {
            viewModelScope.launch {
                repo.alternarFavorito(libro)
            }
        }
    }

    /**
     * Guarda un libro en la biblioteca local mediante [BibliotecaRepositorio] (Room)
     * o mediante el mecanismo provisional de respaldo.
     */
    fun guardarLibro(libro: LibroModelo) {
        val repo = bibliotecaRepositorio
        if (repo != null) {
            viewModelScope.launch {
                repo.guardarLibro(libro)
            }
        } else {
            viewModelScope.launch {
                accionesBiblioteca.guardarLibro(libro)
            }
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

    /**
     * Factoría para instanciar [BuscarLibrosViewModel] con sus dependencias de Room.
     */
    class Factory(
        private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
        private val bibliotecaRepositorio: BibliotecaRepositorio
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BuscarLibrosViewModel(
                repositorio = repositorio,
                bibliotecaRepositorio = bibliotecaRepositorio
            ) as T
        }
    }
}
