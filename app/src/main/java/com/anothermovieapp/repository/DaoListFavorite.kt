/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoListFavorite {
    @Query("SELECT * from favorite")
    fun get(): Flow<List<EntityDBFavoriteMovie>>

    @Delete
    suspend fun remove(id: EntityDBFavoriteMovie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EntityDBFavoriteMovie)
}