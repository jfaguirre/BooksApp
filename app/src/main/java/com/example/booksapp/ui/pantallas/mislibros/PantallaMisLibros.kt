package com.example.booksapp.ui.pantallas.mislibros

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booksapp.BooksApplication
import com.example.booksapp.R
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.ui.componentes.BarraSuperior
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSuperficie
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.DoradoCalificacion
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario
import com.example.booksapp.ui.tema.TerciarioFijo

/**
 * Pantalla "Mis Libros" — Biblioteca personal del usuario.
 * Permite ver, filtrar por pestaña, cambiar estados de lectura y gestionar favoritos.
 */
@Composable
fun PantallaMisLibros(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as BooksApplication
    val viewModel: MisLibrosViewModel = viewModel(
        factory = MisLibrosViewModel.Factory(application.bibliotecaRepositorio)
    )

    val estadoUi by viewModel.estadoUi.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para mostrar el snackbar cuando hay mensaje
    LaunchedEffect(estadoUi) {
        val estado = estadoUi as? MisLibrosEstadoUi.Exito ?: return@LaunchedEffect
        val mensaje = estado.mensajeSnackbar ?: return@LaunchedEffect
        val resultado = snackbarHostState.showSnackbar(
            message = mensaje,
            actionLabel = if (estado.libroEliminado != null) "Deshacer" else null
        )
        if (resultado == androidx.compose.material3.SnackbarResult.ActionPerformed) {
            viewModel.restaurarLibroEliminado()
        }
        viewModel.limpiarSnackbar()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraSuperior() },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { datos ->
                Snackbar(
                    snackbarData = datos,
                    containerColor = Terciario,
                    contentColor = Color.White,
                    actionColor = ContenedorPrimario.copy(alpha = 0.8f)
                )
            }
        }
    ) { relleno ->
        when (val estado = estadoUi) {
            is MisLibrosEstadoUi.Cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(relleno),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ContenedorPrimario)
                }
            }

            is MisLibrosEstadoUi.Exito -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(relleno)
                ) {
                    // Encabezado con estadísticas
                    EncabezadoBiblioteca(
                        total = estado.totalLibros,
                        leidos = estado.totalLeidos,
                        leyendo = estado.totalLeyendo
                    )

                    // Pestañas de filtrado
                    PestañasBiblioteca(
                        pestañaActual = estado.pestañaActual,
                        onSeleccionarPestaña = viewModel::seleccionarPestaña
                    )

                    // Contenido según pestaña
                    AnimatedContent(
                        targetState = estado.librosMostrados,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "libros_animacion"
                    ) { libros ->
                        if (libros.isEmpty()) {
                            ContenidoVacio(pestaña = estado.pestañaActual)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = libros,
                                    key = { it.id }
                                ) { libro ->
                                    TarjetaLibroBiblioteca(
                                        libro = libro,
                                        onAlternarFavorito = { viewModel.alternarFavorito(libro) },
                                        onCambiarEstado = { estado ->
                                            viewModel.cambiarEstadoLectura(libro, estado)
                                        },
                                        onEliminar = { viewModel.eliminarLibro(libro) }
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

// ─── COMPONENTES INTERNOS ─────────────────────────────────────────────────────

@Composable
private fun EncabezadoBiblioteca(
    total: Int,
    leidos: Int,
    leyendo: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Mi Biblioteca",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Primario
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ChipEstadistica(
                icono = Icons.Filled.LibraryBooks,
                valor = "$total",
                etiqueta = "Total",
                colorFondo = ContenedorSuperficie,
                colorIcono = ContenedorPrimario,
                modifier = Modifier.weight(1f)
            )
            ChipEstadistica(
                icono = Icons.AutoMirrored.Filled.MenuBook,
                valor = "$leyendo",
                etiqueta = "Leyendo",
                colorFondo = Secundario.copy(alpha = 0.12f),
                colorIcono = Secundario,
                modifier = Modifier.weight(1f)
            )
            ChipEstadistica(
                icono = Icons.Filled.CheckCircle,
                valor = "$leidos",
                etiqueta = "Leídos",
                colorFondo = Terciario.copy(alpha = 0.12f),
                colorIcono = Terciario,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ChipEstadistica(
    icono: ImageVector,
    valor: String,
    etiqueta: String,
    colorFondo: Color,
    colorIcono: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun PestañasBiblioteca(
    pestañaActual: PestañaBiblioteca,
    onSeleccionarPestaña: (PestañaBiblioteca) -> Unit,
    modifier: Modifier = Modifier
) {
    data class ItemPestaña(
        val pestaña: PestañaBiblioteca,
        val etiqueta: String,
        val icono: ImageVector
    )

    val pestañas = listOf(
        ItemPestaña(PestañaBiblioteca.TODOS, "Todos", Icons.Filled.LibraryBooks),
        ItemPestaña(PestañaBiblioteca.FAVORITOS, "Favoritos", Icons.Filled.Favorite),
        ItemPestaña(PestañaBiblioteca.POR_LEER, "Por leer", Icons.Filled.BookmarkAdd),
        ItemPestaña(PestañaBiblioteca.LEYENDO, "Leyendo", Icons.AutoMirrored.Filled.MenuBook),
        ItemPestaña(PestañaBiblioteca.LEIDO, "Leídos", Icons.Filled.CheckCircle)
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pestañas) { item ->
            val estaSeleccionada = item.pestaña == pestañaActual
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (estaSeleccionada) ContenedorPrimario
                        else ContenedorSuperficie
                    )
                    .clickable { onSeleccionarPestaña(item.pestaña) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = item.icono,
                    contentDescription = null,
                    tint = if (estaSeleccionada) SobrePrimario else Terciario,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = item.etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (estaSeleccionada) FontWeight.Bold else FontWeight.Medium,
                    color = if (estaSeleccionada) SobrePrimario else Terciario
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ContenidoVacio(
    pestaña: PestañaBiblioteca,
    modifier: Modifier = Modifier
) {
    val (icono, titulo, subtitulo) = when (pestaña) {
        PestañaBiblioteca.TODOS -> Triple(
            Icons.Filled.LibraryBooks,
            "Tu biblioteca está vacía",
            "Añade libros desde la pantalla de Inicio tocando \"Añadir\""
        )
        PestañaBiblioteca.FAVORITOS -> Triple(
            Icons.Outlined.FavoriteBorder,
            "Sin favoritos aún",
            "Marca libros como favoritos tocando el corazón"
        )
        PestañaBiblioteca.POR_LEER -> Triple(
            Icons.Filled.BookmarkAdd,
            "Sin libros por leer",
            "Guarda libros con estado \"Por leer\" para verlos aquí"
        )
        PestañaBiblioteca.LEYENDO -> Triple(
            Icons.AutoMirrored.Filled.MenuBook,
            "No estás leyendo nada",
            "Cambia el estado de un libro a \"Leyendo\" para seguir tu progreso"
        )
        PestañaBiblioteca.LEIDO -> Triple(
            Icons.Filled.CheckCircle,
            "Aún no has terminado ningún libro",
            "Marca un libro como \"Leído\" cuando lo termines"
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = ContenedorPrimario.copy(alpha = 0.4f),
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Tarjeta de libro dentro de la biblioteca personal con menú de opciones.
 */
@Composable
private fun TarjetaLibroBiblioteca(
    libro: LibroModelo,
    onAlternarFavorito: () -> Unit,
    onCambiarEstado: (EstadoLectura) -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Portada
            Box(
                modifier = Modifier
                    .size(width = 72.dp, height = 104.dp)
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

            // Información del libro
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
                Text(
                    text = libro.obtenerTextoAutores(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Chips de acciones: estado de lectura
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        ChipEstadoLectura(
                            estado = EstadoLectura.POR_LEER,
                            onClick = { onCambiarEstado(EstadoLectura.POR_LEER) }
                        )
                    }
                    item {
                        ChipEstadoLectura(
                            estado = EstadoLectura.LEYENDO,
                            onClick = { onCambiarEstado(EstadoLectura.LEYENDO) }
                        )
                    }
                    item {
                        ChipEstadoLectura(
                            estado = EstadoLectura.LEIDO,
                            onClick = { onCambiarEstado(EstadoLectura.LEIDO) }
                        )
                    }
                }
            }

            // Columna de acciones: favorito y más opciones
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(104.dp)
            ) {
                IconButton(
                    onClick = onAlternarFavorito,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (libro.esFavorito) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = Secundario,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { mostrarMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Más opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = mostrarMenu,
                        onDismissRequest = { mostrarMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Eliminar de biblioteca") },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = Secundario
                                )
                            },
                            onClick = {
                                mostrarMenu = false
                                onEliminar()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipEstadoLectura(
    estado: EstadoLectura,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (etiqueta, icono, colorFondo, colorTexto) = when (estado) {
        EstadoLectura.POR_LEER -> Cuadrupla(
            "Por leer",
            Icons.Filled.Schedule,
            ContenedorSuperficieAlto,
            Terciario
        )
        EstadoLectura.LEYENDO -> Cuadrupla(
            "Leyendo",
            Icons.AutoMirrored.Filled.MenuBook,
            Secundario.copy(alpha = 0.15f),
            Secundario
        )
        EstadoLectura.LEIDO -> Cuadrupla(
            "Leído",
            Icons.Filled.CheckCircle,
            Terciario.copy(alpha = 0.15f),
            Terciario
        )
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colorFondo)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorTexto,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = colorTexto,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

/** Helper data class para destructuring de 4 valores */
private data class Cuadrupla<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

private operator fun <A, B, C, D> Cuadrupla<A, B, C, D>.component1() = a
private operator fun <A, B, C, D> Cuadrupla<A, B, C, D>.component2() = b
private operator fun <A, B, C, D> Cuadrupla<A, B, C, D>.component3() = c
private operator fun <A, B, C, D> Cuadrupla<A, B, C, D>.component4() = d

