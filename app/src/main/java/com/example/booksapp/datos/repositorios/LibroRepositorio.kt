package com.example.booksapp.datos.repositorios

import com.example.booksapp.datos.modelos.DetalleAutorModelo
import com.example.booksapp.datos.modelos.DetalleObraModelo
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.modelos.TipoFiltroBusqueda

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

    /**
     * Realiza una búsqueda avanzada filtrada por Todos, Título, Autor o Categoría.
     */
    suspend fun buscarLibrosPorFiltro(
        consulta: String,
        tipoFiltro: TipoFiltroBusqueda,
        limite: Int = 20
    ): Result<List<LibroModelo>>

    /**
     * Obtiene la información detallada de una obra literaria.
     */
    suspend fun obtenerDetalleObra(idObra: String): Result<DetalleObraModelo>

    /**
     * Obtiene la información biográfica y bibliográfica de un autor.
     */
    suspend fun obtenerDetalleAutor(idAutor: String): Result<DetalleAutorModelo>

    /**
     * Obtiene las obras pertenecientes a un autor, permitiendo excluir una obra (ej. la actual).
     */
    suspend fun obtenerObrasDeAutor(
        idAutor: String,
        excluirIdObra: String? = null,
        limite: Int = 10
    ): Result<List<LibroModelo>>
}
