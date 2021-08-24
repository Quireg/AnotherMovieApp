/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoMovieTrailer {
    @Query("SELECT * from trailers WHERE movie_id = :idd")
    suspend fun get(idd : String): List<EntityDBMovieTrailer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EntityDBMovieTrailer)
}