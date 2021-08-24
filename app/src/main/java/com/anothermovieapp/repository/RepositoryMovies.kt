/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.flow.Flow

interface RepositoryMovies {

    suspend fun getMovies(): Flow<Resource<List<EntityDBMovie>>>
    suspend fun getMovieTrailers(id: String): Flow<Resource<List<EntityDBMovieTrailer>>>
    suspend fun getMovieReviews(id: String): Flow<Resource<ModelReviewsListRespond>>
    suspend fun fetchMoreReviews()

}