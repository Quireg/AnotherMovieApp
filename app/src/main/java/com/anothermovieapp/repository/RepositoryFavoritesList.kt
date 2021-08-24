/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.flow.Flow

interface RepositoryFavoritesList {
    suspend fun getFavoriteMovies(): Flow<List<EntityDBMovie>>
    suspend fun remove(id: Long)
    suspend fun add(id: Long)
    suspend fun addOrRemove(id: Long)
    suspend fun isFavourite(id: Long) : Flow<Boolean>
}