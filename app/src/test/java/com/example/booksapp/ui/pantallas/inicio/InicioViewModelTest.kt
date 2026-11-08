package com.example.booksapp.ui.pantallas.inicio

import com.example.booksapp.datos.modelos.CategoriaModelo
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.repositorios.LibroRepositorio
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

    override suspend fun obtenerLibroDestacado(): Result<LibroModelo> = Result.success(libroDestacado)
    override suspend fun obtenerLibrosPorMateria(materia: String, limite: Int): Result<List<LibroModelo>> = Result.success(librosFantasia)
    override suspend fun obtenerTendencias(limite: Int): Result<List<LibroModelo>> = Result.success(emptyList())
    override suspend fun buscarLibros(consulta: String, limite: Int): Result<List<LibroModelo>> = Result.success(emptyList())
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
}
