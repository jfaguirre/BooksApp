package com.example.booksapp.datos.remoto.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * DTO para la respuesta del endpoint /authors/{id}/works.json de Open Library.
 */
@Serializable
data class RespuestaObrasAutorDto(
    val size: Int? = null,
    val entries: List<EntradaObraAutorDto> = emptyList()
)

/**
 * Obra individual devuelta en la lista de obras del autor.
 */
@Serializable
data class EntradaObraAutorDto(
    val key: String,
    val title: String,
    val covers: List<Long> = emptyList(),
    val description: JsonElement? = null,
    val subjects: List<String> = emptyList(),
    @SerialName("first_publish_date") val primeraFechaPublicacion: String? = null
) {
    fun obtenerTextoDescripcion(): String {
        return when (description) {
            is JsonPrimitive -> description.content
            is JsonObject -> (description["value"] as? JsonPrimitive)?.content.orEmpty()
            else -> ""
        }
    }
}

