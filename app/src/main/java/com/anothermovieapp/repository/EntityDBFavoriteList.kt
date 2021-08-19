package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(primaryKeys = ["id"], tableName = "favorite")
data class EntityDBFavoriteList(
    @ColumnInfo(name = "id")
    var id: String
)