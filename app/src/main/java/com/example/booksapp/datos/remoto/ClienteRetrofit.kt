package com.example.booksapp.datos.remoto

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Proveedor singleton de la instancia de Retrofit configurada para Open Library.
 */
object ClienteRetrofit {

    private const val URL_BASE = "https://openlibrary.org/"

    private val formatoJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val clienteOkHttp: OkHttpClient by lazy {
        val interceptorRegistro = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(interceptorRegistro)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val servicioOpenLibrary: OpenLibraryServicio by lazy {
        val tipoContenido = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteOkHttp)
            .addConverterFactory(formatoJson.asConverterFactory(tipoContenido))
            .build()
            .create(OpenLibraryServicio::class.java)
    }

    /**
     * Construye la URL de la portada del libro a partir de su ID de portada en Open Library.
     */
    fun construirUrlPortada(idPortada: Long?, tamanio: String = "M"): String {
        return if (idPortada != null && idPortada > 0) {
            "https://covers.openlibrary.org/b/id/$idPortada-$tamanio.jpg"
        } else {
            ""
        }
    }
}
