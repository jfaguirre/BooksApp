package com.example.booksapp.ui.pantallas.detalleObra

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.material3.Surface
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
import com.example.booksapp.ui.tema.ContenedorSuperficie
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.DoradoCalificacion
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario
import com.example.booksapp.ui.tema.TerciarioFijo
import java.util.Locale

/**
 * Pantalla de Detalle de Obra literaria con paleta editorial oficial, portada sombreada y carrusel del autor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleObra(
    idObra: String,
    alVolver: () -> Unit,
    alClicAutor: (String) -> Unit,
    alClicObra: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleObraViewModel = viewModel()
) {
    LaunchedEffect(idObra) {
        viewModel.cargarDetalleObra(idObra)
    }

    val estadoUi by viewModel.estadoUi.collectAsState()
    var descripcionExpandida by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.detalle_obra_titulo),
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
                actions = {
                    if (estadoUi is DetalleObraEstadoUi.Exito) {
                        val esFavorito = (estadoUi as DetalleObraEstadoUi.Exito).esFavorito
                        IconButton(onClick = { viewModel.alternarFavorito() }) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(id = R.string.detalle_cd_favorito),
                                tint = Secundario
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { rellenoSuperior ->
        when (val estado = estadoUi) {
            is DetalleObraEstadoUi.Cargando -> {
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
                            text = stringResource(id = R.string.detalle_estado_cargando_obra),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is DetalleObraEstadoUi.Error -> {
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

            is DetalleObraEstadoUi.Vacio -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.detalle_estado_vacio_obra),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is DetalleObraEstadoUi.Exito -> {
                val obra = estado.obra

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Hero: Portada centrada y sombreada con lomo físico
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(235.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ContenedorSuperficieAlto)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(obra.urlPortada.ifEmpty { R.drawable.booksapp })
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(id = R.drawable.booksapp),
                                    error = painterResource(id = R.drawable.booksapp),
                                    contentDescription = stringResource(id = R.string.detalle_cd_portada),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Sombra de lomo físico izquierdo
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(6.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(Color.Black.copy(alpha = 0.42f), Color.Transparent)
                                            )
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Título de la obra
                            Text(
                                text = obra.titulo,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Primario,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Autor(es) tocables
                            val claveAutorPrincipal = obra.obtenerClavePrimerAutor()
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(enabled = claveAutorPrincipal.isNotEmpty()) {
                                        alClicAutor(claveAutorPrincipal)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = Secundario,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = obra.obtenerTextoAutores(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Secundario,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Metadatos (Año y Calificación)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                obra.anioPublicacion?.let { anio ->
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(ContenedorSuperficie)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.detalle_publicado_en, anio),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                obra.calificacion?.let { calif ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(TerciarioFijo)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = DoradoCalificacion,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.1f", calif),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Terciario
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Botón principal de guardado en biblioteca
                            if (estado.estaGuardado) {
                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = ContenedorSuperficieAlto,
                                    modifier = Modifier.fillMaxWidth(0.85f).height(46.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.BookmarkAdded,
                                            contentDescription = null,
                                            tint = Secundario,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(id = R.string.detalle_guardado_biblioteca),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.guardarEnBiblioteca(obra) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ContenedorPrimario,
                                        contentColor = SobrePrimario
                                    ),
                                    shape = RoundedCornerShape(9999.dp),
                                    modifier = Modifier.fillMaxWidth(0.85f).height(46.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.BookmarkAdd,
                                        contentDescription = null,
                                        tint = SobrePrimario,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.detalle_agregar_biblioteca),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Sección: Sinopsis con Ver más / Ver menos
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ContenedorSuperficieBajo)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.detalle_seccion_sinopsis),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Primario
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val textoSinopsis = obra.descripcion.ifEmpty {
                                stringResource(id = R.string.detalle_sin_descripcion)
                            }

                            val esLargo = textoSinopsis.length > 200

                            Text(
                                text = textoSinopsis,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = if (!esLargo || descripcionExpandida) Int.MAX_VALUE else 4,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 22.sp,
                                modifier = Modifier.animateContentSize()
                            )

                            if (esLargo) {
                                TextButton(
                                    onClick = { descripcionExpandida = !descripcionExpandida },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(
                                        text = if (descripcionExpandida) {
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

                    // Sección: Temas o Categorías
                    if (obra.temas.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = stringResource(id = R.string.detalle_seccion_temas),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(obra.temas) { tema ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(TerciarioFijo)
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = tema,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Terciario,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sección: Más de este autor (LazyRow)
                    if (obra.obrasRelacionadasAutor.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = stringResource(id = R.string.detalle_seccion_mas_autor),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primario
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    items(obra.obrasRelacionadasAutor, key = { it.id }) { otraObra ->
                                        TarjetaObraRelacionada(
                                            libro = otraObra,
                                            alHacerClic = { alClicObra(otraObra.obtenerIdLimpio()) }
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
 * Tarjeta compacta para la sección "Más de este autor".
 */
@Composable
private fun TarjetaObraRelacionada(
    libro: LibroModelo,
    alHacerClic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable { alHacerClic() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ContenedorSuperficieBajo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                        .width(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = libro.titulo,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            libro.anioPublicacion?.let { anio ->
                Text(
                    text = anio.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

