package com.example.booksapp.ui.pantallas.busqueda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booksapp.R
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.datos.modelos.TipoFiltroBusqueda
import com.example.booksapp.ui.componentes.BarraSuperior
import com.example.booksapp.ui.componentes.TarjetaLibroBusqueda
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSecundario
import com.example.booksapp.ui.tema.ContenedorSuperficie
import com.example.booksapp.ui.tema.ContenedorSuperficieAlto
import com.example.booksapp.ui.tema.ContenedorTerciario
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.SobreSecundario
import com.example.booksapp.ui.tema.SobreTerciario
import com.example.booksapp.ui.tema.Terciario

/**
 * Pantalla "Buscar Libros" replicando el diseño oficial de Stitch.
 */
@Composable
fun PantallaBuscarLibros(
    modifier: Modifier = Modifier,
    viewModel: BuscarLibrosViewModel = viewModel(),
    alSeleccionarLibro: (LibroModelo) -> Unit = {}
) {
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val filtroSeleccionado by viewModel.filtroSeleccionado.collectAsState()
    val estadoUi by viewModel.estadoUi.collectAsState()
    val idsGuardados by viewModel.idsLibrosGuardados.collectAsState()
    val gestorFoco = LocalFocusManager.current

    // Transición infinita para el indicador de pulso de la API
    val transicionInfinita = rememberInfiniteTransition(label = "PulsoApi")
    val alfaPulso by transicionInfinita.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlfaPulsoApi"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior(
                subtitulo = stringResource(id = R.string.buscar_titulo)
            )
        }
    ) { rellenoSuperior ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(rellenoSuperior),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Campo de búsqueda con debounce y botón de filtros
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contenedor píldora del campo de texto
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(CircleShape)
                                .background(ContenedorSuperficieAlto)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = ContenedorPrimario,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (textoBusqueda.isEmpty()) {
                                    Text(
                                        text = stringResource(id = R.string.buscar_placeholder),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                                BasicTextField(
                                    value = textoBusqueda,
                                    onValueChange = { viewModel.actualizarTextoBusqueda(it) },
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            gestorFoco.clearFocus()
                                            viewModel.ejecutarBusqueda()
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (textoBusqueda.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.limpiarBusqueda() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = stringResource(id = R.string.buscar_cd_limpiar),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Botón de filtros avanzados
                        IconButton(
                            onClick = { /* Menú de filtros */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ContenedorSuperficieAlto)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = stringResource(id = R.string.buscar_cd_filtros),
                                tint = Primario,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chips horizontales de filtros: Todos / Por Título / Por Autor / Por Categoría
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(TipoFiltroBusqueda.entries) { filtro ->
                            val estaActivo = filtro == filtroSeleccionado
                            val textoChip = when (filtro) {
                                TipoFiltroBusqueda.TODOS -> stringResource(id = R.string.chip_todos)
                                TipoFiltroBusqueda.TITULO -> stringResource(id = R.string.chip_por_titulo)
                                TipoFiltroBusqueda.AUTOR -> stringResource(id = R.string.chip_por_autor)
                                TipoFiltroBusqueda.CATEGORIA -> stringResource(id = R.string.chip_por_categoria)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (estaActivo) ContenedorPrimario else ContenedorSuperficie)
                                    .clickable { viewModel.seleccionarFiltro(filtro) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = textoChip,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (estaActivo) FontWeight.Bold else FontWeight.Medium,
                                    color = if (estaActivo) SobrePrimario else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Sección: Explorar Géneros (Bento Grid 2 columnas)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoStories,
                                contentDescription = null,
                                tint = Secundario,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.buscar_seccion_generos),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.buscar_temas_activos),
                            style = MaterialTheme.typography.labelSmall,
                            color = Secundario,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fila 1 del Bento Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaGeneroBento(
                            titulo = stringResource(id = R.string.genero_misterio_titulo),
                            subtitulo = stringResource(id = R.string.genero_misterio_subtitulo),
                            icono = Icons.Filled.Fingerprint,
                            colorFondo = ContenedorTerciario,
                            colorTexto = SobreTerciario,
                            alHacerClic = { viewModel.seleccionarGenero("mystery") },
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaGeneroBento(
                            titulo = stringResource(id = R.string.genero_clasicos_titulo),
                            subtitulo = stringResource(id = R.string.genero_clasicos_subtitulo),
                            icono = Icons.Filled.LocalLibrary,
                            colorFondo = ContenedorPrimario,
                            colorTexto = SobrePrimario,
                            alHacerClic = { viewModel.seleccionarGenero("classics") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fila 2 del Bento Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaGeneroBento(
                            titulo = stringResource(id = R.string.genero_romance_titulo),
                            subtitulo = stringResource(id = R.string.genero_romance_subtitulo),
                            icono = Icons.Filled.Favorite,
                            colorFondo = Secundario,
                            colorTexto = SobreSecundario,
                            alHacerClic = { viewModel.seleccionarGenero("romance") },
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaGeneroBento(
                            titulo = stringResource(id = R.string.genero_scifi_titulo),
                            subtitulo = stringResource(id = R.string.genero_scifi_subtitulo),
                            icono = Icons.Filled.RocketLaunch,
                            colorFondo = ContenedorSuperficieAlto,
                            colorTexto = MaterialTheme.colorScheme.onSurface,
                            alHacerClic = { viewModel.seleccionarGenero("science_fiction") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fila 3 del Bento Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaGeneroBento(
                            titulo = stringResource(id = R.string.genero_filosofia_titulo),
                            subtitulo = stringResource(id = R.string.genero_filosofia_subtitulo),
                            icono = Icons.Filled.HourglassBottom,
                            colorFondo = ContenedorSuperficieAlto,
                            colorTexto = MaterialTheme.colorScheme.onSurface,
                            alHacerClic = { viewModel.seleccionarGenero("philosophy") },
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaGeneroBento(
                            titulo = stringResource(id = R.string.genero_tecnologia_titulo),
                            subtitulo = stringResource(id = R.string.genero_tecnologia_subtitulo),
                            icono = Icons.Filled.DataObject,
                            colorFondo = ContenedorSuperficieAlto,
                            colorTexto = MaterialTheme.colorScheme.onSurface,
                            alHacerClic = { viewModel.seleccionarGenero("technology") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Píldora de estado de sincronización con la API
            item {
                val cantidadResultados = when (val estado = estadoUi) {
                    is BuscarLibrosEstadoUi.Exito -> estado.cantidadResultados
                    else -> 0
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ContenedorSuperficie)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .alpha(alfaPulso)
                                .background(Secundario)
                        )
                        Text(
                            text = stringResource(id = R.string.buscar_api_conectada),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sync,
                            contentDescription = null,
                            tint = Terciario,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.buscar_resultados_formato, cantidadResultados),
                            style = MaterialTheme.typography.labelSmall,
                            color = Terciario,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Encabezado de la sección "Coincidencias en Catálogo"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.buscar_seccion_coincidencias),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.buscar_badge_verificado),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Lista dinámica según estado de la UI
            when (val estado = estadoUi) {
                is BuscarLibrosEstadoUi.Cargando -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = ContenedorPrimario,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = stringResource(id = R.string.buscar_estado_cargando),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                is BuscarLibrosEstadoUi.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (estado.esErrorRed) Icons.Filled.WifiOff else Icons.Filled.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = if (estado.esErrorRed) {
                                        stringResource(id = R.string.buscar_estado_error_red)
                                    } else {
                                        stringResource(id = R.string.buscar_estado_error_general, estado.mensaje)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { viewModel.ejecutarBusqueda() },
                                    colors = ButtonDefaults.buttonColors(containerColor = ContenedorPrimario)
                                ) {
                                    Text(text = stringResource(id = R.string.boton_reintentar))
                                }
                            }
                        }
                    }
                }

                is BuscarLibrosEstadoUi.Vacio -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(52.dp)
                                )
                                Text(
                                    text = stringResource(id = R.string.buscar_estado_vacio),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(id = R.string.buscar_estado_vacio_sugerencia),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                is BuscarLibrosEstadoUi.Exito -> {
                    items(estado.libros, key = { it.id }) { libro ->
                        val idLimpio = libro.obtenerIdLimpio()
                        val guardado = idsGuardados.contains(libro.id) || idsGuardados.contains(idLimpio)
                        TarjetaLibroBusqueda(
                            libro = libro,
                            estaGuardado = guardado,
                            alSeleccionarLibro = alSeleccionarLibro,
                            alGuardarLibro = { viewModel.guardarLibro(it) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Banner didáctico de Room DB offline
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ContenedorSuperficieAlto)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ContenedorSecundario),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Storage,
                            contentDescription = null,
                            tint = SobreSecundario,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.banner_offline_titulo),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = R.string.banner_offline_descripcion),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual del Bento Grid para explorar géneros literarios.
 */
@Composable
private fun TarjetaGeneroBento(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorFondo: Color,
    colorTexto: Color,
    alHacerClic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colorFondo)
            .clickable { alHacerClic() }
            .padding(12.dp)
    ) {
        // Icono de fondo decorativo
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorTexto.copy(alpha = 0.12f),
            modifier = Modifier
                .size(54.dp)
                .align(Alignment.BottomEnd)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorTexto.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorTexto,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    color = colorTexto
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = colorTexto.copy(alpha = 0.75f)
                )
            }
        }
    }
}

