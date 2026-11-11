package com.example.booksapp.ui.pantallas.detalleAutor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booksapp.R
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.ContornoVariante
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario

/**
 * Pantalla de Detalle de Autor con foto circular, periodo de vida, biografía expandible y catálogo de obras.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleAutor(
    idAutor: String,
    alVolver: () -> Unit,
    alClicObra: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleAutorViewModel = viewModel()
) {
    LaunchedEffect(idAutor) {
        viewModel.cargarDetalleAutor(idAutor)
    }

    val estadoUi by viewModel.estadoUi.collectAsState()
    var biografiaExpandida by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.detalle_autor_titulo),
                        style = MaterialTheme.typography.titleLarge,
                        color = ContenedorPrimario,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.detalle_cd_volver),
                            tint = Primario
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { rellenoSuperior ->
        when (val estado = estadoUi) {
            is DetalleAutorEstadoUi.Cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = ContenedorPrimario,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.detalle_estado_cargando_autor),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is DetalleAutorEstadoUi.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (estado.esErrorRed) {
                                stringResource(id = R.string.buscar_estado_error_red)
                            } else {
                                estado.mensaje
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.reintentar() },
                            colors = ButtonDefaults.buttonColors(containerColor = ContenedorPrimario)
                        ) {
                            Text(text = stringResource(id = R.string.boton_reintentar))
                        }
                    }
                }
            }

            is DetalleAutorEstadoUi.Vacio -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.detalle_estado_vacio_autor),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is DetalleAutorEstadoUi.Exito -> {
                val autor = estado.autor

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // Hero: Foto circular y datos biográficos
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(116.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, ContenedorPrimario, CircleShape)
                                    .background(ContenedorSuperficieAlto),
                                contentAlignment = Alignment.Center
                            ) {
                                if (autor.urlFoto.isNotEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(autor.urlFoto)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(id = R.drawable.booksapp),
                                        error = painterResource(id = R.drawable.booksapp),
                                        contentDescription = stringResource(id = R.string.detalle_cd_foto_autor),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = ContenedorPrimario,
                                        modifier = Modifier.size(54.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Nombre del autor
                            Text(
                                text = autor.nombre,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Primario,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Periodo de vida (nacimiento - muerte)
                            val periodoVida = autor.obtenerPeriodoVida()
                            if (periodoVida.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = periodoVida,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Sección: Biografía
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ContenedorSuperficieBajo)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.detalle_seccion_biografia),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Primario
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val textoBio = autor.biografia.ifEmpty {
                                stringResource(id = R.string.detalle_sin_biografia)
                            }
                            val esLargo = textoBio.length > 200

                            Text(
                                text = textoBio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = if (!esLargo || biografiaExpandida) Int.MAX_VALUE else 4,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 22.sp,
                                modifier = Modifier.animateContentSize()
                            )

                            if (esLargo) {
                                TextButton(
                                    onClick = { biografiaExpandida = !biografiaExpandida },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(
                                        text = if (biografiaExpandida) {
                                            stringResource(id = R.string.detalle_ver_menos)
                                        } else {
                                            stringResource(id = R.string.detalle_ver_mas)
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Secundario,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Sección: Obras del autor
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.detalle_seccion_obras_autor),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Primario
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (autor.obras.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.detalle_sin_obras_adicionales),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    autor.obras.forEach { obra ->
                                        TarjetaObraDeAutorFila(
                                            libro = obra,
                                            alHacerClic = { alClicObra(obra.obtenerIdLimpio()) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila horizontal para mostrar cada obra del catálogo del autor.
 */
@Composable
private fun TarjetaObraDeAutorFila(
    libro: LibroModelo,
    alHacerClic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { alHacerClic() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(88.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ContenedorSuperficieAlto)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(libro.urlPortada.ifEmpty { R.drawable.booksapp })
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.booksapp),
                    error = painterResource(id = R.drawable.booksapp),
                    contentDescription = libro.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                libro.anioPublicacion?.let { anio ->
                    Text(
                        text = anio.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (libro.temas.isNotEmpty()) {
                    Text(
                        text = libro.temas.take(3).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = Secundario,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
