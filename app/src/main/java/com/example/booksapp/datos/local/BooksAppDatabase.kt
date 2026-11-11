package com.example.booksapp.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Base de datos Room de BooksApp para persistencia local.
 * Gestiona la biblioteca personal del usuario.
 */
@Database(
    entities = [LibroBibliotecaEntidad::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ConvertidoresRoom::class)
abstract class BooksAppDatabase : RoomDatabase() {

    abstract fun bibliotecaDao(): BibliotecaDao

    companion object {
        @Volatile
        private var INSTANCIA: BooksAppDatabase? = null

        /**
         * Obtiene la instancia singleton de la base de datos, creándola si no existe.
         */
        fun obtenerInstancia(context: Context): BooksAppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BooksAppDatabase::class.java,
                    "booksapp_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}

