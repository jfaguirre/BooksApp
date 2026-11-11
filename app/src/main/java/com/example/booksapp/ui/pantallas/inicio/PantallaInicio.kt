package com.example.booksapp.ui.pantallas.inicio

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booksapp.BooksApplication
import com.example.booksapp.R
import com.example.booksapp.datos.modelos.LibroModelo
import com.example.booksapp.ui.componentes.BarraSuperior
import com.example.booksapp.ui.componentes.TarjetaLibroDestacado
import com.example.booksapp.ui.componentes.TarjetaLibroHorizontal
import com.example.booksapp.ui.componentes.TarjetaMetaLectura
import com.example.booksapp.ui.componentes.TarjetaTendenciaComunidad
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSuperficie
import com.example.booksapp.ui.tema.ContenedorSuperficieBajo
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario
import com.example.booksapp.ui.tema.TerciarioFijo

/**
 * Pantalla principal de Inicio de BooksApp, consumiendo Open Library API.
 */
@Composable
fun PantallaInicio(
    modifier: Modifier = Modifier,
    alSeleccionarLibro: (LibroModelo) -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as BooksApplication
    val viewModel: InicioViewModel = viewModel(
        factory = InicioViewModel.Factory(
            bibliotecaRepositorio = application.bibliotecaRepositorio
        )
    )
    val estadoUi by viewModel.estadoUi.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para mostrar snackbar de confirmación al añadir libro
    LaunchedEffect(estadoUi) {
        val estado = estadoUi as? InicioEstadoUi.Exito ?: return@LaunchedEffect
        val mensaje = estado.mensajeConfirmacion ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message = mensaje)
        viewModel.limpiarMensaje()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraSuperior()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { datos ->
                Snackbar(
                    snackbarData = datos,
                    containerColor = Terciario,
                    contentColor = Color.White
                )
            }
        }
    ) { rellenoSuperior ->
        when (val estado = estadoUi) {
            is InicioEstadoUi.Cargando -> {
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
                            text = stringResource(id = R.string.estado_cargando_libros),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is InicioEstadoUi.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.estado_error_carga),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = estado.mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.cargarDatosInicio() },
                            colors = ButtonDefaults.buttonColors(containerColor = ContenedorPrimario)
                        ) {
                            Text(text = stringResource(id = R.string.boton_reintentar))
                        }
                    }
                }
            }

            is InicioEstadoUi.Exito -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(rellenoSuperior),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Encabezado de saludo y estado de API
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.inicio_subtitulo_dia).uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Secundario,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = estado.saludo,
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = Primario,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Indicador de API Conectada
                                Row(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(ContenedorSuperficie)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Secundario)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.inicio_estado_api),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Terciario,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.CloudDone,
                                        contentDescription = null,
                                        tint = Secundario,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Barra de búsqueda con icono de escaneo QR
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(CircleShape)
                                    .background(ContenedorSuperficieBajo)
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = ContenedorPrimario,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (estado.textoBusqueda.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.inicio_placeholder_buscar),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    BasicTextField(
                                        value = estado.textoBusqueda,
                                        onValueChange = { viewModel.actualizarTextoBusqueda(it) },
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                IconButton(
                                    onClick = { /* Escaneo futuro */ },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(ContenedorSuperficie)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCodeScanner,
                                        contentDescription = stringResource(id = R.string.inicio_cd_escanear),
                                        tint = Terciario,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Chips de Categorías horizontales
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            items(estado.categorias) { cat ->
                                val seleccionada = cat.estaSeleccionada
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (seleccionada) ContenedorPrimario else ContenedorSuperficie)
                                        .clickable { viewModel.seleccionarCategoria(cat) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (seleccionada) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoStories,
                                            contentDescription = null,
                                            tint = SobrePrimario,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = cat.nombre,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
                                        color = if (seleccionada) SobrePrimario else Terciario
                                    )
                                }
                            }
                        }
                    }

                    // Sección: Libro del Momento
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
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
                                        imageVector = Icons.Filled.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Secundario,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.seccion_libro_momento),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Primario
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(TerciarioFijo)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.badge_seleccion_editorial),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Terciario,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            estado.libroDelMomento?.let { libroMomento ->
                                TarjetaLibroDestacado(
                                    libro = libroMomento,
                                    alHacerClic = { alSeleccionarLibro(it) },
                                    alHacerClicAnadir = { viewModel.añadirABiblioteca(it) },
                                    alAlternarFavorito = { viewModel.alternarFavorito(it) }
                                )
                            }
                        }
                    }

                    // Sección: Ficción y Fantasía (Carrusel)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 4.dp, height = 18.dp)
                                            .clip(CircleShape)
                                            .background(Secundario)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.seccion_ficcion_fantasia),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Primario
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.enlace_ver_todo),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Secundario
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Secundario,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                items(estado.librosFiccionYFantasia) { libro ->
                                    TarjetaLibroHorizontal(
                                        libro = libro,
                                        alHacerClic = { alSeleccionarLibro(it) },
                                        alAlternarFavorito = { viewModel.alternarFavorito(it) }
                                    )
                                }
                            }
                        }
                    }

                    // Sección: Ciencia Ficción y Distopía (Carrusel)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 4.dp, height = 18.dp)
                                            .clip(CircleShape)
                                            .background(Terciario)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.seccion_scifi_distopia),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Terciario
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.enlace_explorar),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Terciario
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Terciario,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                items(estado.librosSciFiYDistopia) { libro ->
                                    TarjetaLibroHorizontal(
                                        libro = libro,
                                        alHacerClic = { alSeleccionarLibro(it) },
                                        alAlternarFavorito = { viewModel.alternarFavorito(it) }
                                    )
                                }
                            }
                        }
                    }

                    // Sección: Tendencias de la Comunidad
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
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
                                        imageVector = Icons.Filled.Groups,
                                        contentDescription = null,
                                        tint = ContenedorPrimario,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.seccion_tendencias_comunidad),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Primario
                                    )
                                }

                                Text(
                                    text = stringResource(id = R.string.subtitulo_esta_semana),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                estado.tendenciasComunidad.forEachIndexed { indice, libro ->
                                    TarjetaTendenciaComunidad(
                                        posicion = indice + 1,
                                        libro = libro,
                                        alHacerClic = { alSeleccionarLibro(it) },
                                        alHacerClicAnadir = { viewModel.añadirABiblioteca(libro) }
                                    )
                                }
                            }
                        }
                    }

                    // Banner de Meta de Lectura
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            TarjetaMetaLectura()
                        }
                    }
                }
            }
        }
    }
}
