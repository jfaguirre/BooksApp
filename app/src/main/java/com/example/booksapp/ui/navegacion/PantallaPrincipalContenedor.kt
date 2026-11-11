package com.example.booksapp.ui.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booksapp.R
import com.example.booksapp.ui.componentes.BarraNavegacionInferior
import com.example.booksapp.ui.componentes.DestinoNavegacion
import com.example.booksapp.ui.pantallas.inicio.PantallaInicio
import com.example.booksapp.ui.pantallas.mislibros.PantallaMisLibros
import com.example.booksapp.ui.tema.ContenedorPrimario

/**
 * Contenedor principal que aloja la barra de navegación inferior y alterna entre las pestañas.
 */
@Composable
fun PantallaPrincipalContenedor(
    modifier: Modifier = Modifier
) {
    var destinoActual by remember { mutableStateOf(DestinoNavegacion.INICIO) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BarraNavegacionInferior(
                destinoActual = destinoActual,
                alSeleccionarDestino = { destinoActual = it }
            )
        }
    ) { rellenoInferior ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = rellenoInferior.calculateBottomPadding())
        ) {
            when (destinoActual) {
                DestinoNavegacion.INICIO -> {
                    PantallaInicio()
                }

                DestinoNavegacion.MIS_LIBROS -> {
                    PantallaMisLibros()
                }

                DestinoNavegacion.BUSCAR,
                DestinoNavegacion.MI_LECTURA -> {
                    PantallaProximamente(destino = destinoActual)
                }
            }
        }
    }
}

/**
 * Pantalla transitoria para las secciones que se implementarán posteriormente.
 */
@Composable
private fun PantallaProximamente(
    destino: DestinoNavegacion,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Construction,
                contentDescription = null,
                tint = ContenedorPrimario,
                modifier = Modifier.size(56.dp)
            )

            Text(
                text = stringResource(id = destino.idTexto),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(id = R.string.mensaje_proximamente),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
