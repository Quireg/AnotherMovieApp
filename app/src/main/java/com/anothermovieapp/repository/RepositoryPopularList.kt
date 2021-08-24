/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.flow.Flow

interface RepositoryPopularList {
    suspend fun getPopularMovies() : Flow<Resource<ModelPopularListRespond>>
    suspend fun getTotalNumber() : Flow<Long>
    suspend fun fetchMorePopular()
    suspend fun reloadPopular()
}