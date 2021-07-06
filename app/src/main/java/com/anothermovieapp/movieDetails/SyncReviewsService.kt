package com.anothermovieapp.movieDetails

import android.app.IntentService
import android.app.Notification
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.anothermovieapp.AnotherMovieApplication
import com.anothermovieapp.repository.MovieReview
import com.anothermovieapp.common.UriHelper
import com.anothermovieapp.managers.MovieDatabaseContract.MovieReviews
import com.anothermovieapp.movieList.SyncMovieService
import org.json.JSONException

class SyncReviewsService : IntentService(TAG) {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val n = Notification.Builder(this, AnotherMovieApplication.Companion.NOTIFICATION_CHANNEL_ID).build()
            startForeground(1, n)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent()")
        if (intent != null) {
            val movieId = intent.getStringExtra(EXTRA_PARAM_MOVIE_ID)
            val pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER)
            handleActionFetchReviews(movieId, pageNumber)
        }
    }

    private fun handleActionFetchReviews(movieId: String?, pageNumber: String?) {
        Log.d(TAG, "handleActionFetchReviews()")
        if (movieId == null) {
            Log.e(TAG, "Movie id is null!")
            return
        }
        val uri: Uri?
        uri = if (pageNumber != null) {
            UriHelper.getMovieReviewsUriForPage(movieId, pageNumber)
        } else {
            UriHelper.getMovieReviewsUri(movieId)
        }
        val movieReviewRequest = JsonObjectRequest(Request.Method.GET, uri.toString(), null,
                Response.Listener { response ->
                    Thread(Runnable {
                        Log.d(TAG, "onResponse()")
                        var page: Long = 0
                        var totalPages: String? = null
                        val movieId: String
                        try {
                            Log.v(TAG, response.toString())
                            movieId = response.getString(MovieReview.Companion.JSON_ID)
                            page = response.getLong(MovieReview.Companion.JSON_PAGE)
                            totalPages = response.getString(MovieReview.Companion.JSON_TOTAL_PAGES)
                            val arr = response.getJSONArray(MovieReview.Companion.JSON_ARRAY_RESULTS)
                            val contentValuesArray = arrayOfNulls<ContentValues>(arr.length())
                            for (i in 0 until arr.length()) {
                                val movie_review = arr.getJSONObject(i)
                                val item: MovieReview = MovieReview.Companion.fromJSON(movie_review, movieId)
                                contentValuesArray[i] = MovieReview.Companion.contentValuesFromObj(item)
                            }
                            applicationContext.contentResolver.bulkInsert(
                                    MovieReviews.CONTENT_URI,
                                    contentValuesArray
                            )
                        } catch (e: JSONException) {
                            Log.w(TAG, e.message)
                        } finally {
                            val i = Intent(SYNC_REVIEWS_UPDATES_FILTER)
                            i.putExtra(SYNC_STATUS, SYNC_COMPLETED)
                            i.putExtra(FRAGMENT_TAG, REVIEWS)
                            if (totalPages != null) {
                                i.putExtra(TOTAL_PAGES, java.lang.Long.valueOf(totalPages))
                            }
                            i.putExtra(LOADED_PAGE, page)
                            LocalBroadcastManager.getInstance(this@SyncReviewsService)
                                    .sendBroadcast(i)
                        }
                    }).start()
                }, Response.ErrorListener { error ->
            Log.w(LOG_TAG, error)
            val fetchFailedIntent = Intent(SYNC_REVIEWS_UPDATES_FILTER)
            fetchFailedIntent.putExtra(SYNC_STATUS, SYNC_FAILED)
            fetchFailedIntent.putExtra(FRAGMENT_TAG, REVIEWS)
            LocalBroadcastManager.getInstance(this@SyncReviewsService)
                    .sendBroadcast(fetchFailedIntent)
        })
        Log.d(TAG, "Fetching: $uri")
        Volley.newRequestQueue(this).add(movieReviewRequest)
    }

    companion object {
        private val TAG = SyncReviewsService::class.java.simpleName
        private val LOG_TAG = SyncMovieService::class.java.simpleName
        private const val ACTION_FETCH_REVIEWS = "com.anothermovieapp.services.action.FETCH_REVIEWS"
        private const val EXTRA_PARAM_MOVIE_ID = "com.anothermovieapp.services.extra.MOVIE_ID"
        private const val EXTRA_PARAM_PAGE_NUMBER = "com.anothermovieapp.services.extra.PARAM_PAGE_NUMBER"
        private const val FIRST_PAGE = "1"
        fun startActionFetchReviews(context: Context?, movieId: String?) {
            Log.d(TAG, "startActionFetchReviews()")
            startActionFetchReviewsForPage(context, movieId, FIRST_PAGE)
        }

        fun startActionFetchReviewsForPage(context: Context?, movieId: String?,
                                           pageNumber: String?) {
            Log.d(TAG, "startActionFetchReviewsForPage()")
            val intent = Intent(context, SyncReviewsService::class.java)
            intent.action = ACTION_FETCH_REVIEWS
            intent.putExtra(EXTRA_PARAM_MOVIE_ID, movieId)
            intent.putExtra(EXTRA_PARAM_PAGE_NUMBER, pageNumber)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context!!.startForegroundService(intent)
            } else {
                context!!.startService(intent)
            }
        }
    }
}