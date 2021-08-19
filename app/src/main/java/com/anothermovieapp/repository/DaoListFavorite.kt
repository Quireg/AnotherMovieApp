package com.anothermovieapp.repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface DaoListFavorite {
    @Query("SELECT * from favorite")
    suspend fun get(): List<EntityDBFavoriteList>

    @Delete
    suspend fun remove(id: EntityDBFavoriteList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EntityDBFavoriteList)
}