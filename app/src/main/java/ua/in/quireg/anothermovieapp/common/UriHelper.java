package ua.in.quireg.anothermovieapp.common;


import android.net.Uri;

import ua.in.quireg.anothermovieapp.BuildConfig;

public class UriHelper {

    private static final String API_KEY = BuildConfig.MOVIE_DATABASE_API_KEY;

    public static Uri getMoviesListUri(String tag){
        switch (tag){
            case Constrains.POPULAR:
                return new Uri.Builder()
                        .scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath("popular")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
            case Constrains.TOP_RATED:
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

}
