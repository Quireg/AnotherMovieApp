/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.json.JSONObject

@Entity(primaryKeys = ["reviewId"], tableName = "reviews")
data class EntityDBMovieReview(
    @ColumnInfo(name = "id") var movieId: String,
    @ColumnInfo(name = "reviewId") var reviewId: String,
    @ColumnInfo(name = "author") var author: String,
    @ColumnInfo(name = "content") var content: String
) {

    companion object {
        const val JSON_ID = "id"
        const val JSON_PAGE = "page"
        const val JSON_ARRAY_RESULTS = "results"
        const val JSON_REVIEW_ID = "id"
        const val JSON_AUTHOR = "author"
        const val JSON_CONTENT = "content"
        const val JSON_TOTAL_PAGES = "total_pages"
        fun fromJSON(obj: JSONObject, movieId: String): EntityDBMovieReview {
            return EntityDBMovieReview(
                movieId,
                obj.optString(JSON_REVIEW_ID),
                obj.optString(JSON_AUTHOR),
                obj.optString(JSON_CONTENT)
            )
        }
    }
}