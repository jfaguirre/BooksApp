package com.example.booksapp.ui.pantallas.inicio

import com.example.booksapp.datos.modelos.CategoriaModelo
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.repositorios.LibroRepositorio
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Falso repositorio para pruebas unitarias de [InicioViewModel].
 */
private class FalsoLibroRepositorio : LibroRepositorio {
    var libroDestacado = LibroModelo(
        id = "/works/OL1",
        titulo = "Cien años de soledad",
        autores = listOf("Gabriel García Márquez")
    )
    var librosFantasia = listOf(
        LibroModelo(id = "/works/OL2", titulo = "El nombre del viento", autores = listOf("Patrick Rothfuss"))
    )
    var resultadosBusqueda = listOf(
        LibroModelo(id = "/works/OL9", titulo = "Harry Potter y la piedra filosofal", autores = listOf("J. K. Rowling"))
    )
    val consultasBusqueda = mutableListOf<String>()
    val materiasConsultadas = mutableListOf<String>()

    override suspend fun obtenerLibroDestacado(): Result<LibroModelo> = Result.success(libroDestacado)
    override suspend fun obtenerLibrosPorMateria(materia: String, limite: Int): Result<List<LibroModelo>> {
        materiasConsultadas.add(materia)
        return Result.success(librosFantasia)
    }
    override suspend fun obtenerTendencias(limite: Int): Result<List<LibroModelo>> = Result.success(emptyList())
    override suspend fun buscarLibros(consulta: String, limite: Int): Result<List<LibroModelo>> {
        consultasBusqueda.add(consulta)
        return Result.success(resultadosBusqueda)
    }
}

/**
 * Falso repositorio de biblioteca para pruebas unitarias.
 */
private class FalsoBibliotecaRepositorio : com.example.booksapp.datos.repositorios.BibliotecaRepositorio {
    val librosGuardados = mutableListOf<LibroModelo>()
    val favoritosFlow = kotlinx.coroutines.flow.MutableStateFlow<Set<String>>(emptySet())

    override suspend fun guardarLibro(libro: LibroModelo, estado: com.example.booksapp.datos.local.EstadoLectura) {
        librosGuardados.removeAll { it.id == libro.id }
        librosGuardados.add(libro)
    }

    override suspend fun eliminarLibro(id: String) {
        librosGuardados.removeAll { it.id == id }
        favoritosFlow.value = favoritosFlow.value - id
    }

    override suspend fun alternarFavorito(libro: LibroModelo): Boolean {
        val nuevoEstado = !favoritosFlow.value.contains(libro.id)
        if (nuevoEstado) {
            favoritosFlow.value = favoritosFlow.value + libro.id
            guardarLibro(libro.copy(esFavorito = true))
        } else {
            favoritosFlow.value = favoritosFlow.value - libro.id
        }
        return nuevoEstado
    }

    override suspend fun alternarFavorito(id: String, esFavorito: Boolean) {
        if (esFavorito) favoritosFlow.value = favoritosFlow.value + id
        else favoritosFlow.value = favoritosFlow.value - id
    }

    override suspend fun iniciarLectura(libro: LibroModelo) {
        guardarLibro(libro, com.example.booksapp.datos.local.EstadoLectura.LEYENDO)
    }

    override suspend fun cambiarEstadoLectura(id: String, estado: com.example.booksapp.datos.local.EstadoLectura) {}
    override suspend fun estaGuardado(id: String): Boolean = librosGuardados.any { it.id == id }
    override suspend fun obtenerLibroPorId(id: String): LibroModelo? = librosGuardados.find { it.id == id }

    override fun observarBiblioteca(): kotlinx.coroutines.flow.Flow<List<LibroModelo>> =
        kotlinx.coroutines.flow.flowOf(librosGuardados)

    override fun observarFavoritos(): kotlinx.coroutines.flow.Flow<List<LibroModelo>> =
        favoritosFlow.map { ids -> librosGuardados.filter { ids.contains(it.id) } }

    override fun observarIdsFavoritos(): kotlinx.coroutines.flow.Flow<Set<String>> = favoritosFlow

    override fun observarLibroEnLecturaActiva(): kotlinx.coroutines.flow.Flow<LibroModelo?> =
        kotlinx.coroutines.flow.flowOf(librosGuardados.firstOrNull())

    override fun observarLibrosEnLectura(): kotlinx.coroutines.flow.Flow<List<LibroModelo>> =
        kotlinx.coroutines.flow.flowOf(librosGuardados)

