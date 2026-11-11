package com.example.booksapp.datos.local

/**
 * Estados posibles de lectura para un libro en la biblioteca personal.
 */
enum class EstadoLectura {
    /** El libro está en la lista de pendientes, aún no comenzado. */
    POR_LEER,

    /** El usuario está leyendo activamente el libro. */
    LEYENDO,

    /** El usuario ha terminado de leer el libro. */
    LEIDO
}

