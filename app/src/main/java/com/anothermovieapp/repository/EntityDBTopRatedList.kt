package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(primaryKeys = ["id"], tableName = "top_rated")
data class EntityDBTopRatedList(
    @ColumnInfo(name = "id")
    var id: String
)