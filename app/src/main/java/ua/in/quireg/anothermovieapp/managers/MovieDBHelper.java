package ua.in.quireg.anothermovieapp.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.in.quireg.anothermovieapp.common.MLog;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.*;


public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    private Context mContext;
    MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);

        final String SQL_CREATE_MOVIE_ENTITY_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                MovieEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +

                MovieEntry.COLUMN_ADULT +               " TEXT, "    +
                MovieEntry.COLUMN_BACKDROP_PATH +       " TEXT, "    +
                MovieEntry.COLUMN_BUDGET +              " INTEGER,"  +
                MovieEntry.COLUMN_HOMEPAGE +            " TEXT,"     +
                MovieEntry.COLUMN_IMDB_ID +             " INTEGER,"  +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE +   " TEXT,"     +
                MovieEntry.COLUMN_ORIGINAL_TITLE +      " TEXT, "    +
                MovieEntry.COLUMN_OVERVIEW +            " TEXT, "    +
                MovieEntry.COLUMN_POPULARITY +          " REAL, "    +
                MovieEntry.COLUMN_POSTER_PATH +         " TEXT, "    +
                MovieEntry.COLUMN_RELEASE_DATE +        " TEXT, "    +
                MovieEntry.COLUMN_REVENUE +             " INTEGER, " +
                MovieEntry.COLUMN_RUNTIME +             " INTEGER, " +
                MovieEntry.COLUMN_STATUS +              " TEXT, "    +
                MovieEntry.COLUMN_TAGLINE +             " TEXT, "    +
                MovieEntry.COLUMN_TITLE +               " TEXT, "    +
                MovieEntry.COLUMN_VIDEO +               " TEXT, "    +
                MovieEntry.COLUMN_VOTE_AVERAGE +        " REAL, "    +
                MovieEntry.COLUMN_VOTE_COUNT +          " INTEGER " +
                "UNIQUE ON CONFLICT REPLACE);";

        MLog.d(LOG_TAG, "Creating database:");
        MLog.d(LOG_TAG, SQL_CREATE_MOVIE_ENTITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_ENTITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
