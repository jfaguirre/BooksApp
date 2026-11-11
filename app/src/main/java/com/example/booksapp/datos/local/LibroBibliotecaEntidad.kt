package com.example.booksapp.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa un libro guardado en la biblioteca personal del usuario.
 * Almacena tanto los datos del libro como el estado de lectura y favorito.
 */
@Entity(tableName = "libros_biblioteca")
data class LibroBibliotecaEntidad(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val autores: String,              // Lista serializada separada por "|"
    val descripcion: String = "",
    val urlPortada: String = "",
    val anioPublicacion: Int? = null,
    val calificacion: Double = 0.0,
    val cantidadCalificaciones: String = "",
    val etiqueta: String? = null,
    val esFavorito: Boolean = false,
    val estadoLectura: EstadoLectura = EstadoLectura.POR_LEER,
    val fechaAgregado: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long = System.currentTimeMillis()
)

