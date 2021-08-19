package com.anothermovieapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.ArrayList
import javax.inject.Inject

class RepositoryMoviesImpl @Inject constructor(
    var db: Database,
    val moviesWebService: WebserviceMovieDatabase
) : RepositoryMovies {

    val callbackMoviesChannel = Channel<Resource<List<EntityDBMovie>>>()
    val callbackTrailersChannel = Channel<Resource<List<EntityDBMovieTrailer>>>()
    val callbackReviewsChannel = Channel<Resource<ModelReviewsListRespond>>()


    override suspend fun getMovies(): Flow<Resource<List<EntityDBMovie>>> {
        return flow {
            db.movieDao().get()
        }
    }

    override suspend fun getMovieTrailers(id: String): Flow<Resource<List<EntityDBMovieTrailer>>> {
        return flow {
            emit(Resource.loading(db.movieTrailerDao().get()))
            try {
                val responseString = moviesWebService.getMovieTrailers(id).string()
                val response = JSONObject(responseString)
                val arr = response.getJSONArray("results")
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val movieTrailer = EntityDBMovieTrailer(
                        obj.getString("id"),
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
                emit(Resource.success(db.movieTrailerDao().get()))
            } catch (ex: Throwable) {
                emit(Resource.error(ex, db.movieTrailerDao().get()))
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
                        JSONObject(moviesWebService.getMovieReviewsForPage(id, 1).toString())
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
                                    it.currentPage, it.totalPages, db.movieReviewDao().get(id.toLong())
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

//    val movies: Flow<List<Movie>> = db.movieDao().getMovies()
//    val trailers: Flow<List<MovieTrailer>> = db.movieTrailerDao().getMovieTrailers()
//    val reviews: Flow<List<MovieReview>> = db.movieReviewDao().getMovieReviews()
//
//    override suspend fun getMovies(): Flow<List<EntityDBMovie>> {
//        val r: Retrofit = Retrofit.Builder()
//                .baseUrl("https://api.themoviedb.org")
//                .addConverterFactory(object : Converter.Factory() {
//                    override fun responseBodyConverter(type: Type,
//                                                       annotations: Array<out Annotation>,
//                                                       retrofit: Retrofit):
//                            Converter<ResponseBody, *> {
//                        return  Converter<ResponseBody, List<EntityDBMovie>> {
//
//                            val jo = JSONObject(it.string())
//                            val arr = jo.getJSONArray("results")
//                            val page = jo.getLong("page")
//                            val totalResults = jo.getLong("total_results")
//                            val list = mutableListOf<EntityDBMovie>()
//                            for (i in 0 until arr.length()) {
//                                val movie = arr.getJSONObject(i)
//                                val item: EntityDBMovie = EntityDBMovie.fromJSON(movie)
//                                list.add(item)
//                            }
//                            list
//                        }
//                    }
//                })
//                .build();
//        val s: IMovieDatabaseWebservice = r.create(IMovieDatabaseWebservice::class.java)
//
//        moviesWebService.getMovie("ads")
//        return db.movieDao().get()
//    }
//
//    override suspend fun getMovieTrailers(): Flow<List<EntityDBMovieTrailer>> {
//        return db.movieTrailerDao().get()
//    }
//
//    override suspend fun getMovieReviews(): Flow<List<EntityDBMovieReview>> {
//    }
//

    inline fun <ResultType, RequestType> networkBoundResource(
        crossinline query: () -> Flow<ResultType>,
        crossinline fetch: suspend () -> RequestType,
        crossinline saveFetchResult: suspend (RequestType) -> Unit,
        crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
        crossinline shouldFetch: (ResultType) -> Boolean = { true }
    ) = flow<Resource<ResultType>> {
        val data = query().first()
        emit(Resource.loading(data))

        val flow = if (shouldFetch(data)) {
            try {
                saveFetchResult(fetch())
                query().map { Resource.success(it) }
            } catch (throwable: Throwable) {
                onFetchFailed(throwable)
                query().map { Resource.error(throwable, it) }
            }
        } else {
            query().map { Resource.success(it) }
        }
        emitAll(flow)
    }

}