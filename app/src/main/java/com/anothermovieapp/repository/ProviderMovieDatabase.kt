package com.anothermovieapp.repository

import android.content.Context
import androidx.room.Room
import javax.inject.Inject


class ProviderMovieDatabase @Inject constructor(context: Context) {

    private var INSTANCE: Database = Room.databaseBuilder(
            context.applicationContext, Database::class.java,
            "movies_database")
            .fallbackToDestructiveMigration()
            .build()

    fun getDatabase(): Database {
        return INSTANCE
    }
}