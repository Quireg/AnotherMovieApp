/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EntityDBMovie::class, EntityDBMovieTrailer::class, EntityDBMovieReview::class,
        EntityDBPopularList::class, EntityDBFavoriteMovie::class, EntityDBTopRatedList::class,
        EntityDBPopularListFetchState::class, EntityDBTopRatedListFetchState::class,
        EntityDBReviewsListFetchState::class],
    version = 10, exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun movieDao(): DaoMovie
    abstract fun movieTrailerDao(): DaoMovieTrailer
    abstract fun movieReviewDao(): DaoMovieReview
    abstract fun moviePopularDao(): DaoListPopular
    abstract fun movieTopRatedDao(): DaoListTopRated
    abstract fun movieFavoriteDao(): DaoListFavorite
}