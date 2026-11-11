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
import com.example.booksapp.datos.repositorios.UsuarioRepositorio
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    private val bibliotecaRepositorio: BibliotecaRepositorio? = null,
    private val usuarioRepositorio: UsuarioRepositorio? = null
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

    // Conjunto en memoria de IDs de favoritos actuales
    private var idsFavoritosActuales: Set<String> = emptySet()
    private var nombreUsuarioActual: String = usuarioRepositorio?.obtenerNombreUsuarioActual() ?: "Sofía"
    private var trabajoBusqueda: Job? = null
    private var trabajoCategoria: Job? = null

    init {
        cargarDatosInicio()
        observarFavoritos()
        observarNombreUsuario()
    }

    /**
     * Observa reactivamente el nombre del usuario desde el repositorio local.
     */
    private fun observarNombreUsuario() {
        val repo = usuarioRepositorio ?: return
        viewModelScope.launch {
            repo.observarNombreUsuario().collect { nombre ->
                nombreUsuarioActual = nombre
                val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return@collect
                _estadoUi.update {
                    estadoActual.copy(
                        nombreUsuario = nombre,
                        saludo = "¡Hola, $nombre!"
                    )
                }
            }
        }
    }

    /**
     * Observa reactivamente los IDs favoritos almacenados en la base de datos local.
     */
    private fun observarFavoritos() {
        val repo = bibliotecaRepositorio ?: return
        viewModelScope.launch {
            repo.observarIdsFavoritos().collect { ids ->
                idsFavoritosActuales = ids
                actualizarLibrosConFavoritos(ids)
            }
        }
    }

    private fun actualizarLibrosConFavoritos(ids: Set<String>) {
        val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return
        _estadoUi.update {
            estadoActual.copy(
                libroDelMomento = estadoActual.libroDelMomento?.let {
                    it.copy(esFavorito = ids.contains(it.id))
                },
                librosFiccionYFantasia = estadoActual.librosFiccionYFantasia.map {
                    it.copy(esFavorito = ids.contains(it.id))
                },
                librosSciFiYDistopia = estadoActual.librosSciFiYDistopia.map {
                    it.copy(esFavorito = ids.contains(it.id))
                },
                tendenciasComunidad = estadoActual.tendenciasComunidad.map {
                    it.copy(esFavorito = ids.contains(it.id))
                },
                librosFiltradosPorCategoria = estadoActual.librosFiltradosPorCategoria?.map {
                    it.copy(esFavorito = ids.contains(it.id))
                },
                librosResultadoBusqueda = estadoActual.librosResultadoBusqueda?.map {
                    it.copy(esFavorito = ids.contains(it.id))
                }
            )
        }
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

                val destacado = resultadoDestacado.getOrNull()?.let {
                    it.copy(esFavorito = idsFavoritosActuales.contains(it.id))
                }
                val ficcion = resultadoFiccion.getOrDefault(emptyList()).map {
                    it.copy(esFavorito = idsFavoritosActuales.contains(it.id))
                }
                val sciFi = resultadoSciFi.getOrDefault(emptyList()).map {
                    it.copy(esFavorito = idsFavoritosActuales.contains(it.id))
                }
                val tendencias = resultadoTendencias.getOrDefault(emptyList()).map {
                    it.copy(esFavorito = idsFavoritosActuales.contains(it.id))
                }

                _estadoUi.value = InicioEstadoUi.Exito(
                    saludo = "¡Hola, $nombreUsuarioActual!",
                    nombreUsuario = nombreUsuarioActual,
                    estaApiConectada = true,
                    categorias = categoriasBase,
                    libroDelMomento = destacado,
                    librosFiccionYFantasia = ficcion,
                    librosSciFiYDistopia = sciFi,
                    tendenciasComunidad = tendencias
                )
            } catch (excepcion: Exception) {
                _estadoUi.value = InicioEstadoUi.Error(
                    mensaje = excepcion.localizedMessage ?: "Error al conectar con Open Library"
                )
            }
        }
    }

    /**
     * Actualiza el término de búsqueda ingresado por el usuario y programa una búsqueda
     * diferida contra Open Library, combinando la materia de la categoría activa cuando aplique.
     */
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        if (_estadoUi.value !is InicioEstadoUi.Exito) return

        _estadoUi.update {
            (it as InicioEstadoUi.Exito).copy(textoBusqueda = nuevoTexto)
        }

        val consulta = nuevoTexto.trim()
        if (consulta.isEmpty()) {
            cancelarBusquedaYLimpiarResultados()
        } else {
            programarBusqueda(consulta)
        }
    }

    /**
     * Limpia el texto de búsqueda y sus resultados, restaurando el contenido habitual del inicio.
     */
    fun limpiarBusqueda() {
        actualizarTextoBusqueda("")
    }

    /**
     * Alterna la categoría seleccionada en los chips horizontales y carga los libros
     * de la materia correspondiente desde Open Library.
     */
    fun seleccionarCategoria(categoriaSeleccionada: CategoriaModelo) {
        val estadoActual = _estadoUi.value
        if (estadoActual !is InicioEstadoUi.Exito) return

        val listaActualizada = estadoActual.categorias.map { cat ->
            cat.copy(estaSeleccionada = cat.id == categoriaSeleccionada.id)
        }
        _estadoUi.update {
            (it as InicioEstadoUi.Exito).copy(categorias = listaActualizada)
        }

        trabajoCategoria?.cancel()

        if (categoriaSeleccionada.claveMateriaApi == CategoriaModelo.CLAVE_MATERIA_TODAS) {
            _estadoUi.update {
                (it as InicioEstadoUi.Exito).copy(
                    librosFiltradosPorCategoria = null,
                    estaCargandoCategoria = false
                )
            }
        } else {
            trabajoCategoria = viewModelScope.launch {
                _estadoUi.update {
                    (it as InicioEstadoUi.Exito).copy(estaCargandoCategoria = true)
                }
                val resultado = repositorio.obtenerLibrosPorMateria(
                    materia = categoriaSeleccionada.claveMateriaApi,
                    limite = LIMITE_LIBROS_CATEGORIA
                )
                val estadoPosterior = _estadoUi.value as? InicioEstadoUi.Exito ?: return@launch
                _estadoUi.update {
                    estadoPosterior.copy(
                        librosFiltradosPorCategoria = resultado
                            .getOrDefault(emptyList())
                            .map { aplicarEstadoFavorito(it) },
                        estaCargandoCategoria = false
                    )
                }
            }
        }

        // Si hay una búsqueda activa, se vuelve a ejecutar para respetar la categoría elegida
        val consultaActiva = (_estadoUi.value as? InicioEstadoUi.Exito)
            ?.textoBusqueda?.trim().orEmpty()
        if (consultaActiva.isNotEmpty()) {
            programarBusqueda(consultaActiva)
        }
    }

    /**
     * Ejecuta la búsqueda tras un pequeño retardo para evitar consultas en cada pulsación
     * de tecla; las consultas consecutivas cancelan el trabajo anterior.
     */
    private fun programarBusqueda(consulta: String) {
        trabajoBusqueda?.cancel()
        _estadoUi.update {
            (it as InicioEstadoUi.Exito).copy(estaBuscando = true)
        }
        trabajoBusqueda = viewModelScope.launch {
            delay(RETARZO_BUSQUEDA_MS)
            val consultaConMateria = construirConsultaConCategoria(consulta)
            val resultado = repositorio.buscarLibros(
                consulta = consultaConMateria,
                limite = LIMITE_RESULTADOS_BUSQUEDA
            )
            val estadoPosterior = _estadoUi.value as? InicioEstadoUi.Exito ?: return@launch
            _estadoUi.update {
                estadoPosterior.copy(
                    librosResultadoBusqueda = resultado
                        .getOrDefault(emptyList())
                        .map { aplicarEstadoFavorito(it) },
                    estaBuscando = false
                )
            }
        }
    }

    private fun cancelarBusquedaYLimpiarResultados() {
        trabajoBusqueda?.cancel()
        _estadoUi.update {
            (it as InicioEstadoUi.Exito).copy(
                librosResultadoBusqueda = null,
                estaBuscando = false
            )
        }
    }

    /**
     * Combina la consulta libre del usuario con la materia de la categoría activa,
     * para que la búsqueda respete el filtro seleccionado en los chips.
     */
    private fun construirConsultaConCategoria(consulta: String): String {
        val claveMateria = (_estadoUi.value as? InicioEstadoUi.Exito)
            ?.categorias
            ?.firstOrNull { it.estaSeleccionada }
            ?.claveMateriaApi
        return if (claveMateria != null && claveMateria != CategoriaModelo.CLAVE_MATERIA_TODAS) {
            "subject:$claveMateria $consulta"
        } else {
            consulta
        }
    }

    private fun aplicarEstadoFavorito(libro: LibroModelo): LibroModelo {
        return libro.copy(esFavorito = idsFavoritosActuales.contains(libro.id))
    }

    /**
     * Alterna el estado de favorito de un libro en la biblioteca local y en la UI.
     */
    fun alternarFavorito(libro: LibroModelo) {
        val repo = bibliotecaRepositorio
        if (repo != null) {
            viewModelScope.launch {
                val nuevoEstado = repo.alternarFavorito(libro)
                val mensaje = if (nuevoEstado) {
                    "\"${libro.titulo}\" añadido a tus favoritos"
                } else {
                    "\"${libro.titulo}\" eliminado de tus favoritos"
                }
                val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return@launch
                _estadoUi.update {
                    estadoActual.copy(mensajeConfirmacion = mensaje)
                }
            }
        } else {
            actualizarFavoritoEnMemoria(libro)
        }
    }

    private fun actualizarFavoritoEnMemoria(libro: LibroModelo) {
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

            val librosCategoriaActualizados = estadoActual.librosFiltradosPorCategoria?.map {
                if (it.id == libro.id) it.copy(esFavorito = nuevoEstadoFavorito) else it
            }

            val resultadosBusquedaActualizados = estadoActual.librosResultadoBusqueda?.map {
                if (it.id == libro.id) it.copy(esFavorito = nuevoEstadoFavorito) else it
            }

            _estadoUi.update {
                (it as InicioEstadoUi.Exito).copy(
                    libroDelMomento = libroMomentoActualizado,
                    librosFiccionYFantasia = ficcionActualizada,
                    librosSciFiYDistopia = sciFiActualizada,
                    tendenciasComunidad = tendenciasActualizadas,
                    librosFiltradosPorCategoria = librosCategoriaActualizados,
                    librosResultadoBusqueda = resultadosBusquedaActualizados
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
     * Guarda el nombre del usuario en el almacenamiento local y actualiza el saludo.
     */
    fun guardarNombreUsuario(nuevoNombre: String) {
        val nombreValido = nuevoNombre.trim().ifEmpty { "Sofía" }
        nombreUsuarioActual = nombreValido
        val repo = usuarioRepositorio
        if (repo != null) {
            viewModelScope.launch {
                repo.guardarNombreUsuario(nombreValido)
                val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return@launch
                _estadoUi.update {
                    estadoActual.copy(
                        nombreUsuario = nombreValido,
                        saludo = "¡Hola, $nombreValido!",
                        mensajeConfirmacion = "Nombre actualizado a $nombreValido"
                    )
                }
            }
        } else {
            val estadoActual = _estadoUi.value as? InicioEstadoUi.Exito ?: return
            _estadoUi.update {
                estadoActual.copy(
                    nombreUsuario = nombreValido,
                    saludo = "¡Hola, $nombreValido!"
                )
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
        private val bibliotecaRepositorio: BibliotecaRepositorio? = null,
        private val usuarioRepositorio: UsuarioRepositorio? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
                return InicioViewModel(repositorio, bibliotecaRepositorio, usuarioRepositorio) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }

    private companion object {
        const val RETARZO_BUSQUEDA_MS = 500L
        const val LIMITE_RESULTADOS_BUSQUEDA = 12
        const val LIMITE_LIBROS_CATEGORIA = 12
    }
}

