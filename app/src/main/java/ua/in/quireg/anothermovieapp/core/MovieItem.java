package ua.in.quireg.anothermovieapp.core;


import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

import java.io.Serializable;

import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;

public class MovieItem implements Serializable {

    private long id;
    private boolean adult;
    private String backdropPath;
    private int budget;
    private String homepage;
    private long imdb_id;
    private String originalLanguage;
    private String originalTitle;
    private String title;
    private String overview;
    private double popularity;
    private String posterPath;
    private String releaseDate;
    private int revenue;
    private int runtime;
    private String status;
    private String tagline;
    private boolean video;
    private double vote_average;
    private int voteCount;

    public MovieItem(long id, boolean adult, String backdropPath, int budget,
                     String homepage, long imdb_id, String originalLanguage,
                     String originalTitle, String title, String overview, double popularity,
                     String posterPath, String releaseDate, int revenue, int runtime,
                     String status, String tagline, boolean video, double vote_average, int voteCount) {
        this.id = id;
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.budget = budget;
        this.homepage = homepage;
        this.imdb_id = imdb_id;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.title = title;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.status = status;
        this.tagline = tagline;
        this.video = video;
        this.vote_average = vote_average;
        this.voteCount = voteCount;
    }

    public static final String[] MOVIES_CLOMUNS = {
            MovieDatabaseContract.MovieEntry.TABLE_NAME + "." + MovieDatabaseContract.MovieEntry._ID,
            MovieDatabaseContract.MovieEntry.COLUMN_ADULT,
            MovieDatabaseContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieDatabaseContract.MovieEntry.COLUMN_BUDGET,
            MovieDatabaseContract.MovieEntry.COLUMN_HOMEPAGE,
            MovieDatabaseContract.MovieEntry.COLUMN_IMDB_ID,
            MovieDatabaseContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieDatabaseContract.MovieEntry.COLUMN_TITLE,
            MovieDatabaseContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDatabaseContract.MovieEntry.COLUMN_POPULARITY,
            MovieDatabaseContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieDatabaseContract.MovieEntry.COLUMN_REVENUE,
            MovieDatabaseContract.MovieEntry.COLUMN_RUNTIME,
            MovieDatabaseContract.MovieEntry.COLUMN_STATUS,
            MovieDatabaseContract.MovieEntry.COLUMN_TAGLINE,
            MovieDatabaseContract.MovieEntry.COLUMN_VIDEO,
            MovieDatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieDatabaseContract.MovieEntry.COLUMN_VOTE_COUNT
    };

    public static ContentValues contentValuesFromObj(MovieItem obj){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDatabaseContract.MovieEntry._ID, obj.getId());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_ADULT, obj.isAdult());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_BACKDROP_PATH, obj.getBackdropPath());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_BUDGET, obj.getBudget());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_HOMEPAGE, obj.getHomepage());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_IMDB_ID, obj.getImdb_id());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, obj.getOriginalLanguage());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE, obj.getOriginalTitle());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_TITLE, obj.getTitle());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_OVERVIEW, obj.getOverview());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_POPULARITY, obj.getPopularity());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_POSTER_PATH, obj.getPosterPath());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE, obj.getReleaseDate());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_REVENUE, obj.getRevenue());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_RUNTIME, obj.getRuntime());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_STATUS, obj.getStatus());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_TAGLINE, obj.getTagline());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_VIDEO, obj.isVideo());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE, obj.getVote_average());
        contentValues.put(MovieDatabaseContract.MovieEntry.COLUMN_VOTE_COUNT, obj.getVoteCount());

        return contentValues;
    }

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_ADULT = 1;
    static final int COL_MOVIE_BACKDROP_PATH = 2;
    static final int COL_MOVIE_BUDGET = 3;
    static final int COL_MOVIE_HOMEPAGE = 4;
    static final int COL_MOVIE_IMDB_ID = 5;
    static final int COL_MOVIE_ORIGINAL_LANGUAGE = 6;
    static final int COL_MOVIE_ORIGINAL_TITLE = 7;
    static final int COL_MOVIE_TITLE = 8;
    static final int COL_MOVIE_OVERVIEW = 9;
    static final int COL_MOVIE_POPULARITY = 10;
    static final int COL_MOVIE_POSTER_PATH = 11;
    static final int COL_MOVIE_RELEASE_DATE = 12;
    static final int COL_MOVIE_REVENUE = 13;
    static final int COL_MOVIE_RUNTIME = 14;
    static final int COL_MOVIE_STATUS = 15;
    static final int COL_MOVIE_TAGLINE = 16;
    static final int COL_MOVIE_VIDEO = 17;
    static final int COL_MOVIE_VOTE_AVERAGE = 18;
    static final int COL_MOVIE_VOTE_COUNT = 19;

    public static final String JSON_ID = "id";
    public static final String JSON_ADULT = "adult";
    public static final String JSON_BACKDROP_PATH = "backdrop_path";
    public static final String JSON_BUDGET = "budget";
    public static final String JSON_HOMEPAGE = "homepage";
    public static final String JSON_IMDB_ID = "imdb_id";
    public static final String JSON_ORIGINAL_LANGUAGE = "original_language";
    public static final String JSON_ORIGINAL_TITLE = "original_title";
    public static final String JSON_TITLE = "title";
    public static final String JSON_OVERVIEW = "overview";
    public static final String JSON_POPULARITY = "popularity";
    public static final String JSON_POSTER_PATH = "poster_path";
    public static final String JSON_RELEASE_DATE = "release_date";
    public static final String JSON_REVENUE = "revenue";
    public static final String JSON_RUNTIME = "runtime";
    public static final String JSON_STATUS = "status";
    public static final String JSON_TAGLINE = "tagline";
    public static final String JSON_VIDEO = "video";
    public static final String JSON_VOTE_AVERAGE = "vote_average";
    public static final String JSON_VOTE_COUNT = "vote_count";


    public static MovieItem fromCursor(Cursor cursor) {
        return new MovieItem(
                cursor.getLong(COL_MOVIE_ID),
                Boolean.valueOf(cursor.getString(COL_MOVIE_ADULT)),
                cursor.getString(COL_MOVIE_BACKDROP_PATH),
                cursor.getInt(COL_MOVIE_BUDGET),
                cursor.getString(COL_MOVIE_HOMEPAGE),
                cursor.getLong(COL_MOVIE_IMDB_ID),
                cursor.getString(COL_MOVIE_ORIGINAL_LANGUAGE),
                cursor.getString(COL_MOVIE_ORIGINAL_TITLE),
                cursor.getString(COL_MOVIE_TITLE),
                cursor.getString(COL_MOVIE_OVERVIEW),
                cursor.getDouble(COL_MOVIE_POPULARITY),
                cursor.getString(COL_MOVIE_POSTER_PATH),
                cursor.getString(COL_MOVIE_RELEASE_DATE),
                cursor.getInt(COL_MOVIE_REVENUE),
                cursor.getInt(COL_MOVIE_RUNTIME),
                cursor.getString(COL_MOVIE_STATUS),
                cursor.getString(COL_MOVIE_TAGLINE),
                Boolean.valueOf(cursor.getString(COL_MOVIE_VIDEO)),
                cursor.getDouble(COL_MOVIE_VOTE_AVERAGE),
                cursor.getInt(COL_MOVIE_VOTE_COUNT)
        );
    }

    public static MovieItem fromJSON(JSONObject obj){
        return new MovieItem(
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
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public long getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(long imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
