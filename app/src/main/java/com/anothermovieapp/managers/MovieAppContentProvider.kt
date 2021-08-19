package com.anothermovieapp.managers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.anothermovieapp.managers.MovieDatabaseContract.CONTENT_AUTHORITY
import com.anothermovieapp.managers.MovieDatabaseContract.PATH_FAVOURITE_MOVIES
import com.anothermovieapp.managers.MovieDatabaseContract.PATH_MOVIE_ENTITY
import com.anothermovieapp.managers.MovieDatabaseContract.PATH_MOVIE_REVIEWS
import com.anothermovieapp.managers.MovieDatabaseContract.PATH_POPULAR_MOVIES
import com.anothermovieapp.managers.MovieDatabaseContract.PATH_TOP_RATED_MOVIES

class MovieAppContentProvider /*: ContentProvider() */{
//    private var mOpenHelper: MovieDBHelper? = null
//
//    companion object {
//        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
//        private var sMovieFromPopularQueryBuilder: SQLiteQueryBuilder? = null
//        private var sMovieFromTopRatedQueryBuilder: SQLiteQueryBuilder? = null
//        private var sMovieFromFavouritesQueryBuilder: SQLiteQueryBuilder? = null
//        const val MOVIE_DIR = 101
//        const val MOVIE_ITEM = 102
//        const val FAVOURITE_MOVIE_DIR = 201
//        const val FAVOURITE_MOVIE_ITEM = 202
//        const val POPULAR_MOVIE_DIR = 301
//
//        // public static final int POPULAR_MOVIE_ITEM = 302;
//        const val TOP_RATED_MOVIE_DIR = 401
//
//        //public static final int TOP_RATED_MOVIE_ITEM = 402;
//        const val MOVIE_REVIEWS_DIR = 501
//        const val MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE = 502
//
//        init {
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_ENTITY, MOVIE_DIR)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_ENTITY + "/#", MOVIE_ITEM)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIE_DIR)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVOURITE_MOVIES + "/#", FAVOURITE_MOVIE_ITEM)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_POPULAR_MOVIES, POPULAR_MOVIE_DIR)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TOP_RATED_MOVIES, TOP_RATED_MOVIE_DIR)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_REVIEWS, MOVIE_REVIEWS_DIR)
//            sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_REVIEWS + "/#", MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE)
//            sMovieFromPopularQueryBuilder = SQLiteQueryBuilder()
//            sMovieFromPopularQueryBuilder?.setTables(
//                    MovieDatabaseContract.PopularMovies.TABLE_NAME + " LEFT JOIN " +
//                            MovieDatabaseContract.MovieEntry.TABLE_NAME +
//                            " ON " + MovieDatabaseContract.MovieEntry.TABLE_NAME +
//                            "." + MovieDatabaseContract.MovieEntry._ID +
//                            " = " + MovieDatabaseContract.PopularMovies.TABLE_NAME +
//                            "." + MovieDatabaseContract.PopularMovies._ID
//            )
//            sMovieFromTopRatedQueryBuilder = SQLiteQueryBuilder()
//            sMovieFromTopRatedQueryBuilder?.setTables(
//                    MovieDatabaseContract.TopRatedMovies.TABLE_NAME + " LEFT JOIN " +
//                            MovieDatabaseContract.MovieEntry.TABLE_NAME +
//                            " ON " + MovieDatabaseContract.MovieEntry.TABLE_NAME +
//                            "." + MovieDatabaseContract.MovieEntry._ID +
//                            " = " + MovieDatabaseContract.TopRatedMovies.TABLE_NAME +
//                            "." + MovieDatabaseContract.TopRatedMovies._ID
//            )
//            sMovieFromFavouritesQueryBuilder = SQLiteQueryBuilder()
//            sMovieFromFavouritesQueryBuilder?.setTables(
//                    MovieDatabaseContract.FavouriteMovies.TABLE_NAME + " LEFT JOIN " +
//                            MovieDatabaseContract.MovieEntry.TABLE_NAME +
//                            " ON " + MovieDatabaseContract.MovieEntry.TABLE_NAME +
//                            "." + MovieDatabaseContract.MovieEntry._ID +
//                            " = " + MovieDatabaseContract.FavouriteMovies.TABLE_NAME +
//                            "." + MovieDatabaseContract.FavouriteMovies._ID
//            )
//        }
//    }
//
//    override fun onCreate(): Boolean {
//        mOpenHelper = MovieDBHelper(context)
//        return true
//    }
//
//    override fun query(uri: Uri, projection: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder: String): Cursor? {
//        val cursor: Cursor
//        cursor = when (sUriMatcher.match(uri)) {
//            MOVIE_DIR -> mOpenHelper!!.readableDatabase.query(
//                    MovieDatabaseContract.MovieEntry.TABLE_NAME,
//                    projection,
//                    null,
//                    null,
//                    null,
//                    null,
//                    "_ID ASC"
//            )
//            MOVIE_ITEM -> mOpenHelper!!.readableDatabase.query(
//                    MovieDatabaseContract.MovieEntry.TABLE_NAME,
//                    projection,
//                    MovieDatabaseContract.MovieEntry._ID + " = " + uri.lastPathSegment,
//                    null,
//                    null,
//                    null,
//                    "_ID ASC",
//                    "LIMIT 1"
//            )
//            FAVOURITE_MOVIE_DIR -> sMovieFromFavouritesQueryBuilder!!.query(
//                    mOpenHelper!!.readableDatabase,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null,
//                    null,
//                    sortOrder)
//            FAVOURITE_MOVIE_ITEM -> mOpenHelper!!.readableDatabase.query(
//                    MovieDatabaseContract.FavouriteMovies.TABLE_NAME,
//                    projection,
//                    MovieDatabaseContract.FavouriteMovies._ID + " = " + uri.lastPathSegment,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            )
//            POPULAR_MOVIE_DIR -> sMovieFromPopularQueryBuilder!!.query(
//                    mOpenHelper!!.readableDatabase,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null,
//                    null,
//                    sortOrder)
//            TOP_RATED_MOVIE_DIR -> sMovieFromTopRatedQueryBuilder!!.query(
//                    mOpenHelper!!.readableDatabase,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null,
//                    null,
//                    sortOrder)
//            MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE -> mOpenHelper!!.readableDatabase.query(
//                    MovieDatabaseContract.MovieReviews.TABLE_NAME,
//                    projection,
//                    MovieDatabaseContract.MovieReviews._ID + " = " + uri.lastPathSegment,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            )
//            else -> throw UnsupportedOperationException("Unknown uri: $uri")
//        }
//        cursor.setNotificationUri(context.contentResolver, uri)
//        return cursor
//    }
//
//    override fun getType(uri: Uri): String? {
//        return when (sUriMatcher.match(uri)) {
//            MOVIE_DIR -> MovieEntry.CONTENT_DIR_TYPE
//            MOVIE_ITEM -> MovieEntry.CONTENT_ITEM_TYPE
//            FAVOURITE_MOVIE_DIR -> FavouriteMovies.CONTENT_DIR_TYPE
//            FAVOURITE_MOVIE_ITEM -> FavouriteMovies.CONTENT_ITEM_TYPE
//            POPULAR_MOVIE_DIR -> PopularMovies.CONTENT_DIR_TYPE
//            TOP_RATED_MOVIE_DIR -> TopRatedMovies.CONTENT_DIR_TYPE
//            MOVIE_REVIEWS_DIR -> MovieReviews.CONTENT_DIR_TYPE
//            MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE -> MovieReviews.CONTENT_DIR_TYPE
//            else -> throw UnsupportedOperationException("Unknown uri: $uri")
//        }
//    }
//
//    override fun bulkInsert(uri: Uri, valuesArray: Array<ContentValues>): Int {
//        val db = mOpenHelper!!.writableDatabase
//        val match = sUriMatcher.match(uri)
//        db.beginTransaction()
//        try {
//            for (values in valuesArray) {
//                when (match) {
//                    MOVIE_DIR -> db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//                    FAVOURITE_MOVIE_DIR -> db.insertWithOnConflict(FavouriteMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//                    POPULAR_MOVIE_DIR -> db.insertWithOnConflict(PopularMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//                    TOP_RATED_MOVIE_DIR -> db.insertWithOnConflict(TopRatedMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//                    MOVIE_REVIEWS_DIR -> db.insertWithOnConflict(MovieReviews.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//                    else -> throw UnsupportedOperationException("Unknown uri: $uri")
//                }
//            }
//            db.setTransactionSuccessful()
//        } finally {
//            db.endTransaction()
//        }
//        context.contentResolver.notifyChange(uri, null)
//        return 0
//    }
//
//    override fun insert(uri: Uri, values: ContentValues): Uri? {
//        val db = mOpenHelper!!.writableDatabase
//        val match = sUriMatcher.match(uri)
//        when (match) {
//            MOVIE_DIR -> db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//            FAVOURITE_MOVIE_DIR -> db.insertWithOnConflict(FavouriteMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//            POPULAR_MOVIE_DIR -> db.insertWithOnConflict(PopularMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//            TOP_RATED_MOVIE_DIR -> db.insertWithOnConflict(TopRatedMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
//            else -> throw UnsupportedOperationException("Unknown uri: $uri")
//        }
//        context.contentResolver.notifyChange(uri, null)
//        return uri
//    }
//
//    override fun delete(uri: Uri, selection: String, selectionArgs: Array<String>): Int {
//        var selection: String? = selection
//        var selectionArgs: Array<String>? = selectionArgs
//        val db = mOpenHelper!!.writableDatabase
//        val match = sUriMatcher.match(uri)
//        val rowsDeleted: Int
//        return when (match) {
//            MOVIE_ITEM -> {
//                selection = MovieEntry._ID + " =?"
//                selectionArgs = arrayOf(uri.lastPathSegment)
//                rowsDeleted = db.delete(
//                        MovieEntry.TABLE_NAME,
//                        selection,
//                        selectionArgs
//                )
//                if (rowsDeleted > 0) {
//                    context.contentResolver.notifyChange(uri, null)
//                } else {
//                    throw SQLException("Failed to delete row $uri")
//                }
//                context.contentResolver.notifyChange(uri, null)
//                rowsDeleted
//            }
//            POPULAR_MOVIE_DIR -> {
//                rowsDeleted = db.delete(
//                        PopularMovies.TABLE_NAME,
//                        null,
//                        null
//                )
//                //                if (rowsDeleted > 0) {
////                    //getContext().getContentResolver().notifyChange(uri, null);
////                } else {
////                    throw new android.database.SQLException("Failed to delete row " + uri);
////                }
//                //getContext().getContentResolver().notifyChange(uri, null);
//                rowsDeleted
//            }
//            TOP_RATED_MOVIE_DIR -> {
//                rowsDeleted = db.delete(
//                        TopRatedMovies.TABLE_NAME,
//                        null,
//                        null
//                )
//                //                if (rowsDeleted > 0) {
////                    //getContext().getContentResolver().notifyChange(uri, null);
////                } else {
////                    throw new android.database.SQLException("Failed to delete row " + uri);
////                }
//                //getContext().getContentResolver().notifyChange(uri, null);
//                rowsDeleted
//            }
//            FAVOURITE_MOVIE_ITEM -> {
//                selection = FavouriteMovies._ID + " =?"
//                selectionArgs = arrayOf(uri.lastPathSegment)
//                rowsDeleted = db.delete(
//                        FavouriteMovies.TABLE_NAME,
//                        selection,
//                        selectionArgs
//                )
//                //                if (rowsDeleted > 0) {
////                    getContext().getContentResolver().notifyChange(uri, null);
////                } else {
////                    throw new android.database.SQLException("Failed to delete row " + uri);
////                }
//                context.contentResolver.notifyChange(uri, null)
//                rowsDeleted
//            }
//            else -> throw UnsupportedOperationException("Unknown uri: $uri")
//        }
//    }
//
//    override fun update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
//        throw UnsupportedOperationException("Unsupported operation")
//    }
}