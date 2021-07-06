package com.anothermovieapp.repository

import android.content.Context
import androidx.room.Room


class MovieDatabaseProvider(context: Context,
                            private var INSTANCE: MoviesDatabase = Room.databaseBuilder(
                                    context.applicationContext, MoviesDatabase::class.java,
                                    "movies_database")
                                    .fallbackToDestructiveMigration()
                                    .build()) {

    fun getDatabase(): MoviesDatabase {
        return INSTANCE
    }
}