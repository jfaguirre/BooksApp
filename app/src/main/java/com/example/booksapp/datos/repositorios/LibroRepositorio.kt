package com.example.booksapp.datos.repositorios

import com.example.booksapp.datos.modelos.LibroModelo

/**
 * Contrato de repositorio para el acceso a datos de libros desde fuentes remotas y locales.
 */
interface LibroRepositorio {

    /**
     * Obtiene una lista de libros correspondientes a una materia o categoría.
     */
    suspend fun obtenerLibrosPorMateria(materia: String, limite: Int = 8): Result<List<LibroModelo>>

    /**
     * Obtiene las obras en tendencia de la comunidad.
     */
    suspend fun obtenerTendencias(limite: Int = 5): Result<List<LibroModelo>>

    /**
     * Obtiene la obra destacada o "Libro del Momento".
     */
    suspend fun obtenerLibroDestacado(): Result<LibroModelo>

    /**
     * Realiza una búsqueda de libros por término clave.
     */
    suspend fun buscarLibros(consulta: String, limite: Int = 10): Result<List<LibroModelo>>
}
