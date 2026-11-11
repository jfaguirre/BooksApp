package com.example.booksapp.datos.repositorios

import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.LibroModelo
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio para la biblioteca personal del usuario (datos locales).
 */
interface BibliotecaRepositorio {

    /** Guarda un libro en la biblioteca local. */
    suspend fun guardarLibro(libro: LibroModelo, estado: EstadoLectura = EstadoLectura.POR_LEER)

    /** Elimina un libro de la biblioteca local. */
    suspend fun eliminarLibro(id: String)

    /** Alterna el favorito de un libro. Devuelve el nuevo estado. */
    suspend fun alternarFavorito(id: String, esFavorito: Boolean)

    /** Cambia el estado de lectura de un libro. */
    suspend fun cambiarEstadoLectura(id: String, estado: EstadoLectura)

    /** Verifica si un libro está guardado. */
    suspend fun estaGuardado(id: String): Boolean

    /** Flujo reactivo de todos los libros de la biblioteca. */
    fun observarBiblioteca(): Flow<List<LibroModelo>>

    /** Flujo reactivo de los favoritos. */
    fun observarFavoritos(): Flow<List<LibroModelo>>

    /** Flujo reactivo de libros por estado de lectura. */
    fun observarPorEstado(estado: EstadoLectura): Flow<List<LibroModelo>>

    /** Flujo del total de libros en la biblioteca. */
    fun observarTotalLibros(): Flow<Int>

    /** Flujo del total de libros leídos. */
    fun observarTotalLeidos(): Flow<Int>

    /** Flujo del total de libros en progreso. */
    fun observarTotalLeyendo(): Flow<Int>
}

