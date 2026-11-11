package com.example.booksapp.datos.local

import com.example.booksapp.datos.modelos.LibroModelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Implementación provisional en memoria de [AccionesBiblioteca].
 * Permite probar el botón "+ Guardar" y el estado "Guardado" en la UI de inmediato.
 */
class AccionesBibliotecaProvisional private constructor() : AccionesBiblioteca {

    private val librosEnMemoria = mutableMapOf<String, LibroModelo>()

    // Incluimos por defecto la clave de "Cien años de soledad" para replicar fielmente el diseño de Stitch
    private val _idsLibrosGuardados = MutableStateFlow<Set<String>>(setOf("/works/OL27479W", "OL27479W"))
    override val idsLibrosGuardados: StateFlow<Set<String>> = _idsLibrosGuardados.asStateFlow()

    override suspend fun guardarLibro(libro: LibroModelo): Boolean {
        librosEnMemoria[libro.id] = libro
        val idLimpio = libro.obtenerIdLimpio()
        _idsLibrosGuardados.update { idsActuales ->
            idsActuales + libro.id + idLimpio
        }
        return true
    }

    override fun esLibroGuardado(idLibro: String): Boolean {
        val idLimpio = idLibro.removePrefix("/works/").removePrefix("works/")
        val actual = _idsLibrosGuardados.value
        return actual.contains(idLibro) || actual.contains(idLimpio)
    }

    companion object {
        val instancia: AccionesBibliotecaProvisional by lazy { AccionesBibliotecaProvisional() }
    }
}

