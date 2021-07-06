package com.anothermovieapp.managers

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object MovieDatabaseContract {
    const val CONTENT_AUTHORITY = "com.anothermovieapp.app"
    private val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
    const val PATH_MOVIE_ENTITY = "movie"
    const val PATH_TOP_RATED_MOVIES = "top_rated_movies"
    const val PATH_POPULAR_MOVIES = "popular_movies"
    const val PATH_FAVOURITE_MOVIES = "favourite_movies"
    const val PATH_MOVIE_REVIEWS = "movie_reviews"

    object MovieEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_ENTITY).build()
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_ENTITY
        const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_ENTITY
        const val TABLE_NAME = "movie_entity"
        const val _ID = "id"
        const val COLUMN_ADULT = "adult"
        const val COLUMN_BACKDROP_PATH = "backdrop_path"
        const val COLUMN_BUDGET = "budget"
        const val COLUMN_HOMEPAGE = "homepage"
        const val COLUMN_IMDB_ID = "imdb_id"
        const val COLUMN_ORIGINAL_LANGUAGE = "original_language"
        const val COLUMN_ORIGINAL_TITLE = "original_title"
        const val COLUMN_OVERVIEW = "overview"
        const val COLUMN_POPULARITY = "popularity"
        const val COLUMN_POSTER_PATH = "poster_path"
        const val COLUMN_RELEASE_DATE = "release_date"
        const val COLUMN_REVENUE = "revenue"
        const val COLUMN_RUNTIME = "runtime"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TAGLINE = "tagline"
        const val COLUMN_TITLE = "title"
        const val COLUMN_VIDEO = "video"
        const val COLUMN_VOTE_AVERAGE = "vote_average"
        const val COLUMN_VOTE_COUNT = "vote_count"
        fun buildUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }

    object TopRatedMovies : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED_MOVIES).build()
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIES
        const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIES
        const val TABLE_NAME = "top_rated_movies"
        const val _ID = "id"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PAGE = "page"
        fun buildUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }

    object PopularMovies : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR_MOVIES).build()
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIES
        const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIES
        const val TABLE_NAME = "popular_movies"
        const val _ID = "id"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PAGE = "page"
        fun buildUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }

    object FavouriteMovies : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIES).build()
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIES
        const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE_MOVIES
        const val TABLE_NAME = "favourite_movies"
        const val _ID = "id"
        fun buildUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }

    object MovieReviews : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEWS).build()
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS
        const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS
        const val TABLE_NAME = "movie_reviews"
        const val _ID = "movie_id"
        const val COLUMN_REVIEW_ID = "review_id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        fun buildUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }
}