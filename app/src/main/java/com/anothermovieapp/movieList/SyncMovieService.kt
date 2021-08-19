//package com.anothermovieapp.movieList
//
//import android.app.IntentService
//import android.app.Notification
//import android.content.ContentValues
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.util.Log
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.android.volley.Request
//import com.android.volley.Response
//import com.android.volley.toolbox.JsonObjectRequest
//import com.android.volley.toolbox.Volley
//import com.anothermovieapp.AnotherMovieApplication
//import com.anothermovieapp.common.Constants
//import com.anothermovieapp.repository.EntityDBMovie
//import com.anothermovieapp.common.UriHelper
//import org.json.JSONException
//
//class SyncMovieService : IntentService("SyncMovieService") {
//    override fun onCreate() {
//        super.onCreate()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val n = Notification.Builder(this, AnotherMovieApplication.Companion.NOTIFICATION_CHANNEL_ID).build()
//            startForeground(1, n)
//        }
//    }
//
//    override fun onHandleIntent(intent: Intent) {
//        if (intent != null) {
//            val tag = intent.getStringExtra(EXTRA_PARAM_TAG)
//            val pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER)
//            handleActionFetchNewMovies(tag, pageNumber)
//        }
//    }
//
//    private fun handleActionFetchNewMovies(tag: String?, pageNumber: String?) {
//        Log.d(LOG_TAG, "handleActionFetchNewMovies()")
//        if (tag == null || pageNumber == null) {
//            Log.e(LOG_TAG, "tag or page number is null")
//            return
//        }
//        val uri = UriHelper.getMoviesListPageUri(tag, pageNumber)
//        val movieListRequest = JsonObjectRequest(Request.Method.GET, uri.toString(), null,
//                Response.Listener { response ->
//                    Thread(Runnable {
//                        var page: Long = 0
//                        var totalResults: Long = 0
//                        try {
//                            Log.d(LOG_TAG, "onResponse()")
//                            Log.d(LOG_TAG, response.toString())
//                            val arr = response.getJSONArray("results")
//                            page = response.getLong("page")
//                            totalResults = response.getLong("total_results")
//                            val cv = arrayOfNulls<ContentValues>(arr.length())
//                            val cvArrayIdsOnly = arrayOfNulls<ContentValues>(arr.length())
//                            for (i in 0 until arr.length()) {
//                                val movie = arr.getJSONObject(i)
//                                val item: EntityDBMovie = EntityDBMovie.fromJSON(movie)
//
//
//
//                                cv[i] = EntityDBMovie.Companion.contentValuesFromObj(item)
//                                val cv2 = ContentValues()
//                                when (tag) {
//                                    Constants.POPULAR -> {
//                                        cv2.put(PopularMovies._ID, item.id)
//                                        cv2.put(PopularMovies.COLUMN_PAGE, pageNumber)
//                                        cv2.put(PopularMovies.COLUMN_POSITION, i)
//                                        cvArrayIdsOnly[i] = cv2
//                                    }
//                                    Constants.TOP_RATED -> {
//                                        cv2.put(TopRatedMovies._ID, item.id)
//                                        cv2.put(TopRatedMovies.COLUMN_PAGE, pageNumber)
//                                        cv2.put(TopRatedMovies.COLUMN_POSITION, i)
//                                        cvArrayIdsOnly[i] = cv2
//                                    }
//                                }
//                            }
//                            applicationContext.contentResolver.bulkInsert(
//                                    MovieEntry.CONTENT_URI,
//                                    cv
//                            )
//                            when (tag) {
//                                Constants.POPULAR -> {
//                                    if (page == 1L) {
//                                        applicationContext.contentResolver.delete(
//                                                PopularMovies.CONTENT_URI,
//                                                null,
//                                                null
//                                        )
//                                    }
//                                    applicationContext.contentResolver.bulkInsert(
//                                            PopularMovies.CONTENT_URI,
//                                            cvArrayIdsOnly
//                                    )
//                                }
//                                Constants.TOP_RATED -> {
//                                    if (page == 1L) {
//                                        applicationContext.contentResolver.delete(
//                                                TopRatedMovies.CONTENT_URI,
//                                                null,
//                                                null
//                                        )
//                                    }
//                                    applicationContext.contentResolver.bulkInsert(
//                                            TopRatedMovies.CONTENT_URI,
//                                            cvArrayIdsOnly
//                                    )
//                                }
//                            }
//                        } catch (e: JSONException) {
//                            Log.w(LOG_TAG, e)
//                        } finally {
//                            val i = Intent(Constants.SYNC_MOVIE_UPDATES_FILTER)
//                            i.putExtra(Constants.SYNC_STATUS, Constants.SYNC_COMPLETED)
//                            i.putExtra(Constants.FRAGMENT_TAG, tag)
//                            i.putExtra(Constants.TOTAL_ITEMS_LOADED, totalResults)
//                            i.putExtra(Constants.LOADED_PAGE, page)
//                            LocalBroadcastManager.getInstance(this@SyncMovieService)
//                                    .sendBroadcast(i)
//                        }
//                    }).start()
//                }, Response.ErrorListener {
//            val fetchFailedIntent = Intent(Constants.SYNC_MOVIE_UPDATES_FILTER)
//            fetchFailedIntent.putExtra(Constants.SYNC_STATUS, Constants.SYNC_FAILED)
//            fetchFailedIntent.putExtra(Constants.FRAGMENT_TAG, tag)
//            LocalBroadcastManager.getInstance(this@SyncMovieService)
//                    .sendBroadcast(fetchFailedIntent)
//        })
//        Volley.newRequestQueue(this).add(movieListRequest)
//    }
//
//    companion object {
//        private val LOG_TAG = SyncMovieService::class.java.simpleName
//        private const val ACTION_FETCH_NEW_MOVIES = "com.anothermovieapp.services.action.FETCH_NEW_MOVIES"
//        private const val EXTRA_PARAM_TAG = "com.anothermovieapp.services.extra.PARAM_TAG"
//        private const val EXTRA_PARAM_PAGE_NUMBER = "com.anothermovieapp.services.extra.PARAM_PAGE_NUMBER"
//        fun startFetchMovies(context: Context?, tag: String?, pageNumber: String?) {
//            val intent = Intent(context, SyncMovieService::class.java)
//            intent.action = ACTION_FETCH_NEW_MOVIES
//            intent.putExtra(EXTRA_PARAM_TAG, tag)
//            intent.putExtra(EXTRA_PARAM_PAGE_NUMBER, pageNumber)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context!!.startForegroundService(intent)
//            } else {
//                context!!.startService(intent)
//            }
//        }
//    }
//}