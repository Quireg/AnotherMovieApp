/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class RepositoryMoviesImpl @Inject constructor(
    var db: Database,
    val moviesWebService: WebserviceMovieDatabase
) : RepositoryMovies {

    override suspend fun getMovies(): Flow<Resource<List<EntityDBMovie>>> {
        return flow {
            db.movieDao().get()
        }
    }

    override suspend fun getMovieTrailers(id: String): Flow<Resource<List<EntityDBMovieTrailer>>> {
        return flow {
            emit(Resource.loading(db.movieTrailerDao().get(id)))
            try {
                val responseString = moviesWebService.getMovieTrailers(id).string()
                val response = JSONObject(responseString)
                val arr = response.getJSONArray("results")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val movieTrailer = EntityDBMovieTrailer(
                        obj.getString("id"),
                        id,
                        obj.getString("iso_639_1"),
                        obj.getString("iso_3166_1"),
                        obj.getString("key"),
                        obj.getString("name"),
                        obj.getString("site"),
                        obj.getString("size"),
                        obj.getString("type")
                    )
                    db.movieTrailerDao().insert(movieTrailer)
                }
                emit(Resource.success(db.movieTrailerDao().get(id)))
            } catch (ex: Throwable) {
                emit(Resource.error(ex, db.movieTrailerDao().get(id)))
            }
        }
    }

    override suspend fun getMovieReviews(id: String): Flow<Resource<ModelReviewsListRespond>> {
        return flow {
            val fetchState = db.movieReviewDao().getFetchState(id.toLong())
            if (fetchState != null) {
                emit(
                    Resource.loading(
                        ModelReviewsListRespond(
                            fetchState.currentPage,
                            fetchState.totalPages,
                            db.movieReviewDao().get(id.toLong())
                        )
                    )
                )
            } else {
                try {
                    val response =
                        JSONObject(moviesWebService.getMovieReviewsForPage(id, 1).string())
                    val movieId = response.getString(EntityDBMovieReview.JSON_ID)
                    val page = response.getLong(EntityDBMovieReview.JSON_PAGE)
                    val totalPages = response.getString(EntityDBMovieReview.JSON_TOTAL_PAGES)
                    val arr =
                        response.getJSONArray(EntityDBMovieReview.JSON_ARRAY_RESULTS)
                    for (i in 0 until arr.length()) {
                        val movie_review = arr.getJSONObject(i)
                        val item: EntityDBMovieReview =
                            EntityDBMovieReview.fromJSON(movie_review, movieId)
                        db.movieReviewDao().insert(item)
                    }
                    db.movieReviewDao().setFetchState(
                        EntityDBReviewsListFetchState(id.toLong(), page, totalPages.toLong())
                    )
                    db.movieReviewDao().getFetchState(id.toLong())?.let {
                        emit(
                            Resource.success(
                                ModelReviewsListRespond(
                                    it.currentPage,
                                    it.totalPages,
                                    db.movieReviewDao().get(id.toLong())
                                )
                            )
                        )
                    }
                } catch (e: Throwable) {
                    emit(
                        Resource.error(
                            e, ModelReviewsListRespond(
                                0, 0, null
                            )
                        )
                    )
                }
            }
        }
    }

    override suspend fun fetchMoreReviews() {
        TODO("Not yet implemented")
    }
}