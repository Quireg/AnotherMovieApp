package com.anothermovieapp.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MovieDao {
    @Query("SELECT * from movies")
    fun getMovies() : LiveData<List<Movie>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(item: Movie)
}