package com.example.booksapp.datos.repositorios

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [LibroRepositorioImpl] escritas en español.
 */
class LibroRepositorioTest {

    private lateinit var repositorio: LibroRepositorio

    @Before
    fun configurar() {
        repositorio = LibroRepositorioImpl()
    }

    @Test
    fun obtenerLibroDestacado_retornaLibroConDatosCompletos() = runBlocking {
        val resultado = repositorio.obtenerLibroDestacado()

        assertTrue(resultado.isSuccess)
        val libro = resultado.getOrNull()
        assertNotNull(libro)
        assertTrue(libro?.titulo?.isNotEmpty() == true)
        assertTrue(libro?.autores?.isNotEmpty() == true)
    }

    @Test
    fun obtenerLibrosPorMateria_cuandoEsFantasia_retornaListaNoVacia() = runBlocking {
        val resultado = repositorio.obtenerLibrosPorMateria("fantasy", limite = 4)

        assertTrue(resultado.isSuccess)
        val lista = resultado.getOrDefault(emptyList())
        assertTrue(lista.isNotEmpty())
    }

    @Test
    fun obtenerTendencias_retornaListaDeTendenciasConLectores() = runBlocking {
        val resultado = repositorio.obtenerTendencias(limite = 2)

        assertTrue(resultado.isSuccess)
        val tendencias = resultado.getOrDefault(emptyList())
        assertTrue(tendencias.isNotEmpty())
        assertNotNull(tendencias.first().lectores)
    }
}