    override fun observarPorEstado(estado: com.example.booksapp.datos.local.EstadoLectura): kotlinx.coroutines.flow.Flow<List<LibroModelo>> =
        kotlinx.coroutines.flow.flowOf(emptyList())

    override fun observarTotalLibros(): kotlinx.coroutines.flow.Flow<Int> =
        kotlinx.coroutines.flow.flowOf(librosGuardados.size)

    override fun observarTotalLeidos(): kotlinx.coroutines.flow.Flow<Int> = kotlinx.coroutines.flow.flowOf(0)
    override fun observarTotalLeyendo(): kotlinx.coroutines.flow.Flow<Int> = kotlinx.coroutines.flow.flowOf(0)
}

/**
 * Falso repositorio de usuario para pruebas unitarias.
 */
private class FalsoUsuarioRepositorio(nombreInicial: String = "Sofía") : com.example.booksapp.datos.repositorios.UsuarioRepositorio {
    val nombreFlow = kotlinx.coroutines.flow.MutableStateFlow(nombreInicial)

    override fun observarNombreUsuario(): kotlinx.coroutines.flow.Flow<String> = nombreFlow

    override suspend fun guardarNombreUsuario(nombre: String) {
        nombreFlow.value = nombre
    }

    override fun obtenerNombreUsuarioActual(): String = nombreFlow.value
}

