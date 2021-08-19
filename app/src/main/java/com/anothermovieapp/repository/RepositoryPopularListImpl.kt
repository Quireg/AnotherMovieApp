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


class RepositoryPopularListImpl @Inject constructor(
    var db: Database,
    val moviesWebService: WebserviceMovieDatabase
) : RepositoryPopularList {

    val callbackChannel = Channel<Resource<ModelPopularListRespond>>()
    val totalResultsChannel = Channel<Long>()

    private var loadJob: Job? = null
    private var isFetching = false

    override suspend fun getPopularMovies(): Flow<Resource<ModelPopularListRespond>> {
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

    override suspend fun fetchMorePopular() {
        performLoad(false)
    }

    override suspend fun reloadPopular() {
        performLoad(true)
    }

    private suspend fun performLoad(reset: Boolean) {
        if (reset) {
            loadJob?.cancel()
            isFetching = false
            db.moviePopularDao().clear()
            db.moviePopularDao().setFetchState(EntityDBPopularListFetchState("1", 0, 0))
        }

        if (isFetching) {
            return
        }
        isFetching = true

        coroutineScope {
            loadJob = launch(Dispatchers.Default) {
                val fetchState = db.moviePopularDao().getFetchState()
                var page = 1
                fetchState?.let { page = it.currentPage.toInt() + 1 }
                Timber.d("Request popular load, page %d", page)

                networkBoundResource(
                    query = {
                        flow {
                            val listIds = db.moviePopularDao().get()
                            val list = listIds.map { e ->
                                db.movieDao().get().first { it.id == e.id.toLong() }
                            }
                            val fetchState = db.moviePopularDao().getFetchState()
                            if (fetchState == null) {
                                emit(ModelPopularListRespond(1, 0, null));
                            } else {
                                emit(
                                    ModelPopularListRespond(
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
                            db.moviePopularDao().getFetchState()?.let {
                                pageToFetch = it.currentPage.toInt() + 1
                            }
                        }
                        if (pageToFetch >= 5) {
                            pageToFetch  = 5
                        }
                        moviesWebService.getMoviesListPopular(pageToFetch)
                    },
                    saveFetchResult = {
                        val response = JSONObject(it.string())
                        val arr = response.getJSONArray("results")
                        val page_response = response.getLong("page")
                        val totalResults = response.getLong("total_results")
                        val list = ArrayList<EntityDBPopularList>()
                        for (i in 0 until arr.length()) {
                            val movie = arr.getJSONObject(i)
                            val item: EntityDBMovie = EntityDBMovie.fromJSON(movie)
                            list.add(EntityDBPopularList(item.id.toString()))
                            db.movieDao().insert(item)
                        }
                        val listTo = mutableListOf<EntityDBPopularList>()
                        listTo.addAll(db.moviePopularDao().get())
                        listTo.addAll(list)
                        db.moviePopularDao().insert(listTo)
                        db.moviePopularDao().setFetchState(
                            EntityDBPopularListFetchState("1", page_response, totalResults)
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