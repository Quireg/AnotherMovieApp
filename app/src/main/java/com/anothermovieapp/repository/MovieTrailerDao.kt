package com.anothermovieapp.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * Created by artur.menchenko@globallogic.com on 29/12/20.
 */
@Dao
interface MovieTrailerDao {
    @Query("SELECT * from trailers")
    fun getMovieTrailers() : LiveData<List<MovieTrailer>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieTrailer(item: MovieTrailer)
}