package com.example.booksapp.ui.pantallas.lector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booksapp.R
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.repositorios.BibliotecaRepositorio
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario
import kotlinx.coroutines.launch

/**
 * Modos de visualización de fondo para el lector literario.
 */
enum class TemaLectura(
    val nombre: String,
    val colorFondo: Color,
    val colorTexto: Color,
    val colorBarra: Color
) {
    CLARO(
        nombre = "Claro",
        colorFondo = Color(0xFFFBFBFB),
        colorTexto = Color(0xFF1E1E1E),
        colorBarra = Color(0xFFFFFFFF)
    ),
    SEPIA(
        nombre = "Sepia",
        colorFondo = Color(0xFFF7EEDD),
        colorTexto = Color(0xFF382B1D),
        colorBarra = Color(0xFFEFE2CB)
    ),
    OSCURO(
        nombre = "Noche",
        colorFondo = Color(0xFF161616),
        colorTexto = Color(0xFFE5E5E5),
        colorBarra = Color(0xFF222222)
    )
}

/**
 * Pantalla de lectura interactiva para leer cualquier libro seleccionado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLectorLibro(
    libro: LibroModelo,
    alRegresar: () -> Unit,
    modifier: Modifier = Modifier,
    repositorio: BibliotecaRepositorio? = null
) {
    val corrutina = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val capitulos = remember(libro) { ContenidoLibrosLector.obtenerCapitulos(libro) }
    var capituloActualIndice by remember { mutableIntStateOf(0) }
    var tamanoFuenteSp by remember { mutableFloatStateOf(17f) }
    var temaSeleccionado by remember { mutableStateOf(TemaLectura.SEPIA) }
    var mostrarAjustes by remember { mutableStateOf(false) }
    var esFavoritoLocal by remember { mutableStateOf(libro.esFavorito) }
    var yaMarcadoLeido by remember { mutableStateOf(false) }

    // Al abrir el libro, se inicia su lectura en la base de datos local
    LaunchedEffect(libro.id) {
        repositorio?.iniciarLectura(libro)
        val guardado = repositorio?.obtenerLibroPorId(libro.id)
        if (guardado != null) {
            esFavoritoLocal = guardado.esFavorito
        }
    }

    val capituloActual = capitulos.getOrElse(capituloActualIndice) { capitulos.first() }
    val progresoLectura = (capituloActualIndice + 1).toFloat() / capitulos.size.toFloat()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = temaSeleccionado.colorFondo,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = temaSeleccionado.colorBarra,
                    titleContentColor = temaSeleccionado.colorTexto,
                    navigationIconContentColor = temaSeleccionado.colorTexto,
                    actionIconContentColor = temaSeleccionado.colorTexto
                ),
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = temaSeleccionado.colorTexto
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = libro.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = libro.obtenerTextoAutores(),
                            style = MaterialTheme.typography.labelSmall,
                            color = temaSeleccionado.colorTexto.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    // Botón para alternar favorito directamente en el lector
                    IconButton(
                        onClick = {
                            val nuevoEstado = !esFavoritoLocal
                            esFavoritoLocal = nuevoEstado
                            corrutina.launch {
                                val resultado = repositorio?.alternarFavorito(libro) ?: nuevoEstado
                                esFavoritoLocal = resultado
                                val mensaje = if (resultado) {
                                    "Añadido a tus favoritos"
                                } else {
                                    "Eliminado de tus favoritos"
                                }
                                snackbarHostState.showSnackbar(mensaje)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (esFavoritoLocal) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.inicio_cd_favorito),
                            tint = if (esFavoritoLocal) Secundario else temaSeleccionado.colorTexto.copy(alpha = 0.8f)
                        )
                    }

                    // Botón de personalización visual (tamaño y tema)
                    IconButton(onClick = { mostrarAjustes = !mostrarAjustes }) {
                        Icon(
                            imageVector = Icons.Filled.FormatSize,
                            contentDescription = stringResource(id = R.string.lector_fuente_tamano),
                            tint = temaSeleccionado.colorTexto
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = temaSeleccionado.colorBarra,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Barra de progreso y texto de capítulo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Capítulo ${capituloActualIndice + 1} de ${capitulos.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = temaSeleccionado.colorTexto.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(progresoLectura * 100).toInt()}% leído",
                            style = MaterialTheme.typography.labelSmall,
                            color = ContenedorPrimario,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progresoLectura },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ContenedorPrimario,
                        trackColor = temaSeleccionado.colorTexto.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Controles de navegación entre capítulos y marcar como leído
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (capituloActualIndice > 0) capituloActualIndice--
                            },
                            enabled = capituloActualIndice > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ContenedorSuperficieBajo,
                                contentColor = temaSeleccionado.colorTexto
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Anterior", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                yaMarcadoLeido = true
                                corrutina.launch {
                                    repositorio?.cambiarEstadoLectura(libro.id, EstadoLectura.LEIDO)
                                    snackbarHostState.showSnackbar("¡Felicitaciones! Has completado la lectura.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (yaMarcadoLeido) Terciario else ContenedorPrimario,
                                contentColor = SobrePrimario
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (yaMarcadoLeido) "¡Leído!" else "Marcar como Leído",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                if (capituloActualIndice < capitulos.size - 1) capituloActualIndice++
                            },
                            enabled = capituloActualIndice < capitulos.size - 1,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ContenedorSuperficieBajo,
                                contentColor = temaSeleccionado.colorTexto
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Siguiente", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {
            // Panel flotante de ajustes (tamaño de fuente y tema de lectura)
            AnimatedVisibility(
                visible = mostrarAjustes,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = temaSeleccionado.colorBarra),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Ajustes de Lectura",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = temaSeleccionado.colorTexto
                        )

                        // Selector de tamaño de letra
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tamaño: ${tamanoFuenteSp.toInt()} sp",
                                style = MaterialTheme.typography.bodySmall,
                                color = temaSeleccionado.colorTexto
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (tamanoFuenteSp > 13f) tamanoFuenteSp -= 2f
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ContenedorSuperficieAlto)
                                ) {
                                    Text("A-", color = temaSeleccionado.colorTexto, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        if (tamanoFuenteSp < 26f) tamanoFuenteSp += 2f
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ContenedorSuperficieAlto)
                                ) {
                                    Text("A+", color = temaSeleccionado.colorTexto, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Selector de tema (Claro / Sepia / Noche)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TemaLectura.entries.forEach { tema ->
                                val seleccionado = tema == temaSeleccionado
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(tema.colorFondo)
                                        .clickable { temaSeleccionado = tema }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tema.nombre,
                                        color = tema.colorTexto,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Selector horizontal rápido de capítulos
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(capitulos) { indice, capitulo ->
                    val esActivo = indice == capituloActualIndice
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (esActivo) ContenedorPrimario
                                else temaSeleccionado.colorBarra
                            )
                            .clickable { capituloActualIndice = indice }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Capítulo ${capitulo.numero}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (esActivo) FontWeight.Bold else FontWeight.Medium,
                            color = if (esActivo) SobrePrimario else temaSeleccionado.colorTexto
                        )
                    }
                }
            }

            // Cuerpo de lectura
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cabecera del libro y capítulo actual
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Miniatura de portada
                        Box(
                            modifier = Modifier
                                .size(width = 84.dp, height = 120.dp)
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

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = capituloActual.titulo,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = temaSeleccionado.colorTexto,
                            fontFamily = FontFamily.Serif
                        )

                        Text(
                            text = libro.obtenerTextoAutores(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = temaSeleccionado.colorTexto.copy(alpha = 0.7f)
                        )
                    }
                }

                // Párrafos del capítulo
                items(capituloActual.parrafos) { parrafo ->
                    Text(
                        text = parrafo,
                        color = temaSeleccionado.colorTexto,
                        fontSize = tamanoFuenteSp.sp,
                        lineHeight = (tamanoFuenteSp * 1.6f).sp,
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
