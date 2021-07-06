package com.anothermovieapp.repository

import android.content.ContentValues
import android.database.Cursor
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anothermovieapp.managers.MovieDatabaseContract.MovieReviews
import org.json.JSONObject

@Entity(primaryKeys = ["_id", ""], tableName = "reviews")
data class MovieReview(@PrimaryKey @ColumnInfo(name = "id") var movieId: String?,
                       @ColumnInfo(name = "reviewId") var reviewId: String,
                       @ColumnInfo(name = "author") var author: String,
                       @ColumnInfo(name = "content") var content: String) {

    companion object {
        fun contentValuesFromObj(obj: MovieReview): ContentValues {
            val contentValues = ContentValues()
            contentValues.put(MovieReviews._ID, obj.movieId)
            contentValues.put(MovieReviews.COLUMN_REVIEW_ID, obj.reviewId)
            contentValues.put(MovieReviews.COLUMN_AUTHOR, obj.author)
            contentValues.put(MovieReviews.COLUMN_CONTENT, obj.content)
            return contentValues
        }

        const val JSON_ID = "id"
        const val JSON_PAGE = "page"
        const val JSON_ARRAY_RESULTS = "results"
        const val JSON_REVIEW_ID = "id"
        const val JSON_AUTHOR = "author"
        const val JSON_CONTENT = "content"
        const val JSON_TOTAL_PAGES = "total_pages"
        fun fromJSON(obj: JSONObject, movieId: String?): MovieReview {
            return MovieReview(
                    movieId,
                    obj.optString(JSON_REVIEW_ID),
                    obj.optString(JSON_AUTHOR),
                    obj.optString(JSON_CONTENT)
            )
        }

        val MOVIES_REVIEWS_CLOMUNS = arrayOf<String?>(
                MovieReviews.TABLE_NAME + "." + MovieReviews._ID,
                MovieReviews.COLUMN_REVIEW_ID,
                MovieReviews.COLUMN_AUTHOR,
                MovieReviews.COLUMN_CONTENT)
        const val COL_ID = 0
        const val COL_REVIEW_ID = 1
        const val COL_AUTHOR = 2
        const val COL_CONTENT = 3
        fun fromCursor(cursor: Cursor?): MovieReview {
            return MovieReview(
                    cursor!!.getString(COL_ID),
                    cursor.getString(COL_REVIEW_ID),
                    cursor.getString(COL_AUTHOR),
                    cursor.getString(COL_CONTENT)
            )
        }
    }

}