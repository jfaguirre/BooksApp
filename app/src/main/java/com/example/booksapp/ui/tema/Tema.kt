package com.example.booksapp.ui.tema

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Esquema de colores para modo claro
private val EsquemaColorClaro = lightColorScheme(
    primary = Primario,
    onPrimary = SobrePrimario,
    primaryContainer = ContenedorPrimario,
    onPrimaryContainer = SobreContenedorPrimario,
    secondary = Secundario,
    onSecondary = SobreSecundario,
    secondaryContainer = ContenedorSecundario,
    onSecondaryContainer = SobreContenedorSecundario,
    tertiary = Terciario,
    onTertiary = SobreTerciario,
    tertiaryContainer = ContenedorTerciario,
    onTertiaryContainer = SobreContenedorTerciario,
    background = Fondo,
    onBackground = SobreFondo,
    surface = Superficie,
    onSurface = SobreSuperficie,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = SobreSuperficieVariante,
    surfaceContainer = ContenedorSuperficie,
    surfaceContainerLow = ContenedorSuperficieBajo,
    surfaceContainerHigh = ContenedorSuperficieAlto,
    surfaceContainerHighest = ContenedorSuperficieMasAlto,
    outline = Contorno,
    outlineVariant = ContornoVariante
)

// Esquema de colores para modo oscuro
private val EsquemaColorOscuro = darkColorScheme(
    primary = PrimarioOscuro,
    onPrimary = SobrePrimarioOscuro,
    primaryContainer = ContenedorPrimarioOscuro,
    onPrimaryContainer = PrimarioFijo,
    secondary = SecundarioOscuro,
    onSecondary = SobreSecundario,
    secondaryContainer = ContenedorSecundario,
    onSecondaryContainer = SobreSecundarioFijo,
    tertiary = TerciarioOscuro,
    onTertiary = SobreTerciario,
    tertiaryContainer = ContenedorTerciario,
    onTertiaryContainer = TerciarioFijo,
    background = SuperficieOscura,
    onBackground = SobreSuperficieOscura,
    surface = SuperficieOscura,
    onSurface = SobreSuperficieOscura,
    surfaceVariant = ContenedorSuperficieOscura,
    onSurfaceVariant = ContornoVariante,
    surfaceContainer = ContenedorSuperficieOscura,
    outline = Contorno,
    outlineVariant = ContornoVariante
)

@Composable
fun BooksAppTema(
    esModoOscuro: Boolean = false,
    contenido: @Composable () -> Unit
) {
    val esquemaColor = if (esModoOscuro) EsquemaColorOscuro else EsquemaColorClaro

    MaterialTheme(
        colorScheme = esquemaColor,
        typography = TipografiaBooksApp,
        shapes = FormasBooksApp
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            contenido()
        }
    }
}
