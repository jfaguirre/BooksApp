package com.example.booksapp

import android.app.Application
import com.example.booksapp.datos.local.BooksAppDatabase
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import com.example.booksapp.datos.repositorios.BibliotecaRepositorioImpl

/**
 * Clase Application de BooksApp. Inicializa la base de datos Room
 * y expone el repositorio de biblioteca como singleton.
 */
class BooksApplication : Application() {

    /** Base de datos Room (inicialización diferida). */
    val database: BooksAppDatabase by lazy {
        BooksAppDatabase.obtenerInstancia(this)
    }

    /** Repositorio de biblioteca personal (inicialización diferida). */
    val bibliotecaRepositorio: BibliotecaRepositorio by lazy {
        BibliotecaRepositorioImpl(database.bibliotecaDao())
    }
}

