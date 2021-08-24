/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["id"], tableName = "favorite")
data class EntityDBFavoriteMovie(
    @ColumnInfo(name = "id")
    var id: String
)