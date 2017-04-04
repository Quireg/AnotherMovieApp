package ua.in.quireg.anothermovieapp.managers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.*;



public class MovieEntityProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDBHelper mOpenHelper;

    public static final int MOVIE_DIR = 101;
    public static final int MOVIE_ITEM = 102;

    static {
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_ENTITY, MOVIE_DIR);
        sUriMatcher.addURI(MovieDatabaseContract.CONTENT_AUTHORITY, MovieDatabaseContract.PATH_MOVIE_ENTITY + "/#", MOVIE_ITEM);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)){
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE_DIR:
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = MovieEntry.buildUri(_id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;


        switch (match){
            case MOVIE_ITEM:
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME,
                        MovieEntry._ID + " =?",
                        new String[]{uri.getLastPathSegment()}
                );
                if ( rowsDeleted > 0 ) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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
