/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["id"], tableName = "popular")
data class EntityDBPopularList(
    @ColumnInfo(name = "id")
    var id: String
)