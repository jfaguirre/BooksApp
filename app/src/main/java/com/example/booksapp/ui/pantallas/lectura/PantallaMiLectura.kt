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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.booksapp.BooksApplication
import com.example.booksapp.R
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.ui.componentes.BarraSuperior
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSecundario
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.ContenedorTerciario
import com.example.booksapp.ui.tema.DoradoCalificacion
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.PrimarioFijo
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario

@Composable
fun PantallaMiLectura(
    alAbrirLector: (LibroModelo) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as BooksApplication
    val repositorio = application.bibliotecaRepositorio

    val viewModel: MiLecturaViewModel = viewModel(
        factory = MiLecturaViewModel.Factory(repositorio)
    )

    val estadoUi by viewModel.estadoUi.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar snackbar cuando hay mensaje
    val mensajeSnackbar = (estadoUi as? MiLecturaEstadoUi.Exito)?.mensajeSnackbar
    LaunchedEffect(mensajeSnackbar) {
        mensajeSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraSuperior() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { relleno ->
        when (val estado = estadoUi) {
            is MiLecturaEstadoUi.Cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(relleno),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ContenedorPrimario)
                }
            }

            is MiLecturaEstadoUi.Exito -> {
                if (estado.bibliotecaVacia) {
                    // Estado vacío: biblioteca completamente vacía
                    EstadoVacioGlobal(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(relleno)
                    )
                } else {
                    ContenidoMiLectura(
                        estado = estado,
                        alAbrirLector = alAbrirLector,
                        alIniciarLectura = { id, titulo ->
                            viewModel.iniciarLectura(id, titulo)
                        },
                        alMarcarLeido = { id, titulo ->
                            viewModel.marcarComoLeido(id, titulo)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(relleno)
                    )
                }
            }
        }
    }
}


