package com.anothermovieapp.managers

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.anothermovieapp.repository.Movie
import com.anothermovieapp.repository.MovieTrailer
import com.anothermovieapp.common.UriHelper
import com.anothermovieapp.movieDetails.FetchTrailersCallback
import org.json.JSONException
import java.util.*

object MovieTrailersProvider {
    private val LOG_TAG = MovieTrailersProvider::class.java.simpleName
    fun fetchTrailersList(movie: Movie?, c: Context?, callback: FetchTrailersCallback) {
        val uri = UriHelper.getMovieTrailerUriById(movie?.id.toString())
        val movieTrailersRequest = JsonObjectRequest(
                Request.Method.GET, uri.toString(), null, Response.Listener { response ->
            try {
                val arr = response.getJSONArray("results")
                val trailers: MutableList<MovieTrailer> = ArrayList()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val movieTrailer = MovieTrailer(
                            obj.getString("id"),
                            obj.getString("iso_639_1"),
                            obj.getString("iso_3166_1"),
                            obj.getString("key"),
                            obj.getString("name"),
                            obj.getString("site"),
                            obj.getString("size"),
                            obj.getString("type")
                    )
                    trailers.add(movieTrailer)
                }
                callback.onTrailersFetchCompleted(trailers)
            } catch (e: JSONException) {
                Log.w(LOG_TAG, e.message)
            }
        }, Response.ErrorListener { Log.d(LOG_TAG, "Failed: $uri") })
        Log.d(LOG_TAG, "Fetching: $uri")
        Volley.newRequestQueue(c).add(movieTrailersRequest)
    }
}