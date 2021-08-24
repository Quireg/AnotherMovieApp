/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["_id"], tableName = "trailers")
data class EntityDBMovieTrailer(
    @ColumnInfo(name = "_id") var _id: String,
    @ColumnInfo(name = "movie_id") var movie_id_id: String,
    @ColumnInfo(name = "iso_639_1") var iso_639_1: String,
    @ColumnInfo(name = "iso_3166_1") var iso_3166_1: String,
    @ColumnInfo(name = "key") var key: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "site") var site: String,
    @ColumnInfo(name = "size") var size: String,
    @ColumnInfo(name = "type") var type: String
)