@Composable
private fun ContenidoMiLectura(
    estado: MiLecturaEstadoUi.Exito,
    alAbrirLector: (LibroModelo) -> Unit,
    alIniciarLectura: (String, String) -> Unit,
    alMarcarLeido: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ─── ENCABEZADO ─────────────────────────────────────────────────
        item {
            EncabezadoMiLectura()
        }

        // ─── A. RESUMEN DE LECTURA ──────────────────────────────────────
        item {
            SeccionResumenLectura(
                totalBiblioteca = estado.totalLibros,
                totalPorLeer = estado.totalPorLeer,
                totalLeyendo = estado.totalLeyendo,
                totalLeidos = estado.totalLeidos
            )
        }

        // ─── B. ACTUALMENTE LEYENDO ─────────────────────────────────────
        item {
            TituloSeccion(
                titulo = stringResource(R.string.mi_lectura_seccion_leyendo),
                icono = Icons.Filled.AutoStories,
                colorIcono = Secundario,
                cantidad = estado.totalLeyendo
            )
        }

        if (estado.librosLeyendo.isEmpty()) {
            item {
                TarjetaEstadoVacio(
                    icono = Icons.Outlined.AutoStories,
                    titulo = stringResource(R.string.mi_lectura_vacio_leyendo_titulo),
                    subtitulo = stringResource(R.string.mi_lectura_vacio_leyendo_subtitulo)
                )
            }
        } else {
            items(
                items = estado.librosLeyendo,
                key = { "leyendo_${it.id}" }
            ) { libro ->
                TarjetaLibroEnLectura(
                    libro = libro,
                    alHacerClicContinuar = { alAbrirLector(libro) },
                    alMarcarLeido = { alMarcarLeido(libro.id, libro.titulo) }
                )
            }
        }

        // ─── C. PRÓXIMAS LECTURAS ───────────────────────────────────────
        item {
            TituloSeccion(
                titulo = stringResource(R.string.mi_lectura_seccion_proximas),
                icono = Icons.Filled.BookmarkAdd,
                colorIcono = Terciario,
                cantidad = estado.totalPorLeer
            )
        }

        if (estado.librosPorLeer.isEmpty()) {
            item {
                TarjetaEstadoVacio(
                    icono = Icons.Outlined.BookmarkBorder,
                    titulo = stringResource(R.string.mi_lectura_vacio_proximas_titulo),
                    subtitulo = stringResource(R.string.mi_lectura_vacio_proximas_subtitulo)
                )
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(
                        items = estado.librosPorLeer,
                        key = { "porleer_${it.id}" }
                    ) { libro ->
                        TarjetaLibroProximaLectura(
                            libro = libro,
                            alIniciarLectura = { alIniciarLectura(libro.id, libro.titulo) },
                            alAbrirDetalle = { alAbrirLector(libro) }
                        )
                    }
                }
            }
        }

        // ─── D. LIBROS TERMINADOS ───────────────────────────────────────
        item {
            TituloSeccion(
                titulo = stringResource(R.string.mi_lectura_seccion_terminados),
                icono = Icons.Filled.CheckCircle,
                colorIcono = ContenedorTerciario,
                cantidad = estado.totalLeidos
            )
        }

        if (estado.librosLeidos.isEmpty()) {
            item {
                TarjetaEstadoVacio(
                    icono = Icons.Outlined.CheckCircle,
                    titulo = stringResource(R.string.mi_lectura_vacio_terminados_titulo),
                    subtitulo = stringResource(R.string.mi_lectura_vacio_terminados_subtitulo)
                )
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(
                        items = estado.librosLeidos,
                        key = { "leido_${it.id}" }
                    ) { libro ->
                        TarjetaLibroTerminado(
                            libro = libro,
                            alAbrirDetalle = { alAbrirLector(libro) }
                        )
                    }
                }
            }
        }

        // ─── E. ESTADÍSTICAS DE LECTURA ─────────────────────────────────
        item {
            TituloSeccion(
                titulo = stringResource(R.string.mi_lectura_seccion_estadisticas),
                icono = Icons.Filled.BarChart,
                colorIcono = ContenedorPrimario
            )
        }

        item {
            SeccionEstadisticas(
                totalLeidos = estado.totalLeidos,
                totalLeyendo = estado.totalLeyendo,
                totalPorLeer = estado.totalPorLeer,
                porcentajeTerminados = estado.porcentajeTerminados
            )
        }

        // Espacio final
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ENCABEZADO
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EncabezadoMiLectura(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = Secundario,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(R.string.mi_lectura_titulo),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Primario
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// A. RESUMEN DE LECTURA
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SeccionResumenLectura(
    totalBiblioteca: Int,
    totalPorLeer: Int,
    totalLeyendo: Int,
    totalLeidos: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Primario),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Primario, ContenedorPrimario)
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.mi_lectura_seccion_resumen).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimarioFijo,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ItemResumen(
                        cantidad = totalBiblioteca,
                        etiqueta = stringResource(R.string.mi_lectura_total_biblioteca),
                        icono = Icons.Filled.LibraryBooks
                    )
                    ItemResumen(
                        cantidad = totalPorLeer,
                        etiqueta = stringResource(R.string.mi_lectura_total_por_leer),
                        icono = Icons.Filled.Book
                    )
                    ItemResumen(
                        cantidad = totalLeyendo,
                        etiqueta = stringResource(R.string.mi_lectura_total_leyendo),
                        icono = Icons.Filled.AutoStories
                    )
                    ItemResumen(
                        cantidad = totalLeidos,
                        etiqueta = stringResource(R.string.mi_lectura_total_leidos),
                        icono = Icons.Filled.CheckCircle
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemResumen(
    cantidad: Int,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SobrePrimario.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = PrimarioFijo,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = cantidad.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = SobrePrimario,
            fontSize = 20.sp
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = PrimarioFijo,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// B. TARJETA LIBRO EN LECTURA (LEYENDO)
// ═══════════════════════════════════════════════════════════════════════════════

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
                                text = stringResource(R.string.mi_lectura_total_leyendo),
                                style = MaterialTheme.typography.labelSmall,
                                color = Secundario,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        // Calificación
                        if (libro.calificacion > 0) {
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

@Composable
private fun TarjetaLibroProximaLectura(
    libro: LibroModelo,
    alIniciarLectura: () -> Unit,
    alAbrirDetalle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .clickable { alAbrirDetalle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Portada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
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

                // Badge estado
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Terciario.copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.mi_lectura_badge_por_leer),
                        style = MaterialTheme.typography.labelSmall,
                        color = SobrePrimario,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }

            // Información
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = libro.obtenerTextoAutores(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón "Empezar a leer"
                Button(
                    onClick = alIniciarLectura,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terciario,
                        contentColor = SobrePrimario
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.mi_lectura_boton_empezar),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// D. TARJETA LIBRO TERMINADO (LEIDO)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TarjetaLibroTerminado(
    libro: LibroModelo,
    alAbrirDetalle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .clickable { alAbrirDetalle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Portada con overlay de completado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
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

                // Badge leído
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ContenedorTerciario.copy(alpha = 0.90f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SobrePrimario,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = stringResource(R.string.mi_lectura_badge_leido),
                            style = MaterialTheme.typography.labelSmall,
                            color = SobrePrimario,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Información
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = libro.obtenerTextoAutores(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )

                // Calificación si existe
                if (libro.calificacion > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = DoradoCalificacion,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = String.format("%.1f", libro.calificacion),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Terciario,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeccionEstadisticas(
    totalLeidos: Int,
    totalLeyendo: Int,
    totalPorLeer: Int,
    porcentajeTerminados: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Fila de estadísticas individuales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ItemEstadistica(
                    valor = totalLeidos.toString(),
                    etiqueta = stringResource(R.string.mi_lectura_estadistica_terminados),
                    color = ContenedorTerciario
                )
                ItemEstadistica(
                    valor = totalLeyendo.toString(),
                    etiqueta = stringResource(R.string.mi_lectura_estadistica_en_progreso),
                    color = Secundario
                )
                ItemEstadistica(
                    valor = totalPorLeer.toString(),
                    etiqueta = stringResource(R.string.mi_lectura_estadistica_pendientes),
                    color = Terciario
                )
            }

            // Barra de porcentaje completado
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.mi_lectura_estadistica_porcentaje),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format("%.0f%%", porcentajeTerminados),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ContenedorPrimario
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Barra de progreso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(ContenedorSuperficieAlto)
                ) {
                    val fraccion = (porcentajeTerminados / 100f).coerceIn(0f, 1f)
                    if (fraccion > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraccion)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(ContenedorPrimario, ContenedorSecundario)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemEstadistica(
    valor: String,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// F. ESTADOS VACÍOS
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Estado vacío global cuando la biblioteca está completamente vacía.
 */
@Composable
private fun EstadoVacioGlobal(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(ContenedorSuperficieAlto),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CollectionsBookmark,
                    contentDescription = null,
                    tint = ContenedorPrimario,
                    modifier = Modifier.size(44.dp)
                )
            }

            Text(
                text = stringResource(R.string.mi_lectura_vacio_biblioteca_titulo),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.mi_lectura_vacio_biblioteca_subtitulo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Tarjeta de estado vacío reutilizable para cada sección.
 */
@Composable
private fun TarjetaEstadoVacio(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(ContenedorSuperficieAlto),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = ContenedorPrimario.copy(alpha = 0.6f),
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TituloSeccion(
    titulo: String,
    icono: ImageVector,
    colorIcono: Color,
    modifier: Modifier = Modifier,
    cantidad: Int? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorIcono,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (cantidad != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorIcono.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = cantidad.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorIcono,
                    fontSize = 11.sp
                )
            }
        }
    }
}
