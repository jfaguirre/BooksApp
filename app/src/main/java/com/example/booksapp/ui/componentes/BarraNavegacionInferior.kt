package com.example.booksapp.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booksapp.R
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.SobrePrimario

/**
 * Elementos de navegación inferior en BooksApp.
 */
enum class DestinoNavegacion(
    val ruta: String,
    val idTexto: Int,
    val iconoSeleccionado: ImageVector,
    val iconoNoSeleccionado: ImageVector
) {
    INICIO("inicio", R.string.nav_inicio, Icons.Filled.Home, Icons.Outlined.Home),
    BUSCAR("buscar", R.string.nav_buscar, Icons.Filled.Search, Icons.Outlined.Search),
    MIS_LIBROS("mis_libros", R.string.nav_mis_libros, Icons.Filled.Book, Icons.Outlined.Book),
    MI_LECTURA("mi_lectura", R.string.nav_mi_lectura, Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook)
}

/**
 * Barra de navegación inferior estilizada siguiendo el diseño de Stitch con píldora activa.
 */
@Composable
fun BarraNavegacionInferior(
    destinoActual: DestinoNavegacion,
    alSeleccionarDestino: (DestinoNavegacion) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DestinoNavegacion.entries.forEach { destino ->
                val estaSeleccionado = destino == destinoActual

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { alSeleccionarDestino(destino) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Píldora indicadora
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 28.dp)
                            .clip(CircleShape)
                            .background(
                                if (estaSeleccionado) ContenedorPrimario else MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (estaSeleccionado) destino.iconoSeleccionado else destino.iconoNoSeleccionado,
                            contentDescription = stringResource(id = destino.idTexto),
                            tint = if (estaSeleccionado) SobrePrimario else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = destino.idTexto),
                        fontSize = 11.sp,
                        fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Medium,
                        color = if (estaSeleccionado) ContenedorPrimario else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
