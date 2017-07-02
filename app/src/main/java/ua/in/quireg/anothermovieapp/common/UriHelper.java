package ua.in.quireg.anothermovieapp.common;


import android.net.Uri;

import ua.in.quireg.anothermovieapp.BuildConfig;

public class UriHelper {
    private static final String LOG_TAG = UriHelper.class.getSimpleName();
    private static final String API_KEY = BuildConfig.MOVIE_DATABASE_API_KEY;

    public static Uri getMoviesListUri(String tag){
        MovieAppLogger.v(LOG_TAG, "getMoviesListUri()");
        switch (tag){
            case Constants.POPULAR:
                return new Uri.Builder()
                        .scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("popular")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
            case Constants.TOP_RATED:
                return new Uri.Builder()
                        .scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("top_rated")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
        }
        return null;
    }

    public static Uri getMoviesListPageUri(String tag, String page){
        MovieAppLogger.v(LOG_TAG, "getMoviesListPageUri()");

        switch (tag){
            case Constants.POPULAR:
                return new Uri.Builder()
                        .scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("popular")
                        .appendQueryParameter("page", page)
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
            case Constants.TOP_RATED:
                return new Uri.Builder()
                        .scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("top_rated")
                        .appendQueryParameter("page", page)
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
        }
        return null;
    }

    public static Uri getImageUri(String imagePath, String imageSize){
        MovieAppLogger.v(LOG_TAG, "getImageUri()");
        return new Uri.Builder()
                .scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath(imageSize)
                .appendPath(imagePath)
                .build()
                .normalizeScheme();
    }

    public static Uri getMovieUriById(String id){
        MovieAppLogger.v(LOG_TAG, "getMovieUriById()");
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendQueryParameter("api_key", API_KEY)
                .build();
    }

    public static Uri getMovieTrailerUriById(String id){
        MovieAppLogger.v(LOG_TAG, "getMovieTrailerUriById()");
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY)
                .build();
    }

    public static Uri getMovieReviewsUri(String id){
        MovieAppLogger.v(LOG_TAG, "getMovieReviewsUri()");
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY)
                .build();
    }
    public static Uri getMovieReviewsUriForPage(String id, String page){
        MovieAppLogger.v(LOG_TAG, "getMovieReviewsUri()");
        return new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("page", page)
                .appendQueryParameter("api_key", API_KEY)
                .build();
    }

    public static Uri getYouTubeLinkToPlay(String id){
        MovieAppLogger.v(LOG_TAG, "getYouTubeLinkToPlay()");
        return new Uri.Builder()
                .scheme("https")
                .authority("youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", id)
                .build();
    }

    public static Uri getYouTubeTrailerPreviewLink(String trailer_id){
        MovieAppLogger.v(LOG_TAG, "getYouTubeTrailerPreviewLink()");
        return new Uri.Builder()
                .scheme("https")
                .authority("i1.ytimg.com")
                .appendPath("vi")
                .appendPath(trailer_id)
                .appendPath("hqdefault.jpg")
                .build();
    }

}
