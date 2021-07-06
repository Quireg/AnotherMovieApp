package com.anothermovieapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Movie::class, MovieTrailer::class, MovieReview::class], version = 1, exportSchema = false)
abstract class MoviesDatabase:RoomDatabase() {
    abstract fun movieDao() : MovieDao
    abstract fun movieTrailerDao() : MovieTrailerDao
    abstract fun movieReviewDao() : MovieReviewDao

}