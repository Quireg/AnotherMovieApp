package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(primaryKeys = ["_id", ""], tableName = "trailers")
data class MovieTrailer(@PrimaryKey @ColumnInfo(name = "_id") var _id: String,
                        @ColumnInfo(name = "iso_639_1") var iso_639_1: String,
                        @ColumnInfo(name = "iso_3166_1") var iso_3166_1: String,
                        @ColumnInfo(name = "key") var key: String,
                        @ColumnInfo(name = "name") var name: String,
                        @ColumnInfo(name = "site") var site: String,
                        @ColumnInfo(name = "size") var size: String,
                        @ColumnInfo(name = "type") var type: String)