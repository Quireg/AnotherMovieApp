package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["id"], tableName = "top_rated_fetch_state")
data class EntityDBTopRatedListFetchState(
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "currentPage")
    var currentPage: Long,
    @ColumnInfo(name = "totalPages")
    var totalPages: Long
)