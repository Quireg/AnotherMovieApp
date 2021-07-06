package com.anothermovieapp.common

import android.net.Uri
import com.anothermovieapp.BuildConfig

object UriHelper {
    private val LOG_TAG = UriHelper::class.java.simpleName
    private const val API_KEY = BuildConfig.MOVIE_DATABASE_API_KEY
    fun getMoviesListUri(tag: String?): Uri? {
        MovieAppLogger.v(LOG_TAG, "getMoviesListUri()")
        when (tag) {
            Constants.POPULAR -> return Uri.Builder()
                    .scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("popular")
                    .appendQueryParameter("api_key", API_KEY)
                    .build()
            Constants.TOP_RATED -> return Uri.Builder()
                    .scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("top_rated")
                    .appendQueryParameter("api_key", API_KEY)
                    .build()
        }
        return null
    }

    fun getMoviesListPageUri(tag: String?, page: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getMoviesListPageUri()")
        return when (tag) {
            Constants.POPULAR -> Uri.Builder()
                    .scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("popular")
                    .appendQueryParameter("page", page)
                    .appendQueryParameter("api_key", API_KEY)
                    .build()
            Constants.TOP_RATED -> Uri.Builder()
                    .scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("top_rated")
                    .appendQueryParameter("page", page)
                    .appendQueryParameter("api_key", API_KEY)
                    .build()
            else -> Uri.Builder()
                    .scheme("https")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("top_rated")
                    .appendQueryParameter("page", page)
                    .appendQueryParameter("api_key", API_KEY)
                    .build()
        }
    }

    fun getImageUri(imagePath: String?, imageSize: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getImageUri()")
        return Uri.Builder()
                .scheme("https")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath(imageSize)
                .appendPath(imagePath)
                .build()
                .normalizeScheme()
    }

    fun getMovieUriById(id: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getMovieUriById()")
        return Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendQueryParameter("api_key", API_KEY)
                .build()
    }

    fun getMovieTrailerUriById(id: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getMovieTrailerUriById()")
        return Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY)
                .build()
    }

    fun getMovieReviewsUri(id: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getMovieReviewsUri()")
        return Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY)
                .build()
    }

    fun getMovieReviewsUriForPage(id: String?, page: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getMovieReviewsUri()")
        return Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("page", page)
                .appendQueryParameter("api_key", API_KEY)
                .build()
    }

    fun getYouTubeLinkToPlay(id: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getYouTubeLinkToPlay()")
        return Uri.Builder()
                .scheme("https")
                .authority("youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", id)
                .build()
    }

    fun getYouTubeTrailerPreviewLink(trailer_id: String?): Uri {
        MovieAppLogger.v(LOG_TAG, "getYouTubeTrailerPreviewLink()")
        return Uri.Builder()
                .scheme("https")
                .authority("i1.ytimg.com")
                .appendPath("vi")
                .appendPath(trailer_id)
                .appendPath("hqdefault.jpg")
                .build()
    }
}