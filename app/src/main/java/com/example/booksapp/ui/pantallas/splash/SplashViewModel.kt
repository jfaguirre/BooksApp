package com.example.booksapp.ui.pantallas.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado que representa el progreso y mensaje de la pantalla de bienvenida (Splash).
 */
data class EstadoSplash(
    val progreso: Float = 0.25f,
    val idMensajeEtapa: Int = R.string.splash_etapa_1,
    val estaCompletado: Boolean = false
)

/**
 * ViewModel que gestiona la secuencia de carga inicial del Splash Screen.
 */
class SplashViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EstadoSplash())
    val estado: StateFlow<EstadoSplash> = _estado.asStateFlow()

    init {
        iniciarSecuenciaDeCarga()
    }

    /**
     * Ejecuta las etapas secuenciales de carga antes de navegar al Inicio.
     */
    private fun iniciarSecuenciaDeCarga() {
        viewModelScope.launch {
            // Etapa 1: Abriendo biblioteca (25%)
            _estado.update {
                it.copy(progreso = 0.25f, idMensajeEtapa = R.string.splash_etapa_1)
            }
            delay(650)

            // Etapa 2: Cargando notas y citas (60%)
            _estado.update {
                it.copy(progreso = 0.60f, idMensajeEtapa = R.string.splash_etapa_2)
            }
            delay(700)

            // Etapa 3: Verificando páginas leídas (85%)
            _estado.update {
                it.copy(progreso = 0.85f, idMensajeEtapa = R.string.splash_etapa_3)
            }
            delay(650)

            // Etapa 4: Todo listo para leer (100%)
            _estado.update {
                it.copy(progreso = 1.0f, idMensajeEtapa = R.string.splash_etapa_4)
            }
            delay(500)

            _estado.update {
                it.copy(estaCompletado = true)
            }
        }
    }
}
