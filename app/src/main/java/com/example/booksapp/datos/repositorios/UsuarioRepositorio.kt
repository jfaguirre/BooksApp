package com.example.booksapp.datos.repositorios

import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio para la gestión del perfil y preferencias locales del usuario.
 */
interface UsuarioRepositorio {

    /**
     * Observa el nombre del usuario de manera reactiva como [Flow].
     */
    fun observarNombreUsuario(): Flow<String>

    /**
     * Guarda el nombre del usuario en el almacenamiento local del dispositivo.
     */
    suspend fun guardarNombreUsuario(nombre: String)

    /**
     * Retorna el nombre actual de forma síncrona.
     */
    fun obtenerNombreUsuarioActual(): String
}
