package com.example.booksapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.booksapp.ui.navegacion.GrafoNavegacion
import com.example.booksapp.ui.tema.BooksAppTema

/**
 * Actividad principal de la aplicación BooksApp.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BooksAppTema {
                GrafoNavegacion()
            }
        }
    }
}
