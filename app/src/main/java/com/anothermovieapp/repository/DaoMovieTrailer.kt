package com.anothermovieapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Created by artur.menchenko@globallogic.com on 29/12/20.
 */
@Dao
interface DaoMovieTrailer {
    @Query("SELECT * from trailers")
    suspend fun get(): List<EntityDBMovieTrailer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EntityDBMovieTrailer)
}