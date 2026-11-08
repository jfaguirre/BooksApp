package com.example.booksapp.ui.navegacion

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.booksapp.ui.pantallas.splash.PantallaSplash

/**
 * Grafo de navegación central de BooksApp.
 */
@Composable
fun GrafoNavegacion(
    modifier: Modifier = Modifier,
    controladorNavegacion: NavHostController = rememberNavController()
) {
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
            PantallaPrincipalContenedor()
        }
    }
}
