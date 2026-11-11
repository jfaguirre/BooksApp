package com.example.booksapp.datos.remoto.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * DTO para la respuesta del endpoint /authors/{id}.json de Open Library.
 */
@Serializable
data class DetalleAutorDto(
    val key: String,
    val name: String,
    val bio: JsonElement? = null,
    @SerialName("birth_date") val fechaNacimiento: String? = null,
    @SerialName("death_date") val fechaMuerte: String? = null,
    val photos: List<Long> = emptyList()
) {
    /**
     * Extrae la biografía independientemente de si viene como String plano
     * o como un objeto JSON con formato {"type": "/type/text", "value": "..."}.
     */
    fun obtenerTextoBiografia(): String {
        return when (bio) {
            is JsonPrimitive -> bio.content
            is JsonObject -> (bio["value"] as? JsonPrimitive)?.content.orEmpty()
            else -> ""
        }
    }
}

