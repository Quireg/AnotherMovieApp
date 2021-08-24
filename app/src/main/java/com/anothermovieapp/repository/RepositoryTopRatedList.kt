/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.flow.Flow

interface RepositoryTopRatedList {
    suspend fun getTopRatedMovies(): Flow<Resource<ModelTopRatedListRespond>>
    suspend fun fetchMoreTopRated()
    suspend fun reloadTopRated()
    suspend fun getTotalNumber(): Flow<Long>
}