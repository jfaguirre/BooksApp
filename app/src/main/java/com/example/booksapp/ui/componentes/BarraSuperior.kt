package com.example.booksapp.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booksapp.R
import com.example.booksapp.ui.tema.ContenedorPrimario
import com.example.booksapp.ui.tema.Primario
import com.example.booksapp.ui.tema.SobrePrimario

/**
 * Barra superior de navegación para la pantalla de Inicio de BooksApp.
 */
@Composable
fun BarraSuperior(
    modifier: Modifier = Modifier,
    alHacerClicNotificaciones: () -> Unit = {},
    alHacerClicPerfil: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logotipo oficial desde drawable/booksapp.xml
            Image(
                painter = painterResource(id = R.drawable.booksapp),
                contentDescription = stringResource(id = R.string.cd_logo_app),
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = ContenedorPrimario,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.inicio_titulo),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de notificaciones
            IconButton(
                onClick = alHacerClicNotificaciones,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(id = R.string.inicio_cd_notificaciones),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Avatar de usuario
            IconButton(
                onClick = alHacerClicPerfil,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Primario)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(id = R.string.inicio_cd_perfil),
                    tint = SobrePrimario,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
