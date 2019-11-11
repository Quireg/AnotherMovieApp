package ua.in.quireg.anothermovieapp.managers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.Nullable;

import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.FavouriteMovies;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.MovieEntry;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.MovieReviews;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.PopularMovies;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.TopRatedMovies;


public class MovieAppContentProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDBHelper mOpenHelper;
    private static final SQLiteQueryBuilder sMovieFromPopularQueryBuilder;
    private static final SQLiteQueryBuilder sMovieFromTopRatedQueryBuilder;
    private static final SQLiteQueryBuilder sMovieFromFavouritesQueryBuilder;

    public static final int MOVIE_DIR = 101;
    public static final int MOVIE_ITEM = 102;

    public static final int FAVOURITE_MOVIE_DIR = 201;
    public static final int FAVOURITE_MOVIE_ITEM = 202;

    public static final int POPULAR_MOVIE_DIR = 301;
    // public static final int POPULAR_MOVIE_ITEM = 302;

    public static final int TOP_RATED_MOVIE_DIR = 401;
    //public static final int TOP_RATED_MOVIE_ITEM = 402;

    public static final int MOVIE_REVIEWS_DIR = 501;
    public static final int MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE = 502;


    static {
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_ENTITY, MOVIE_DIR);
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_ENTITY + "/#", MOVIE_ITEM);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIE_DIR);
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_FAVOURITE_MOVIES + "/#", FAVOURITE_MOVIE_ITEM);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_POPULAR_MOVIES, POPULAR_MOVIE_DIR);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_TOP_RATED_MOVIES, TOP_RATED_MOVIE_DIR);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_REVIEWS, MOVIE_REVIEWS_DIR);
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_REVIEWS  + "/#", MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE);

        sMovieFromPopularQueryBuilder = new SQLiteQueryBuilder();
        sMovieFromPopularQueryBuilder.setTables(
                PopularMovies.TABLE_NAME + " LEFT JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + PopularMovies.TABLE_NAME +
                        "." + PopularMovies._ID


        );

        sMovieFromTopRatedQueryBuilder = new SQLiteQueryBuilder();
        sMovieFromTopRatedQueryBuilder.setTables(
                TopRatedMovies.TABLE_NAME + " LEFT JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + TopRatedMovies.TABLE_NAME +
                        "." + TopRatedMovies._ID


        );

        sMovieFromFavouritesQueryBuilder = new SQLiteQueryBuilder();
        sMovieFromFavouritesQueryBuilder.setTables(
                FavouriteMovies.TABLE_NAME + " LEFT JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + FavouriteMovies.TABLE_NAME +
                        "." + FavouriteMovies._ID


        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_DIR:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        "_ID ASC"
                );
                break;
            case MOVIE_ITEM:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry._ID + " = " + uri.getLastPathSegment(),
                        null,
                        null,
                        null,
                        "_ID ASC",
                        "LIMIT 1"
                );
                break;
            case FAVOURITE_MOVIE_DIR:
                cursor = sMovieFromFavouritesQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVOURITE_MOVIE_ITEM:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavouriteMovies.TABLE_NAME,
                        projection,
                        FavouriteMovies._ID + " = " + uri.getLastPathSegment(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            case POPULAR_MOVIE_DIR:
                cursor = sMovieFromPopularQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOP_RATED_MOVIE_DIR:
                cursor = sMovieFromTopRatedQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieReviews.TABLE_NAME,
                        projection,
                        MovieReviews._ID + " = " + uri.getLastPathSegment(),
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE_DIR:
                return MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_ITEM:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case FAVOURITE_MOVIE_DIR:
                return FavouriteMovies.CONTENT_DIR_TYPE;
            case FAVOURITE_MOVIE_ITEM:
                return FavouriteMovies.CONTENT_ITEM_TYPE;
            case POPULAR_MOVIE_DIR:
                return PopularMovies.CONTENT_DIR_TYPE;
            case TOP_RATED_MOVIE_DIR:
                return TopRatedMovies.CONTENT_DIR_TYPE;
            case MOVIE_REVIEWS_DIR:
                return MovieReviews.CONTENT_DIR_TYPE;
            case MOVIE_REVIEWS_DIR_FOR_SINGLE_MOVIE:
                return MovieReviews.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valuesArray) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);


        db.beginTransaction();
        try {
            for (ContentValues values : valuesArray) {
                switch (match) {
                    case MOVIE_DIR:
                        db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        break;
                    case FAVOURITE_MOVIE_DIR:
                        db.insertWithOnConflict(FavouriteMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        break;

                    case POPULAR_MOVIE_DIR:
                        db.insertWithOnConflict(PopularMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        break;

                    case TOP_RATED_MOVIE_DIR:
                        db.insertWithOnConflict(TopRatedMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        break;
                    case MOVIE_REVIEWS_DIR:
                        db.insertWithOnConflict(MovieReviews.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        break;

                    default:
                        throw new UnsupportedOperationException("Unknown uri: " + uri);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_DIR:
                db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            case FAVOURITE_MOVIE_DIR:
                db.insertWithOnConflict(FavouriteMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            case POPULAR_MOVIE_DIR:
                db.insertWithOnConflict(PopularMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            case TOP_RATED_MOVIE_DIR:
                db.insertWithOnConflict(TopRatedMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;


        switch (match) {
            case MOVIE_ITEM:
                selection = MovieEntry._ID + " =?";
                selectionArgs = new String[]{uri.getLastPathSegment()};

                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                if (rowsDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("Failed to delete row " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case POPULAR_MOVIE_DIR:
                rowsDeleted = db.delete(
                        PopularMovies.TABLE_NAME,
                        null,
                        null
                );
//                if (rowsDeleted > 0) {
//                    //getContext().getContentResolver().notifyChange(uri, null);
//                } else {
//                    throw new android.database.SQLException("Failed to delete row " + uri);
//                }
                //getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case TOP_RATED_MOVIE_DIR:
                rowsDeleted = db.delete(
                        TopRatedMovies.TABLE_NAME,
                        null,
                        null
                );
//                if (rowsDeleted > 0) {
//                    //getContext().getContentResolver().notifyChange(uri, null);
//                } else {
//                    throw new android.database.SQLException("Failed to delete row " + uri);
//                }
                //getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case FAVOURITE_MOVIE_ITEM:
                selection = FavouriteMovies._ID + " =?";
                selectionArgs = new String[]{uri.getLastPathSegment()};

                rowsDeleted = db.delete(
                        FavouriteMovies.TABLE_NAME,
                        selection,
                        selectionArgs
                );
//                if (rowsDeleted > 0) {
//                    getContext().getContentResolver().notifyChange(uri, null);
//                } else {
//                    throw new android.database.SQLException("Failed to delete row " + uri);
//                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

}