/**
 * Pruebas unitarias para el ViewModel de la pantalla de Inicio.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModelTest {

    private val despachadorPruebas = StandardTestDispatcher()
    private lateinit var falsoRepositorio: FalsoLibroRepositorio

    @Before
    fun configurar() {
        Dispatchers.setMain(despachadorPruebas)
        falsoRepositorio = FalsoLibroRepositorio()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun actualizarTextoBusqueda_modificaElEstadoCorrectamente() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        viewModel.actualizarTextoBusqueda("Harry Potter")

        val estado = viewModel.estadoUi.value
        assertTrue(estado is InicioEstadoUi.Exito)
        if (estado is InicioEstadoUi.Exito) {
            assertEquals("Harry Potter", estado.textoBusqueda)
        }
    }

    @Test
    fun seleccionarCategoria_actualizaLaCategoriaActiva() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        val categoriaFiccion = CategoriaModelo(
            id = "2",
            nombre = "Ficción",
            claveMateriaApi = "fiction",
            estaSeleccionada = false
        )

        viewModel.seleccionarCategoria(categoriaFiccion)

        val estado = viewModel.estadoUi.value
        assertTrue(estado is InicioEstadoUi.Exito)
        if (estado is InicioEstadoUi.Exito) {
            val categoriaActiva = estado.categorias.firstOrNull { it.estaSeleccionada }
            assertEquals("2", categoriaActiva?.id)
        }
    }

    @Test
    fun alternarFavorito_invierteElEstadoDelLibro() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        val libro = LibroModelo(id = "/works/OL1", titulo = "Cien años de soledad", esFavorito = false)

        viewModel.alternarFavorito(libro)

        val estado = viewModel.estadoUi.value
        assertTrue(estado is InicioEstadoUi.Exito)
        if (estado is InicioEstadoUi.Exito) {
            val libroActualizado = estado.libroDelMomento
            assertTrue(libroActualizado?.esFavorito == true)
        }
    }

    @Test
    fun alternarFavorito_conBibliotecaRepositorio_persisteYSincronizaEstado() = runTest {
        val bibliotecaRepo = FalsoBibliotecaRepositorio()
        val viewModel = InicioViewModel(
            repositorio = falsoRepositorio,
            bibliotecaRepositorio = bibliotecaRepo
        )
        advanceUntilIdle()

        val libro = LibroModelo(id = "/works/OL1", titulo = "Cien años de soledad", esFavorito = false)

        // Agregar a favoritos
        viewModel.alternarFavorito(libro)
        advanceUntilIdle()

        assertTrue(bibliotecaRepo.favoritosFlow.value.contains("/works/OL1"))
        val estadoDespuesDeAgregar = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertTrue(estadoDespuesDeAgregar.libroDelMomento?.esFavorito == true)

        // Quitar de favoritos
        val libroFavorito = libro.copy(esFavorito = true)
        viewModel.alternarFavorito(libroFavorito)
        advanceUntilIdle()

        assertTrue(!bibliotecaRepo.favoritosFlow.value.contains("/works/OL1"))
        val estadoDespuesDeQuitar = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertTrue(estadoDespuesDeQuitar.libroDelMomento?.esFavorito == false)
    }

    @Test
    fun guardarNombreUsuario_actualizaElSaludoYElEstado() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        viewModel.guardarNombreUsuario("Carlos")

        val estado = viewModel.estadoUi.value
        assertTrue(estado is InicioEstadoUi.Exito)
        if (estado is InicioEstadoUi.Exito) {
            assertEquals("Carlos", estado.nombreUsuario)
            assertEquals("¡Hola, Carlos!", estado.saludo)
        }
    }

    @Test
    fun guardarNombreUsuario_conUsuarioRepositorio_persisteElNombre() = runTest {
        val usuarioRepo = FalsoUsuarioRepositorio("Sofía")
        val viewModel = InicioViewModel(
            repositorio = falsoRepositorio,
            usuarioRepositorio = usuarioRepo
        )
        advanceUntilIdle()

        viewModel.guardarNombreUsuario("Valentina")
        advanceUntilIdle()

        assertEquals("Valentina", usuarioRepo.obtenerNombreUsuarioActual())
        val estado = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertEquals("Valentina", estado.nombreUsuario)
        assertEquals("¡Hola, Valentina!", estado.saludo)
    }

    @Test
    fun actualizarTextoBusqueda_conConsultaRealizada_cargaLosResultados() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        viewModel.actualizarTextoBusqueda("Harry Potter")
        advanceUntilIdle()

        assertTrue(falsoRepositorio.consultasBusqueda.contains("Harry Potter"))
        val estado = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertEquals(1, estado.librosResultadoBusqueda?.size)
        assertEquals("/works/OL9", estado.librosResultadoBusqueda?.first()?.id)
        assertEquals(false, estado.estaBuscando)
    }

    @Test
    fun actualizarTextoBusqueda_conTextoEnBlanco_limpiaLosResultados() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        viewModel.actualizarTextoBusqueda("Harry Potter")
        advanceUntilIdle()
        viewModel.actualizarTextoBusqueda("   ")
        advanceUntilIdle()

        val estado = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertEquals(null, estado.librosResultadoBusqueda)
        assertEquals(false, estado.estaBuscando)
    }

    @Test
    fun limpiarBusqueda_restableceElTextoYLosResultados() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        viewModel.actualizarTextoBusqueda("Harry Potter")
        advanceUntilIdle()
        viewModel.limpiarBusqueda()
        advanceUntilIdle()

        val estado = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertEquals("", estado.textoBusqueda)
        assertEquals(null, estado.librosResultadoBusqueda)
        assertEquals(false, estado.estaBuscando)
    }

    @Test
    fun seleccionarCategoria_distintaDeParaTi_cargaLibrosDeLaMateria() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        val categoriaFiccion = CategoriaModelo(
            id = "2",
            nombre = "Ficción",
            claveMateriaApi = "fiction"
        )

        viewModel.seleccionarCategoria(categoriaFiccion)
        advanceUntilIdle()

        assertTrue(falsoRepositorio.materiasConsultadas.contains("fiction"))
        val estado = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertEquals(false, estado.estaCargandoCategoria)
        assertEquals(1, estado.librosFiltradosPorCategoria?.size)
        assertEquals("/works/OL2", estado.librosFiltradosPorCategoria?.first()?.id)
    }

    @Test
    fun seleccionarCategoria_paraTi_limpiaElFiltroDeCategoria() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        val categoriaFiccion = CategoriaModelo(
            id = "2",
            nombre = "Ficción",
            claveMateriaApi = "fiction"
        )
        val categoriaParaTi = CategoriaModelo(
            id = "1",
            nombre = "Para ti",
            claveMateriaApi = "all"
        )

        viewModel.seleccionarCategoria(categoriaFiccion)
        advanceUntilIdle()
        viewModel.seleccionarCategoria(categoriaParaTi)
        advanceUntilIdle()

        val estado = viewModel.estadoUi.value as InicioEstadoUi.Exito
        assertEquals(null, estado.librosFiltradosPorCategoria)
        assertEquals(false, estado.estaCargandoCategoria)
    }

    @Test
    fun actualizarTextoBusqueda_conCategoriaSeleccionada_incluyeLaMateriaEnLaConsulta() = runTest {
        val viewModel = InicioViewModel(falsoRepositorio)
        advanceUntilIdle()

        val categoriaSciFi = CategoriaModelo(
            id = "3",
            nombre = "Sci-Fi",
            claveMateriaApi = "science_fiction"
        )
        viewModel.seleccionarCategoria(categoriaSciFi)
        advanceUntilIdle()

        viewModel.actualizarTextoBusqueda("dune")
        advanceUntilIdle()

        assertTrue(falsoRepositorio.consultasBusqueda.contains("subject:science_fiction dune"))
    }
}
