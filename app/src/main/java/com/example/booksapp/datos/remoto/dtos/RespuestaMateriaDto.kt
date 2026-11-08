package com.example.booksapp.datos.remoto.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para la respuesta del endpoint /subjects/{subject}.json de Open Library.
 */
@Serializable
data class RespuestaMateriaDto(
    val name: String? = null,
    @SerialName("work_count") val conteoObras: Int? = null,
    val works: List<ObraMateriaDto> = emptyList()
)

@Serializable
data class ObraMateriaDto(
    val key: String,
    val title: String,
    @SerialName("cover_id") val coverId: Long? = null,
    @SerialName("first_publish_year") val primerAnioPublicacion: Int? = null,
    val authors: List<AutorMateriaDto> = emptyList(),
    val subject: List<String> = emptyList()
)

@Serializable
data class AutorMateriaDto(
    val name: String? = null,
    val key: String? = null
)
