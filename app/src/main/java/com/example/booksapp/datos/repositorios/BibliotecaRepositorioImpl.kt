package com.example.booksapp.datos.repositorios

import com.example.booksapp.datos.local.BibliotecaDao
import com.example.booksapp.datos.local.EstadoLectura
import com.example.booksapp.datos.local.LibroBibliotecaEntidad
import com.example.booksapp.datos.modelos.LibroModelo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementación de [BibliotecaRepositorio] utilizando Room como fuente de datos local.
 */
class BibliotecaRepositorioImpl(
    private val dao: BibliotecaDao
) : BibliotecaRepositorio {

    // ─── ESCRITURA ───────────────────────────────────────────────────────────

    override suspend fun guardarLibro(libro: LibroModelo, estado: EstadoLectura) {
        withContext(Dispatchers.IO) {
            dao.guardarLibro(libro.aEntidad(estado))
        }
    }

    override suspend fun eliminarLibro(id: String) {
        withContext(Dispatchers.IO) {
            dao.eliminarLibroPorId(id)
        }
    }

    override suspend fun alternarFavorito(id: String, esFavorito: Boolean) {
        withContext(Dispatchers.IO) {
            dao.actualizarFavorito(id, esFavorito)
        }
    }

    override suspend fun cambiarEstadoLectura(id: String, estado: EstadoLectura) {
        withContext(Dispatchers.IO) {
            dao.actualizarEstadoLectura(id, estado)
        }
    }

    // ─── CONSULTAS ───────────────────────────────────────────────────────────

    override suspend fun estaGuardado(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            dao.contarLibroPorId(id) > 0
        }
    }

    override fun observarBiblioteca(): Flow<List<LibroModelo>> =
        dao.observarTodosLosLibros().map { lista -> lista.map { it.aModelo() } }

    override fun observarFavoritos(): Flow<List<LibroModelo>> =
        dao.observarFavoritos().map { lista -> lista.map { it.aModelo() } }

    override fun observarPorEstado(estado: EstadoLectura): Flow<List<LibroModelo>> =
        dao.observarLibrosPorEstado(estado).map { lista -> lista.map { it.aModelo() } }

    override fun observarTotalLibros(): Flow<Int> = dao.observarTotalLibros()

    override fun observarTotalLeidos(): Flow<Int> = dao.observarTotalLeidos()

    override fun observarTotalLeyendo(): Flow<Int> = dao.observarTotalLeyendo()
}

// ─── EXTENSIONES DE MAPEO ────────────────────────────────────────────────────

/**
 * Convierte un [LibroModelo] a [LibroBibliotecaEntidad] para persistencia en Room.
 */
fun LibroModelo.aEntidad(estado: EstadoLectura = EstadoLectura.POR_LEER): LibroBibliotecaEntidad {
    return LibroBibliotecaEntidad(
        id = this.id,
        titulo = this.titulo,
        autores = this.autores.joinToString("|"),
        descripcion = this.descripcion,
        urlPortada = this.urlPortada,
        anioPublicacion = this.anioPublicacion,
        calificacion = this.calificacion,
        cantidadCalificaciones = this.cantidadCalificaciones,
        etiqueta = this.etiqueta,
        esFavorito = this.esFavorito,
        estadoLectura = estado
    )
}

/**
 * Convierte una [LibroBibliotecaEntidad] de Room a [LibroModelo] para la UI.
 */
fun LibroBibliotecaEntidad.aModelo(): LibroModelo {
    return LibroModelo(
        id = this.id,
        titulo = this.titulo,
        autores = this.autores.split("|").filter { it.isNotBlank() },
        descripcion = this.descripcion,
        urlPortada = this.urlPortada,
        anioPublicacion = this.anioPublicacion,
        calificacion = this.calificacion,
        cantidadCalificaciones = this.cantidadCalificaciones,
        etiqueta = this.etiqueta,
        esFavorito = this.esFavorito
    )
}

