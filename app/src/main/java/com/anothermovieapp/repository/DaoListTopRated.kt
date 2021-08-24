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
interface DaoListTopRated {
    @Query("SELECT * from top_rated")
    suspend fun get(): List<EntityDBTopRatedList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: List<EntityDBTopRatedList>)

    @Query("DELETE from top_rated")
    suspend fun clear()

    @Query("SELECT * from top_rated_fetch_state WHERE id = 1")
    suspend fun getFetchState() : EntityDBTopRatedListFetchState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFetchState(state: EntityDBTopRatedListFetchState)
}