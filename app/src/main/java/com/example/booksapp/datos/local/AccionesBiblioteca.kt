package com.example.booksapp.datos.local

import com.example.booksapp.datos.modelos.LibroModelo
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato de acciones locales de biblioteca para interactuar con la persistencia Room.
 * Desacopla la UI para que el compañero responsable de Room pueda conectarlo fácilmente.
 */
interface AccionesBiblioteca {

    /**
     * Guarda un libro en la biblioteca local.
     */
    suspend fun guardarLibro(libro: LibroModelo): Boolean

    /**
     * Comprueba si un libro ya está guardado en la biblioteca local.
     */
    fun esLibroGuardado(idLibro: String): Boolean

    /**
     * Flujo reactivo con el conjunto de identificadores de libros guardados.
     */
    val idsLibrosGuardados: StateFlow<Set<String>>
}

