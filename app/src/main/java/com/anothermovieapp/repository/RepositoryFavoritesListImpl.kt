package com.anothermovieapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class RepositoryFavoritesListImpl @Inject constructor(
    var db: Database,
) : RepositoryFavoritesList {

    override suspend fun getFavoriteMovies(): LiveData<List<EntityDBMovie>> {
        val liveData = MutableLiveData<List<EntityDBMovie>>()
        val list = db.movieFavoriteDao().get().map { db.movieDao().get()[it.id.toInt()] }
        liveData.postValue(list)
        return liveData
    }

    override suspend fun remove(id: Long) {
        db.movieFavoriteDao().remove(EntityDBFavoriteList(id.toString()))
    }

    override suspend fun add(id: Long) {
        db.movieFavoriteDao().insert(EntityDBFavoriteList(id.toString()))
    }
}