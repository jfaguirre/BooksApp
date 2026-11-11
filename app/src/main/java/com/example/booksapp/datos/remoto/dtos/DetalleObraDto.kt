package com.example.booksapp.datos.remoto.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * DTO para la respuesta del endpoint /works/{id}.json de Open Library.
 */
@Serializable
data class DetalleObraDto(
    val key: String,
    val title: String,
    val description: JsonElement? = null,
    val covers: List<Long> = emptyList(),
    val subjects: List<String> = emptyList(),
    val authors: List<AutorReferenciaDto> = emptyList(),
    @SerialName("first_publish_date") val primeraFechaPublicacion: String? = null
) {
    /**
     * Extrae el texto descriptivo independientemente de si viene como String plano
     * o como un objeto JSON con formato {"type": "/type/text", "value": "..."}.
     */
    fun obtenerTextoDescripcion(): String {
        return when (description) {
            is JsonPrimitive -> description.content
            is JsonObject -> (description["value"] as? JsonPrimitive)?.content.orEmpty()
            else -> ""
        }
    }
}

/**
 * Representa la referencia al autor dentro de la obra en Open Library.
 */
@Serializable
data class AutorReferenciaDto(
    val author: EnlaceClaveDto? = null,
    val type: EnlaceClaveDto? = null
)

/**
 * Objeto genérico para claves de relación en Open Library como {"key": "/authors/OL123A"}.
 */
@Serializable
data class EnlaceClaveDto(
    val key: String? = null
)

