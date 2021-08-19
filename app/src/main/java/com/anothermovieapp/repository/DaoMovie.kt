package com.anothermovieapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DaoMovie {
    @Query("SELECT * from movies")
    suspend fun get(): List<EntityDBMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EntityDBMovie)
}