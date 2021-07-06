package com.anothermovieapp.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MovieReviewDao {
    @Query("SELECT * from reviews")
    fun getMovieReviews() : LiveData<List<MovieReview>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieReview(item: MovieReview)
}