/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.common

object Constants {
    const val POPULAR = "POPULAR"
    const val TOP_RATED = "TOP_RATED"
    const val FAVOURITES = "FAVOURITES"
    const val MOVIE = "movie_bundle"
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