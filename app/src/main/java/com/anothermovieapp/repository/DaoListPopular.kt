/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoListPopular {
    @Query("SELECT * from popular")
    suspend fun get(): List<EntityDBPopularList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: List<EntityDBPopularList>)

    @Query("DELETE from popular")
    suspend fun clear()

    @Query("SELECT * from popular_fetch_state WHERE id = 1")
    suspend fun getFetchState() : EntityDBPopularListFetchState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFetchState(state: EntityDBPopularListFetchState)
}