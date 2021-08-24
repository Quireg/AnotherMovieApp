/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RepositoryFavoritesListImpl @Inject constructor(
    var db: Database,
) : RepositoryFavoritesList {

    override suspend fun getFavoriteMovies(): Flow<List<EntityDBMovie>> {
        return flow {
            db.movieFavoriteDao().get().collect {
                emit(it.map { fav -> db.movieDao().get().findLast { it.id == fav.id.toLong() }!! })
            }
        }
    }

    override suspend fun remove(id: Long) {
        db.movieFavoriteDao().remove(EntityDBFavoriteMovie(id.toString()))
    }

    override suspend fun add(id: Long) {
        db.movieFavoriteDao().insert(EntityDBFavoriteMovie(id.toString()))
    }

    override suspend fun addOrRemove(id: Long) {

        val favMovie = db.movieFavoriteDao().get().first().find { it.id.toLong() == id }
        if (favMovie == null) {
            db.movieFavoriteDao().insert(EntityDBFavoriteMovie(id.toString()))
        } else {
            db.movieFavoriteDao().remove(favMovie)
        }

    }

    override suspend fun isFavourite(id: Long): Flow<Boolean> {
        return flow {
            db.movieFavoriteDao().get().collect {
                emit(it.find { it.id.toLong() == id } != null)
            }
        }
    }
}