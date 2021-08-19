package com.anothermovieapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface DaoMovieReview {
    @Query("SELECT * from reviews WHERE id = :idd")
    suspend fun get(idd : Long): List<EntityDBMovieReview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EntityDBMovieReview)

    @Query("SELECT * from reviews_fetch_state WHERE id = :idd")
    suspend fun getFetchState(idd : Long) : EntityDBReviewsListFetchState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFetchState(state: EntityDBReviewsListFetchState)
}