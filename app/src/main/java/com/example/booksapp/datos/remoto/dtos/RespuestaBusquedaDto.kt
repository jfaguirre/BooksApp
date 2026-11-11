package com.example.booksapp.datos.remoto.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la respuesta del endpoint /search.json de Open Library.
 */
@Serializable
data class RespuestaBusquedaDto(
    @SerialName("numFound") val cantidadEncontrada: Int? = null,
    val docs: List<DocumentoBusquedaDto> = emptyList()
)

@Serializable
data class DocumentoBusquedaDto(
    val key: String,
    val title: String,
    @SerialName("author_name") val nombresAutores: List<String> = emptyList(),
    @SerialName("author_key") val clavesAutores: List<String> = emptyList(),
    @SerialName("cover_i") val coverId: Long? = null,
    @SerialName("first_publish_year") val primerAnioPublicacion: Int? = null,
    val publisher: List<String> = emptyList(),
    @SerialName("ratings_average") val promedioCalificacion: Double? = null
)
