package com.example.booksapp.ui.pantallas.lectura

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booksapp.BooksApplication
import com.example.booksapp.R
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.ui.componentes.BarraSuperior
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.DoradoCalificacion
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario
import com.example.booksapp.ui.tema.TerciarioFijo
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra la lista completa de libros que el usuario está leyendo actualmente,
 * permitiéndole seleccionar y continuar la lectura de cualquiera de ellos.
 */
@Composable
fun PantallaMiLectura(
    alAbrirLector: (LibroModelo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as BooksApplication
    val repositorio = application.bibliotecaRepositorio
    val librosEnLectura by repositorio.observarLibrosEnLectura().collectAsState(initial = emptyList())

    val corrutina = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraSuperior() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { relleno ->
        if (librosEnLectura.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(relleno),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Encabezado de la sección
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = Secundario,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.mi_lectura_titulo),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primario
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(
                                id = R.string.mi_lectura_libros_en_curso,
                                librosEnLectura.size
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Lista de libros que se están leyendo
                items(
                    items = librosEnLectura,
                    key = { it.id }
                ) { libro ->
                    TarjetaLibroEnLectura(
                        libro = libro,
                        alHacerClicContinuar = { alAbrirLector(libro) },
                        alMarcarLeido = {
                            corrutina.launch {
                                repositorio.cambiarEstadoLectura(libro.id, EstadoLectura.LEIDO)
                                snackbarHostState.showSnackbar(
                                    "\"${libro.titulo}\" marcado como completado"
                                )
                            }
                        }
                    )
                }
            }
        } else {
            // Estado vacío cuando no hay libros en lectura
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(relleno)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(ContenedorSuperficieAlto),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoStories,
                            contentDescription = null,
                            tint = ContenedorPrimario,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.mi_lectura_sin_libro_titulo),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(id = R.string.mi_lectura_sin_libro_subtitulo),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta individual para cada libro en curso dentro de la sección "Mi Lectura".
 */
@Composable
private fun TarjetaLibroEnLectura(
    libro: LibroModelo,
    alHacerClicContinuar: () -> Unit,
    alMarcarLeido: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { alHacerClicContinuar() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portada del libro
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(122.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ContenedorSuperficieAlto)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(libro.urlPortada.ifEmpty { null })
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.booksapp),
                    error = painterResource(id = R.drawable.booksapp),
                    contentDescription = libro.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Información y botones de acción
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge "Leyendo"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Secundario.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Leyendo",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secundario,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        // Calificación
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = DoradoCalificacion,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = String.format("%.1f", libro.calificacion),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Terciario,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = libro.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = libro.obtenerTextoAutores(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fila de acciones: Continuar lectura y Marcar como leído
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = alHacerClicContinuar,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ContenedorPrimario,
                            contentColor = SobrePrimario
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.mi_lectura_continuar_corto),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = alMarcarLeido,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ContenedorSuperficieAlto)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(id = R.string.lector_marcar_leido),
                            tint = Terciario,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
