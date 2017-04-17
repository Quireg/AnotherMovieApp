package ua.in.quireg.anothermovieapp.managers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.*;


public class MovieEntityProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDBHelper mOpenHelper;
    private static final SQLiteQueryBuilder sMovieFromPopularQueryBuilder;
    private static final SQLiteQueryBuilder sMovieFromTopRatedQueryBuilder;
    private static final SQLiteQueryBuilder sMovieFromFavouritesQueryBuilder;

    public static final int MOVIE_DIR = 101;
    public static final int MOVIE_ITEM = 102;

    public static final int FAVOURITE_MOVIE_DIR = 201;
    //public static final int FAVOURITE_MOVIE_ITEM = 202;

    public static final int POPULAR_MOVIE_DIR = 301;
    // public static final int POPULAR_MOVIE_ITEM = 302;

    public static final int TOP_RATED_MOVIE_DIR = 401;
    //public static final int TOP_RATED_MOVIE_ITEM = 402;


    static {
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_ENTITY, MOVIE_DIR);
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_ENTITY + "/#", MOVIE_ITEM);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIE_DIR);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_POPULAR_MOVIES, POPULAR_MOVIE_DIR);

        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_TOP_RATED_MOVIES, TOP_RATED_MOVIE_DIR);

        sMovieFromPopularQueryBuilder = new SQLiteQueryBuilder();
        sMovieFromPopularQueryBuilder.setTables(
                MovieEntry.TABLE_NAME + " INNER JOIN " +
                        PopularMovies.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + PopularMovies.TABLE_NAME +
                        "." + PopularMovies._ID


        );

        sMovieFromTopRatedQueryBuilder = new SQLiteQueryBuilder();
        sMovieFromTopRatedQueryBuilder.setTables(
                MovieEntry.TABLE_NAME + " INNER JOIN " +
                        TopRatedMovies.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + TopRatedMovies.TABLE_NAME +
                        "." + TopRatedMovies._ID


        );

        sMovieFromFavouritesQueryBuilder = new SQLiteQueryBuilder();
        sMovieFromFavouritesQueryBuilder.setTables(
                MovieEntry.TABLE_NAME + " INNER JOIN " +
                        FavouriteMovies.TABLE_NAME +
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
        switch (sUriMatcher.match(uri)) {
            case MOVIE_DIR:
                return mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        "_ID ASC"
                );
            case MOVIE_ITEM:
                return mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        "_ID = " + uri.getLastPathSegment(),
                        null,
                        null,
                        null,
                        "_ID ASC",
                        "LIMIT 1"
                );
            case FAVOURITE_MOVIE_DIR:
                return sMovieFromFavouritesQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case POPULAR_MOVIE_DIR:
                return sMovieFromPopularQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case TOP_RATED_MOVIE_DIR:
                return sMovieFromTopRatedQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
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
            case POPULAR_MOVIE_DIR:
                return PopularMovies.CONTENT_DIR_TYPE;
            case TOP_RATED_MOVIE_DIR:
                return TopRatedMovies.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
//        Uri returnUri;
//        long _id;
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
                if (rowsDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("Failed to delete row " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case FAVOURITE_MOVIE_DIR:
                rowsDeleted = db.delete(
                        FavouriteMovies.TABLE_NAME,
                        null,
                        null
                );
                if (rowsDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("Failed to delete row " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case TOP_RATED_MOVIE_DIR:
                rowsDeleted = db.delete(
                        TopRatedMovies.TABLE_NAME,
                        null,
                        null
                );
                if (rowsDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("Failed to delete row " + uri);
                }
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
