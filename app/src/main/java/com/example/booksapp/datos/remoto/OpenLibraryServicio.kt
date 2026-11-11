package com.example.booksapp.datos.remoto

import com.example.booksapp.datos.remoto.dtos.DetalleAutorDto
import com.example.booksapp.datos.remoto.dtos.DetalleObraDto
import com.example.booksapp.datos.remoto.dtos.RespuestaBusquedaDto
import com.example.booksapp.datos.remoto.dtos.RespuestaMateriaDto
import com.example.booksapp.datos.remoto.dtos.RespuestaObrasAutorDto
import com.example.booksapp.datos.remoto.dtos.RespuestaTendenciasDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para interactuar con la API pública de Open Library.
 */
interface OpenLibraryServicio {

    /**
     * Obtiene una lista de libros correspondientes a una materia o categoría específica.
     */
    @GET("subjects/{materia}.json")
    suspend fun obtenerLibrosPorMateria(
        @Path("materia") materia: String,
        @Query("limit") limite: Int = 10
    ): RespuestaMateriaDto

    /**
     * Obtiene las tendencias diarias de lectura en Open Library.
     */
    @GET("trending/daily.json")
    suspend fun obtenerTendenciasDiarias(
        @Query("limit") limite: Int = 10
    ): RespuestaTendenciasDto

    /**
     * Busca libros mediante una consulta de texto libre (compatibilidad previa).
     */
    @GET("search.json")
    suspend fun buscarLibros(
        @Query("q") consulta: String,
        @Query("limit") limite: Int = 10
    ): RespuestaBusquedaDto

    /**
     * Búsqueda avanzada de libros permitiendo filtrar por consulta general, título o autor,
     * solicitando los campos requeridos de Open Library.
     */
    @GET("search.json")
    suspend fun buscarLibrosAvanzado(
        @Query("q") consultaGeneral: String? = null,
        @Query("title") titulo: String? = null,
        @Query("author") autor: String? = null,
        @Query("fields") campos: String = "key,title,author_name,author_key,cover_i,first_publish_year,publisher,ratings_average",
        @Query("limit") limite: Int = 20
    ): RespuestaBusquedaDto

    /**
     * Obtiene el detalle de una obra a través de su identificador.
     */
    @GET("works/{id}.json")
    suspend fun obtenerDetalleObra(
        @Path("id") idObra: String
    ): DetalleObraDto

    /**
     * Obtiene los datos biográficos de un autor a través de su identificador.
     */
    @GET("authors/{id}.json")
    suspend fun obtenerDetalleAutor(
        @Path("id") idAutor: String
    ): DetalleAutorDto

    /**
     * Obtiene las obras publicadas por un autor específico.
     */
    @GET("authors/{id}/works.json")
    suspend fun obtenerObrasAutor(
        @Path("id") idAutor: String,
        @Query("limit") limite: Int = 20
    ): RespuestaObrasAutorDto
}
