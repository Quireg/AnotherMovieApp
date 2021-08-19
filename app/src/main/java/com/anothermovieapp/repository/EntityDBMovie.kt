package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.io.Serializable

@Entity(primaryKeys = ["id"], tableName = "movies")
data class EntityDBMovie(@ColumnInfo(name = "id") var id: Long,
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
                         @ColumnInfo(name = "voteCount") var voteCount: Int) :Serializable {
    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (!EntityDBMovie::class.java.isAssignableFrom(obj.javaClass)) {
            return false
        }
        val other = obj as EntityDBMovie
        return id == other.id && isAdult == other.isAdult && backdropPath == other.backdropPath && budget == other.budget && homepage == other.homepage && imdb_id == other.imdb_id && originalTitle == other.originalTitle && originalLanguage == other.originalLanguage && title == other.title && overview == other.overview && popularity == other.popularity && posterPath == other.posterPath && releaseDate == other.releaseDate && revenue == other.revenue && runtime == other.runtime && status == other.status && tagline == other.tagline && isVideo == other.isVideo && vote_average == other.vote_average && voteCount == other.voteCount
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
//        val MOVIES_CLOMUNS = arrayOf<String?>(
//                MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
//                MovieEntry.COLUMN_ADULT,
//                MovieEntry.COLUMN_BACKDROP_PATH,
//                MovieEntry.COLUMN_BUDGET,
//                MovieEntry.COLUMN_HOMEPAGE,
//                MovieEntry.COLUMN_IMDB_ID,
//                MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
//                MovieEntry.COLUMN_ORIGINAL_TITLE,
//                MovieEntry.COLUMN_TITLE,
//                MovieEntry.COLUMN_OVERVIEW,
//                MovieEntry.COLUMN_POPULARITY,
//                MovieEntry.COLUMN_POSTER_PATH,
//                MovieEntry.COLUMN_RELEASE_DATE,
//                MovieEntry.COLUMN_REVENUE,
//                MovieEntry.COLUMN_RUNTIME,
//                MovieEntry.COLUMN_STATUS,
//                MovieEntry.COLUMN_TAGLINE,
//                MovieEntry.COLUMN_VIDEO,
//                MovieEntry.COLUMN_VOTE_AVERAGE,
//                MovieEntry.COLUMN_VOTE_COUNT
//        )
//
//        fun contentValuesFromObj(obj: MovieItem): ContentValues {
//            val contentValues = ContentValues()
//            contentValues.put(MovieEntry._ID, obj.id)
//            contentValues.put(MovieEntry.COLUMN_ADULT, obj.isAdult)
//            contentValues.put(MovieEntry.COLUMN_BACKDROP_PATH, obj.getBackdropPath())
//            contentValues.put(MovieEntry.COLUMN_BUDGET, obj.budget)
//            contentValues.put(MovieEntry.COLUMN_HOMEPAGE, obj.homepage)
//            contentValues.put(MovieEntry.COLUMN_IMDB_ID, obj.imdb_id)
//            contentValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, obj.originalLanguage)
//            contentValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, obj.originalTitle)
//            contentValues.put(MovieEntry.COLUMN_TITLE, obj.getTitle())
//            contentValues.put(MovieEntry.COLUMN_OVERVIEW, obj.overview)
//            contentValues.put(MovieEntry.COLUMN_POPULARITY, obj.popularity)
//            contentValues.put(MovieEntry.COLUMN_POSTER_PATH, obj.posterPath)
//            contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, obj.releaseDate)
//            contentValues.put(MovieEntry.COLUMN_REVENUE, obj.revenue)
//            contentValues.put(MovieEntry.COLUMN_RUNTIME, obj.runtime)
//            contentValues.put(MovieEntry.COLUMN_STATUS, obj.status)
//            contentValues.put(MovieEntry.COLUMN_TAGLINE, obj.tagline)
//            contentValues.put(MovieEntry.COLUMN_VIDEO, obj.isVideo)
//            contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, obj.vote_average)
//            contentValues.put(MovieEntry.COLUMN_VOTE_COUNT, obj.voteCount)
//            return contentValues
//        }

        const val COL_MOVIE_ID = 0
        const val COL_MOVIE_ADULT = 1
        const val COL_MOVIE_BACKDROP_PATH = 2
        const val COL_MOVIE_BUDGET = 3
        const val COL_MOVIE_HOMEPAGE = 4
        const val COL_MOVIE_IMDB_ID = 5
        const val COL_MOVIE_ORIGINAL_LANGUAGE = 6
        const val COL_MOVIE_ORIGINAL_TITLE = 7
        const val COL_MOVIE_TITLE = 8
        const val COL_MOVIE_OVERVIEW = 9
        const val COL_MOVIE_POPULARITY = 10
        const val COL_MOVIE_POSTER_PATH = 11
        const val COL_MOVIE_RELEASE_DATE = 12
        const val COL_MOVIE_REVENUE = 13
        const val COL_MOVIE_RUNTIME = 14
        const val COL_MOVIE_STATUS = 15
        const val COL_MOVIE_TAGLINE = 16
        const val COL_MOVIE_VIDEO = 17
        const val COL_MOVIE_VOTE_AVERAGE = 18
        const val COL_MOVIE_VOTE_COUNT = 19
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
//        fun fromCursor(cursor: Cursor?): MovieItem {
//            return MovieItem(
//                    cursor!!.getLong(COL_MOVIE_ID),
//                    java.lang.Boolean.valueOf(cursor.getString(COL_MOVIE_ADULT)),
//                    cursor.getString(COL_MOVIE_BACKDROP_PATH),
//                    cursor.getInt(COL_MOVIE_BUDGET),
//                    cursor.getString(COL_MOVIE_HOMEPAGE),
//                    cursor.getLong(COL_MOVIE_IMDB_ID),
//                    cursor.getString(COL_MOVIE_ORIGINAL_LANGUAGE),
//                    cursor.getString(COL_MOVIE_ORIGINAL_TITLE),
//                    cursor.getString(COL_MOVIE_TITLE),
//                    cursor.getString(COL_MOVIE_OVERVIEW),
//                    cursor.getDouble(COL_MOVIE_POPULARITY),
//                    cursor.getString(COL_MOVIE_POSTER_PATH),
//                    cursor.getString(COL_MOVIE_RELEASE_DATE),
//                    cursor.getInt(COL_MOVIE_REVENUE),
//                    cursor.getInt(COL_MOVIE_RUNTIME),
//                    cursor.getString(COL_MOVIE_STATUS),
//                    cursor.getString(COL_MOVIE_TAGLINE),
//                    java.lang.Boolean.valueOf(cursor.getString(COL_MOVIE_VIDEO)),
//                    cursor.getDouble(COL_MOVIE_VOTE_AVERAGE),
//                    cursor.getInt(COL_MOVIE_VOTE_COUNT)
//            )
//        }

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