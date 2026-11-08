package com.example.booksapp.datos.remoto.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la respuesta del endpoint /trending/daily.json de Open Library.
 */
@Serializable
data class RespuestaTendenciasDto(
    val query: String? = null,
    val works: List<ObraTendenciaDto> = emptyList()
)

@Serializable
data class ObraTendenciaDto(
    val key: String,
    val title: String,
    @SerialName("author_name") val nombresAutores: List<String> = emptyList(),
    @SerialName("cover_i") val coverId: Long? = null,
    @SerialName("first_publish_year") val primerAnioPublicacion: Int? = null
)
