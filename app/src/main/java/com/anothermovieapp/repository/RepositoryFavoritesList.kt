package com.anothermovieapp.repository

import androidx.lifecycle.LiveData

interface RepositoryFavoritesList {
    suspend fun getFavoriteMovies() : LiveData<List<EntityDBMovie>>
    suspend fun remove(id: Long)
    suspend fun add(id: Long)
}