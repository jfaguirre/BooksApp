package com.example.booksapp

import android.app.Application
import com.example.booksapp.datos.local.BooksAppDatabase
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import com.example.booksapp.datos.repositorios.BibliotecaRepositorioImpl
import com.example.booksapp.datos.repositorios.UsuarioRepositorio
import com.example.booksapp.datos.repositorios.UsuarioRepositorioImpl

/**
 * Clase Application de BooksApp. Inicializa la base de datos Room
 * y expone los repositorios como singletons.
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

    /** Repositorio de preferencias y perfil del usuario (inicialización diferida). */
    val usuarioRepositorio: UsuarioRepositorio by lazy {
        UsuarioRepositorioImpl(this)
    }
}

