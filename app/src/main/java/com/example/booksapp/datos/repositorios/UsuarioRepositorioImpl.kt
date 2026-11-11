package com.example.booksapp.datos.repositorios

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Implementación de [UsuarioRepositorio] que utiliza SharedPreferences para persistencia local.
 */
class UsuarioRepositorioImpl(
    context: Context
) : UsuarioRepositorio {

    private val preferencias: SharedPreferences = context.getSharedPreferences(
        PREFERENCIAS_USUARIO,
        Context.MODE_PRIVATE
    )

    private val nombreInicial: String = preferencias.getString(
        CLAVE_NOMBRE_USUARIO,
        NOMBRE_POR_DEFECTO
    ) ?: NOMBRE_POR_DEFECTO

    private val _nombreUsuarioFlow = MutableStateFlow(nombreInicial)

    override fun observarNombreUsuario(): Flow<String> = _nombreUsuarioFlow.asStateFlow()

    override suspend fun guardarNombreUsuario(nombre: String) {
        val nombreLimpio = nombre.trim().ifEmpty { NOMBRE_POR_DEFECTO }
        withContext(Dispatchers.IO) {
            preferencias.edit().putString(CLAVE_NOMBRE_USUARIO, nombreLimpio).apply()
        }
        _nombreUsuarioFlow.value = nombreLimpio
    }

    override fun obtenerNombreUsuarioActual(): String = _nombreUsuarioFlow.value

    companion object {
        private const val PREFERENCIAS_USUARIO = "preferencias_usuario_booksapp"
        private const val CLAVE_NOMBRE_USUARIO = "clave_nombre_usuario"
        const val NOMBRE_POR_DEFECTO = "Sofía"
    }
}
