package com.example.booksapp.ui.pantallas.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booksapp.R
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.ContenedorSecundario
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.PrimarioFijo
import com.example.booksapp.ui.tema.PrimarioFijoAtenuado
import com.example.booksapp.ui.tema.Secundario
import com.example.booksapp.ui.tema.SecundarioFijo
import com.example.booksapp.ui.tema.SobrePrimario
import com.example.booksapp.ui.tema.Terciario

/**
 * Pantalla de bienvenida (Splash Screen) diseñada conforme a las especificaciones de Stitch.
 */
@Composable
fun PantallaSplash(
    alFinalizarSplash: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    // Navegación automática cuando finaliza la secuencia
    LaunchedEffect(estado.estaCompletado) {
        if (estado.estaCompletado) {
            alFinalizarSplash()
        }
    }

    // Animación suave del progreso de la barra
    val progresoAnimado by animateFloatAsState(
        targetValue = estado.progreso,
        animationSpec = tween(durationMillis = 400),
        label = "ProgresoSplash"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primario,
                        ContenedorPrimario,
                        Terciario
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Orbes de luz ambiental sutil
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(240.dp)
                .background(ContenedorSecundario.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(200.dp)
                .background(PrimarioFijoAtenuado.copy(alpha = 0.12f), CircleShape)
        )

        // Contenido estructurado verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicador superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = stringResource(id = R.string.cd_icono_lectura),
                        tint = PrimarioFijoAtenuado,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.splash_edicion_literaria).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SobrePrimario,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Filled.CloudSync,
                    contentDescription = stringResource(id = R.string.cd_icono_sincronizacion),
                    tint = SobrePrimario.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Bloque Hero Central: Logo, Nombre, Lema, Cita y Barra de Carga
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Marco táctil de squircle con el logo booksapp.xml
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.booksapp),
                        contentDescription = stringResource(id = R.string.cd_logo_app),
                        modifier = Modifier.fillMaxSize()
                    )

                    // Detalle de lazo marcador de libros en la esquina inferior derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(width = 12.dp, height = 18.dp)
                            .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                            .background(Secundario)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nombre con estilo tipográfico Stitch: "Bokks" + "App"
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.nombre_app_parte1),
                        style = MaterialTheme.typography.displayLarge,
                        color = SobrePrimario,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(id = R.string.nombre_app_parte2),
                        style = MaterialTheme.typography.displayLarge,
                        color = SecundarioFijo,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lema humanista
                Text(
                    text = stringResource(id = R.string.splash_lema),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimarioFijo.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cita literaria
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.splash_cita),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = SobrePrimario.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Barra de progreso y texto de estado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(200.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.20f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progresoAnimado)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Secundario, ContenedorSecundario)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(id = estado.idMensajeEtapa).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SobrePrimario.copy(alpha = 0.75f),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Pie de página con versión e institución
            Text(
                text = stringResource(id = R.string.splash_pie_version),
                style = MaterialTheme.typography.bodySmall,
                color = SobrePrimario.copy(alpha = 0.55f)
            )
        }
    }
}
