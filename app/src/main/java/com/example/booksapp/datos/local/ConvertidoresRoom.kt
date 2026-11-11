package com.example.booksapp.datos.local

import androidx.room.TypeConverter

/**
 * Convertidores de tipos para Room: permite almacenar enums y listas en SQLite.
 */
class ConvertidoresRoom {

    // --- EstadoLectura ---
    @TypeConverter
    fun deEstadoLectura(estado: EstadoLectura): String = estado.name

    @TypeConverter
    fun aEstadoLectura(valor: String): EstadoLectura = EstadoLectura.valueOf(valor)
}

