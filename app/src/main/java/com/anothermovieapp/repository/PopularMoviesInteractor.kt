package com.anothermovieapp.repository

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.anothermovieapp.common.Constants
import com.anothermovieapp.common.UriHelper
import org.json.JSONException


class PopularMoviesInteractor(private val context: Context, private val database: MoviesDatabase) {
    private val LOG_TAG = PopularMoviesInteractor::class.java.simpleName


    fun fetchPopular(pageNumber: String?, callback: Callback) {
        Log.d(LOG_TAG, "handleActionFetchNewMovies()")
        if (pageNumber == null) {
            Log.e(LOG_TAG, "tag or page number is null")
            return
        }
        val uri = UriHelper.getMoviesListPageUri(Constants.POPULAR, pageNumber)
        val movieListRequest = JsonObjectRequest(Request.Method.GET, uri.toString(), null,
                Response.Listener { response ->
                    Thread(Runnable {
                        var page: Long = 0
                        var totalResults: Long = 0
                        try {
                            Log.d(LOG_TAG, "onResponse()")
                            Log.d(LOG_TAG, response.toString())
                            val arr = response.getJSONArray("results")
                            page = response.getLong("page")
                            totalResults = response.getLong("total_results")
                            val list = ArrayList<Long>()
                            for (i in 0 until arr.length()) {
                                val movie = arr.getJSONObject(i)
                                val item: Movie = Movie.fromJSON(movie)
                                list.add(item.id)
                                database.movieDao().insertMovie(item)
                            }
                            callback.onComplete(list)
                        } catch (e: JSONException) {
                            Log.w(LOG_TAG, e)
                        }
                    }).start()
                }, Response.ErrorListener {
        })
        Volley.newRequestQueue(context).add(movieListRequest)
    }

    interface Callback {
        fun onComplete(result: List<Long>)
    }
}