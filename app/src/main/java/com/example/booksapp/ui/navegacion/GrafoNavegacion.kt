package com.example.booksapp.ui.navegacion

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.booksapp.BooksApplication
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.ui.pantallas.lector.PantallaLectorLibro
import com.example.booksapp.ui.pantallas.splash.PantallaSplash

/**
 * Grafo de navegación central de BooksApp.
 */
@Composable
fun GrafoNavegacion(
    modifier: Modifier = Modifier,
    controladorNavegacion: NavHostController = rememberNavController()
) {
    var libroParaLectura by remember { mutableStateOf<LibroModelo?>(null) }

    NavHost(
        navController = controladorNavegacion,
        startDestination = Ruta.Splash.ruta,
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(route = Ruta.Splash.ruta) {
            PantallaSplash(
                alFinalizarSplash = {
                    controladorNavegacion.navigate(Ruta.Principal.ruta) {
                        popUpTo(Ruta.Splash.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Ruta.Principal.ruta) {
            PantallaPrincipalContenedor(
                alAbrirLector = { libro ->
                    libroParaLectura = libro
                    controladorNavegacion.navigate(Ruta.Lector.ruta)
                }
            )
        }

        composable(route = Ruta.Lector.ruta) {
            val context = LocalContext.current
            val application = context.applicationContext as BooksApplication
            libroParaLectura?.let { libro ->
                PantallaLectorLibro(
                    libro = libro,
                    alRegresar = { controladorNavegacion.popBackStack() },
                    repositorio = application.bibliotecaRepositorio
                )
            }
        }
    }
}
