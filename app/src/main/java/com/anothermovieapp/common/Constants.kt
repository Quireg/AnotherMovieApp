package com.anothermovieapp.common

import com.anothermovieapp.BuildConfig

object Constants {
    const val YOUTUBE_BASE_LINK = "https://www.youtube.com/watch?v="
    const val INSET_TOP = "inset_top"
    const val INSET_BOT = "inset_bot"
    const val FRAGMENT_TAG = "fragment_tag"
    const val POPULAR = "POPULAR"
    const val TOP_RATED = "TOP_RATED"
    const val FAVOURITES = "FAVOURITES"
    const val REVIEWS = "REVIEWS"
    const val MOVIE = "movie_bundle"
    const val SYNC_MOVIE_UPDATES_FILTER = "com.anothermovieapp.SYNC_MOVIE_UPDATES_FILTER"
    const val SYNC_REVIEWS_UPDATES_FILTER = "com.anothermovieapp.SYNC_REVIEWS_UPDATES_FILTER"
    const val TOTAL_ITEMS_LOADED = "TOTAL_ITEMS_LOADED"
    const val TOTAL_PAGES = "TOTAL_PAGES"
    const val LOADED_PAGE = "LOADED_PAGE"
    const val SYNC_STATUS = "SYNC_STATUS"
    const val SYNC_FAILED = "SYNC_FAILED"
    const val SYNC_COMPLETED = "SYNC_COMPLETED"
    const val LOGGING = true
    const val COLUMNS_NUMBER = 2
    const val IMAGE_SIZE_ORIGINAL = "original"
    const val IMAGE_SIZE_W185 = "w185"
    const val IMAGE_SIZE_W300 = "w300"
    const val IMAGE_SIZE_W780 = "w780"

    //fragment interaction
    const val ADD_TO_FAVOURITES = "add_to_favourites"
    const val REMOVE_FROM_FAVOURITES = "remove_from_favourites"

    //favourites fragment sorting
    const val SORT_ORDER = "sort_order"
    const val SORT_BY_NAME = 0
    const val SORT_BY_RELEASE_DATE = 1
    const val SORT_BY_RATING = 2
    const val SORT_BY_VOTES_COUNT = 3
}