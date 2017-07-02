package ua.in.quireg.anothermovieapp.common;


import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;

public class MovieReview {
    public String movieId;
    public String reviewId;
    public String author;
    public String content;

    public MovieReview(String movieId, String reviewId, String author, String content) {
        this.movieId = movieId;
        this.reviewId = reviewId;
        this.author = author;
        this.content = content;
    }



    public static ContentValues contentValuesFromObj(MovieReview obj) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDatabaseContract.MovieReviews._ID, obj.movieId);
        contentValues.put(MovieDatabaseContract.MovieReviews.COLUMN_REVIEW_ID, obj.reviewId);
        contentValues.put(MovieDatabaseContract.MovieReviews.COLUMN_AUTHOR, obj.author);
        contentValues.put(MovieDatabaseContract.MovieReviews.COLUMN_CONTENT, obj.content);
        return contentValues;
    }

    public static final String JSON_ID = "id";
    public static final String JSON_PAGE = "page";
    public static final String JSON_ARRAY_RESULTS = "results";
    public static final String JSON_REVIEW_ID = "id";
    public static final String JSON_AUTHOR = "author";
    public static final String JSON_CONTENT = "content";
    public static final String JSON_TOTAL_PAGES = "total_pages";

    public static MovieReview fromJSON(JSONObject obj, String movieId) {
        return new MovieReview(
                movieId,
                obj.optString(JSON_REVIEW_ID),
                obj.optString(JSON_AUTHOR),
                obj.optString(JSON_CONTENT)

        );
    }
    public static final String[] MOVIES_REVIEWS_CLOMUNS = {
            MovieDatabaseContract.MovieReviews.TABLE_NAME + "." + MovieDatabaseContract.MovieReviews._ID,
            MovieDatabaseContract.MovieReviews.COLUMN_REVIEW_ID,
            MovieDatabaseContract.MovieReviews.COLUMN_AUTHOR,
            MovieDatabaseContract.MovieReviews.COLUMN_CONTENT,

    };

    static final int COL_ID = 0;
    static final int COL_REVIEW_ID = 1;
    static final int COL_AUTHOR = 2;
    static final int COL_CONTENT = 3;

    public static MovieReview fromCursor(Cursor cursor) {
        return new MovieReview(
                cursor.getString(COL_ID),
                cursor.getString(COL_REVIEW_ID),
                cursor.getString(COL_AUTHOR),
                cursor.getString(COL_CONTENT)
        );
    }
}
