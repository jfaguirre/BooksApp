package com.example.booksapp.datos.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones CRUD de la biblioteca personal del usuario.
 */
@Dao
interface BibliotecaDao {

    // ─── INSERTAR / GUARDAR ─────────────────────────────────────────────────

    /**
     * Guarda un libro en la biblioteca. Si ya existe (mismo id), lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarLibro(libro: LibroBibliotecaEntidad)

    // ─── ACTUALIZAR ─────────────────────────────────────────────────────────

    /**
     * Actualiza los datos de un libro existente en la biblioteca.
     */
    @Update
    suspend fun actualizarLibro(libro: LibroBibliotecaEntidad)

    /**
     * Cambia el estado de lectura de un libro.
     */
    @Query("UPDATE libros_biblioteca SET estadoLectura = :estado, fechaActualizacion = :ahora WHERE id = :id")
    suspend fun actualizarEstadoLectura(id: String, estado: EstadoLectura, ahora: Long = System.currentTimeMillis())

    /**
     * Alterna el estado de favorito de un libro.
     */
    @Query("UPDATE libros_biblioteca SET esFavorito = :esFavorito, fechaActualizacion = :ahora WHERE id = :id")
    suspend fun actualizarFavorito(id: String, esFavorito: Boolean, ahora: Long = System.currentTimeMillis())

    // ─── ELIMINAR ───────────────────────────────────────────────────────────

    /**
     * Elimina un libro específico de la biblioteca.
     */
    @Delete
    suspend fun eliminarLibro(libro: LibroBibliotecaEntidad)

    /**
     * Elimina un libro por su id.
     */
    @Query("DELETE FROM libros_biblioteca WHERE id = :id")
    suspend fun eliminarLibroPorId(id: String)

    // ─── CONSULTAS ──────────────────────────────────────────────────────────

    /**
     * Obtiene todos los libros de la biblioteca como Flow reactivo, ordenados por fecha.
     */
    @Query("SELECT * FROM libros_biblioteca ORDER BY fechaAgregado DESC")
    fun observarTodosLosLibros(): Flow<List<LibroBibliotecaEntidad>>

    /**
     * Obtiene todos los libros favoritos.
     */
    @Query("SELECT * FROM libros_biblioteca WHERE esFavorito = 1 ORDER BY fechaAgregado DESC")
    fun observarFavoritos(): Flow<List<LibroBibliotecaEntidad>>

    /**
     * Obtiene los libros filtrados por estado de lectura.
     */
    @Query("SELECT * FROM libros_biblioteca WHERE estadoLectura = :estado ORDER BY fechaAgregado DESC")
    fun observarLibrosPorEstado(estado: EstadoLectura): Flow<List<LibroBibliotecaEntidad>>

    /**
     * Busca un libro por su id (sin Flow, consulta puntual).
     */
    @Query("SELECT * FROM libros_biblioteca WHERE id = :id LIMIT 1")
    suspend fun obtenerLibroPorId(id: String): LibroBibliotecaEntidad?

    /**
     * Verifica si un libro ya está guardado en la biblioteca.
     */
    @Query("SELECT COUNT(*) FROM libros_biblioteca WHERE id = :id")
    suspend fun contarLibroPorId(id: String): Int

    /**
     * Obtiene el total de libros en la biblioteca.
     */
    @Query("SELECT COUNT(*) FROM libros_biblioteca")
    fun observarTotalLibros(): Flow<Int>

    /**
     * Obtiene el total de libros con estado LEIDO.
     */
    @Query("SELECT COUNT(*) FROM libros_biblioteca WHERE estadoLectura = 'LEIDO'")
    fun observarTotalLeidos(): Flow<Int>

    /**
     * Obtiene el total de libros con estado LEYENDO.
     */
    @Query("SELECT COUNT(*) FROM libros_biblioteca WHERE estadoLectura = 'LEYENDO'")
    fun observarTotalLeyendo(): Flow<Int>
}

