package com.example.booksapp.datos.remoto

import com.example.booksapp.datos.remoto.dtos.RespuestaBusquedaDto
import com.example.booksapp.datos.remoto.dtos.RespuestaMateriaDto
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
     * Busca libros mediante una consulta de texto libre (título, autor, ISBN).
     */
    @GET("search.json")
    suspend fun buscarLibros(
        @Query("q") consulta: String,
        @Query("limit") limite: Int = 10
    ): RespuestaBusquedaDto
}
