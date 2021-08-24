/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.json.JSONObject
import java.io.Serializable

@Entity(primaryKeys = ["id"], tableName = "movies")
data class EntityDBMovie(
    @ColumnInfo(name = "id") var id: Long,
    @ColumnInfo(name = "isAdult") var isAdult: Boolean,
    @ColumnInfo(name = "backdropPath") var backdropPath: String?,
    @ColumnInfo(name = "budget") var budget: Int,
    @ColumnInfo(name = "homepage") var homepage: String,
    @ColumnInfo(name = "imdb_id") var imdb_id: Long,
    @ColumnInfo(name = "originalLanguage") var originalLanguage: String,
    @ColumnInfo(name = "originalTitle") var originalTitle: String,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "overview") var overview: String,
    @ColumnInfo(name = "popularity") var popularity: Double,
    @ColumnInfo(name = "posterPath") var posterPath: String,
    @ColumnInfo(name = "releaseDate") var releaseDate: String,
    @ColumnInfo(name = "revenue") var revenue: Int,
    @ColumnInfo(name = "runtime") var runtime: Int,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "tagline") var tagline: String,
    @ColumnInfo(name = "isVideo") var isVideo: Boolean,
    @ColumnInfo(name = "vote_average") var vote_average: Double,
    @ColumnInfo(name = "voteCount") var voteCount: Int
) : Serializable {
    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (!EntityDBMovie::class.java.isAssignableFrom(obj.javaClass)) {
            return false
        }
        val other = obj as EntityDBMovie
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        const val JSON_ID = "id"
        const val JSON_ADULT = "adult"
        const val JSON_BACKDROP_PATH = "backdrop_path"
        const val JSON_BUDGET = "budget"
        const val JSON_HOMEPAGE = "homepage"
        const val JSON_IMDB_ID = "imdb_id"
        const val JSON_ORIGINAL_LANGUAGE = "original_language"
        const val JSON_ORIGINAL_TITLE = "original_title"
        const val JSON_TITLE = "title"
        const val JSON_OVERVIEW = "overview"
        const val JSON_POPULARITY = "popularity"
        const val JSON_POSTER_PATH = "poster_path"
        const val JSON_RELEASE_DATE = "release_date"
        const val JSON_REVENUE = "revenue"
        const val JSON_RUNTIME = "runtime"
        const val JSON_STATUS = "status"
        const val JSON_TAGLINE = "tagline"
        const val JSON_VIDEO = "video"
        const val JSON_VOTE_AVERAGE = "vote_average"
        const val JSON_VOTE_COUNT = "vote_count"

        @kotlin.jvm.JvmStatic
        fun fromJSON(obj: JSONObject): EntityDBMovie {
            return EntityDBMovie(
                obj.optLong(JSON_ID),
                obj.optBoolean(JSON_ADULT),
                obj.optString(JSON_BACKDROP_PATH).replace("/", "").replace("\\", ""),
                obj.optInt(JSON_BUDGET),
                obj.optString(JSON_HOMEPAGE),
                obj.optLong(JSON_IMDB_ID),
                obj.optString(JSON_ORIGINAL_LANGUAGE),
                obj.optString(JSON_ORIGINAL_TITLE),
                obj.optString(JSON_TITLE),
                obj.optString(JSON_OVERVIEW),
                obj.optDouble(JSON_POPULARITY),
                obj.optString(JSON_POSTER_PATH).replace("/", "").replace("\\", ""),
                obj.optString(JSON_RELEASE_DATE),
                obj.optInt(JSON_REVENUE),
                obj.optInt(JSON_RUNTIME),
                obj.optString(JSON_STATUS),
                obj.optString(JSON_TAGLINE),
                obj.optBoolean(JSON_VIDEO),
                obj.optDouble(JSON_VOTE_AVERAGE),
                obj.optInt(JSON_VOTE_COUNT)
            )
        }
    }

}