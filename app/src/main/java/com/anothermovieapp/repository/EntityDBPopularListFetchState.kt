/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["id"], tableName = "popular_fetch_state")
data class EntityDBPopularListFetchState(
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "currentPage")
    var currentPage: Long,
    @ColumnInfo(name = "totalPages")
    var totalPages: Long
)