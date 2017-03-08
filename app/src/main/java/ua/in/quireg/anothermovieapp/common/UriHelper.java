package ua.in.quireg.anothermovieapp.common;


import android.net.Uri;

import ua.in.quireg.anothermovieapp.BuildConfig;

public class UriHelper {

    private static final String API_KEY = BuildConfig.MOVIE_DATABASE_API_KEY;

    public static Uri getMoviesListUri(String tag){
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

    public static Uri getOriginalSizeBitmapUri(String imagePath){
        return new Uri.Builder()
                .scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("original")
                .appendPath(imagePath)
                .build();
    }

    public static Uri getW185SizeBitmapUri(String imagePath){
        return new Uri.Builder()
                .scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(imagePath)
                .build();
    }

}