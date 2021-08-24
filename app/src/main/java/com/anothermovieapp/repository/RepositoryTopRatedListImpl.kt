/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class RepositoryTopRatedListImpl @Inject constructor(
    var db: Database,
    val moviesWebService: WebserviceMovieDatabase,
) : RepositoryTopRatedList {

    val callbackChannel = Channel<Resource<ModelTopRatedListRespond>>()
    val totalResultsChannel = Channel<Long>()

    private var loadJob: Job? = null
    private var isFetching = false

    override suspend fun getTopRatedMovies(): Flow<Resource<ModelTopRatedListRespond>> {
        return flow {
            while (true) {
                emit(callbackChannel.receive())
            }
        }
    }

    override suspend fun getTotalNumber(): Flow<Long> {
        return flow {
            while (true) {
                emit(totalResultsChannel.receive())
            }
        }
    }

    override suspend fun fetchMoreTopRated() {
        performLoad(false)
    }

    override suspend fun reloadTopRated() {
        performLoad(true)
    }

    private suspend fun performLoad(reset: Boolean) {
        if (reset) {
            loadJob?.cancel()
            isFetching = false
            db.movieTopRatedDao().clear()
            db.movieTopRatedDao().setFetchState(EntityDBTopRatedListFetchState("1", 0, 0))
        }

        if (isFetching) {
            return
        }
        isFetching = true

        coroutineScope {
            loadJob = launch(Dispatchers.Default) {
                val fetchState = db.movieTopRatedDao().getFetchState()
                var page = 1
                fetchState?.let { page = it.currentPage.toInt() + 1 }
                Timber.d("Request TopRated load, page %d", page)

                networkBoundResource(
                    query = {
                        flow {
                            val listIds = db.movieTopRatedDao().get()
                            val list = listIds.map { e ->
                                db.movieDao().get().first { it.id == e.id.toLong() }
                            }
                            val fetchState = db.movieTopRatedDao().getFetchState()
                            if (fetchState == null) {
                                emit(ModelTopRatedListRespond(1, list.size.toLong(), list));
                            } else {
                                emit(
                                    ModelTopRatedListRespond(
                                        fetchState.currentPage, fetchState.totalPages,
                                        if (fetchState.currentPage != 0) list else null
                                    )
                                )
                            }
                        }
                    },
                    fetch = {
                        var pageToFetch = 1
                        if (!reset) {
                            db.movieTopRatedDao().getFetchState()?.let {
                                pageToFetch = it.currentPage.toInt() + 1
                            }
                        }
                        moviesWebService.getMoviesListTopRated(pageToFetch)
                    },
                    saveFetchResult = {
                        val response = JSONObject(it.string())
                        val arr = response.getJSONArray("results")
                        val page_response = response.getLong("page")
                        val totalResults = response.getLong("total_results")
                        val list = ArrayList<EntityDBTopRatedList>()
                        for (i in 0 until arr.length()) {
                            val movie = arr.getJSONObject(i)
                            val item: EntityDBMovie = EntityDBMovie.fromJSON(movie)
                            list.add(EntityDBTopRatedList(item.id.toString()))
                            db.movieDao().insert(item)
                        }
                        val listTo = mutableListOf<EntityDBTopRatedList>()
                        listTo.addAll(db.movieTopRatedDao().get())
                        listTo.addAll(list)
                        db.movieTopRatedDao().insert(listTo)
                        db.movieTopRatedDao().setFetchState(
                            EntityDBTopRatedListFetchState("1", page_response, totalResults)
                        )
                        Timber.d("Request load complete, page %d", page_response)
                    },
                    onFetchFailed = {
                        isFetching = false
                        Timber.d("Request failed: %s", it.localizedMessage)
                    }
                ).collect { value ->
                    callbackChannel.offer(value)
                    value.data?.totalPages?.let { totalResultsChannel.offer(it) }
                    isFetching = false
                }
            }
        }
    }

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