package com.example.booksapp.ui.pantallas.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.CategoriaModelo
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import com.example.booksapp.datos.repositorios.LibroRepositorio
import com.example.booksapp.datos.repositorios.LibroRepositorioImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que coordina los datos de Open Library y el estado de la pantalla de Inicio.
 * También permite guardar libros en la biblioteca local a través del [BibliotecaRepositorio].
 */
class InicioViewModel(
    private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
    private val bibliotecaRepositorio: BibliotecaRepositorio? = null
) : ViewModel() {

    private val _estadoUi = MutableStateFlow<InicioEstadoUi>(InicioEstadoUi.Cargando)
    val estadoUi: StateFlow<InicioEstadoUi> = _estadoUi.asStateFlow()

    // Lista inicial de categorías temáticas
    private val categoriasBase = listOf(
        CategoriaModelo(id = "1", nombre = "Para ti", claveMateriaApi = "all", estaSeleccionada = true),
        CategoriaModelo(id = "2", nombre = "Ficción", claveMateriaApi = "fiction", estaSeleccionada = false),
        CategoriaModelo(id = "3", nombre = "Sci-Fi", claveMateriaApi = "science_fiction", estaSeleccionada = false),
        CategoriaModelo(id = "4", nombre = "No Ficción", claveMateriaApi = "nonfiction", estaSeleccionada = false),
        CategoriaModelo(id = "5", nombre = "Clásicos", claveMateriaApi = "classics", estaSeleccionada = false)
    )

    init {
        cargarDatosInicio()
    }

    /**
     * Carga de forma concurrente todas las secciones de libros desde Open Library.
     */
    fun cargarDatosInicio() {
        _estadoUi.value = InicioEstadoUi.Cargando

        viewModelScope.launch {
            try {
                val destacadoDiferido = async { repositorio.obtenerLibroDestacado() }
                val ficcionDiferido = async { repositorio.obtenerLibrosPorMateria("fantasy", 6) }
                val sciFiDiferido = async { repositorio.obtenerLibrosPorMateria("science_fiction", 6) }
                val tendenciasDiferido = async { repositorio.obtenerTendencias(4) }

                val resultadoDestacado = destacadoDiferido.await()
                val resultadoFiccion = ficcionDiferido.await()
                val resultadoSciFi = sciFiDiferido.await()
                val resultadoTendencias = tendenciasDiferido.await()

                _estadoUi.value = InicioEstadoUi.Exito(
                    saludo = "¡Hola, Sofía!",
                    estaApiConectada = true,
                    categorias = categoriasBase,
                    libroDelMomento = resultadoDestacado.getOrNull(),
                    librosFiccionYFantasia = resultadoFiccion.getOrDefault(emptyList()),
                    librosSciFiYDistopia = resultadoSciFi.getOrDefault(emptyList()),
                    tendenciasComunidad = resultadoTendencias.getOrDefault(emptyList())
                )
            } catch (excepcion: Exception) {
                _estadoUi.value = InicioEstadoUi.Error(
                    mensaje = excepcion.localizedMessage ?: "Error al conectar con Open Library"
                )
            }
        }
    }

    /**
     * Actualiza el término de búsqueda ingresado por el usuario.
     */
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        val estadoActual = _estadoUi.value
        if (estadoActual is InicioEstadoUi.Exito) {
            _estadoUi.update {
                (it as InicioEstadoUi.Exito).copy(textoBusqueda = nuevoTexto)
            }
        }
    }

    /**
     * Alterna la categoría seleccionada en los chips horizontales.
     */
    fun seleccionarCategoria(categoriaSeleccionada: CategoriaModelo) {
        val estadoActual = _estadoUi.value
        if (estadoActual is InicioEstadoUi.Exito) {
            val listaActualizada = estadoActual.categorias.map { cat ->
                cat.copy(estaSeleccionada = cat.id == categoriaSeleccionada.id)
            }
            _estadoUi.update {
                (it as InicioEstadoUi.Exito).copy(categorias = listaActualizada)
            }
        }
    }

    /**
     * Alterna el estado de favorito de un libro en la UI.
     */
    fun alternarFavorito(libro: LibroModelo) {
        val estadoActual = _estadoUi.value
        if (estadoActual is InicioEstadoUi.Exito) {
            val nuevoEstadoFavorito = !libro.esFavorito

            val libroMomentoActualizado = if (estadoActual.libroDelMomento?.id == libro.id) {
                estadoActual.libroDelMomento.copy(esFavorito = nuevoEstadoFavorito)
            } else {
                estadoActual.libroDelMomento
            }

            val ficcionActualizada = estadoActual.librosFiccionYFantasia.map {
                if (it.id == libro.id) it.copy(esFavorito = nuevoEstadoFavorito) else it
            }

            val sciFiActualizada = estadoActual.librosSciFiYDistopia.map {
                if (it.id == libro.id) it.copy(esFavorito = nuevoEstadoFavorito) else it
            }

            val tendenciasActualizadas = estadoActual.tendenciasComunidad.map {
                if (it.id == libro.id) it.copy(esFavorito = nuevoEstadoFavorito) else it
            }

            _estadoUi.update {
                (it as InicioEstadoUi.Exito).copy(
                    libroDelMomento = libroMomentoActualizado,
                    librosFiccionYFantasia = ficcionActualizada,
                    librosSciFiYDistopia = sciFiActualizada,
                    tendenciasComunidad = tendenciasActualizadas
                )
            }
        }
    }

    /**
     * Guarda un libro en la biblioteca personal local.
     */
    fun añadirABiblioteca(libro: LibroModelo) {
        val repo = bibliotecaRepositorio ?: return
        viewModelScope.launch {
            repo.guardarLibro(libro, EstadoLectura.POR_LEER)
            // Actualiza la UI para reflejar que el libro fue añadido
            val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return@launch
            _estadoUi.update {
                (it as InicioEstadoUi.Exito).copy(mensajeConfirmacion = "\"${libro.titulo}\" añadido a tu biblioteca")
            }
        }
    }

    /**
     * Limpia el mensaje de confirmación del snackbar.
     */
    fun limpiarMensaje() {
        val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return
        _estadoUi.update { (it as InicioEstadoUi.Exito).copy(mensajeConfirmacion = null) }
    }

    // ─── FACTORY ─────────────────────────────────────────────────────────────

    class Factory(
        private val repositorio: LibroRepositorio = LibroRepositorioImpl(),
        private val bibliotecaRepositorio: BibliotecaRepositorio? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
                return InicioViewModel(repositorio, bibliotecaRepositorio) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}

