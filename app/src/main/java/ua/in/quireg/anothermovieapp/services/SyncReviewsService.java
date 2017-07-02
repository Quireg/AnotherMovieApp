package ua.in.quireg.anothermovieapp.services;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieReview;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.network.VolleyRequestQueueProvider;

public class SyncReviewsService extends IntentService {
    private static final String LOG_TAG = SyncReviewsService.class.getSimpleName();

    private static final String ACTION_FETCH_REVIEWS = "ua.in.quireg.anothermovieapp.services.action.FETCH_REVIEWS";
    private static final String EXTRA_PARAM_MOVIE_ID = "ua.in.quireg.anothermovieapp.services.extra.MOVIE_ID";
    private static final String EXTRA_PARAM_PAGE_NUMBER = "ua.in.quireg.anothermovieapp.services.extra.PARAM_PAGE_NUMBER";
    private static final String FIRST_PAGE = "1";


    public SyncReviewsService() {
        super("SyncReviewsService");
    }

    public static void startActionFetchReviews(Context context, String movieId) {
        Log.d(LOG_TAG, "startActionFetchReviews()");
        startActionFetchReviewsForPage(context, movieId, FIRST_PAGE);
    }

    public static void startActionFetchReviewsForPage(Context context, String movieId, String pageNumber) {
        Log.d(LOG_TAG, "startActionFetchReviewsForPage()");
        Intent intent = new Intent(context, SyncReviewsService.class);
        intent.setAction(ACTION_FETCH_REVIEWS);
        intent.putExtra(EXTRA_PARAM_MOVIE_ID, movieId);
        intent.putExtra(EXTRA_PARAM_PAGE_NUMBER, pageNumber);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent()");
        if (intent != null) {
            final String movieId = intent.getStringExtra(EXTRA_PARAM_MOVIE_ID);
            final String pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER);
            handleActionFetchReviews(movieId, pageNumber);
        }
    }

    private void handleActionFetchReviews(String movieId, String pageNumber) {
        Log.d(LOG_TAG, "handleActionFetchReviews()");

        if (movieId == null) {
            Log.e(LOG_TAG, "Movie id is null!");
            return;
        }
        Uri uri;
        if (pageNumber != null) {
            uri = UriHelper.getMovieReviewsUriForPage(movieId, pageNumber);
        } else {
            uri = UriHelper.getMovieReviewsUri(movieId);
        }

        JsonObjectRequest movieReviewRequest = new JsonObjectRequest
                (Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(LOG_TAG, "onResponse()");

                                long page = 0;
                                String totalPages = null;
                                String movieId;
                                try {
                                    Log.v(LOG_TAG, response.toString());
                                    movieId = response.getString(MovieReview.JSON_ID);
                                    page = response.getLong(MovieReview.JSON_PAGE);
                                    totalPages = response.getString(MovieReview.JSON_TOTAL_PAGES);
                                    JSONArray arr = response.getJSONArray(MovieReview.JSON_ARRAY_RESULTS);

                                    ContentValues[] contentValuesArray = new ContentValues[arr.length()];


                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject movie_review = arr.getJSONObject(i);
                                        MovieReview item = MovieReview.fromJSON(movie_review, movieId);
                                        contentValuesArray[i] = MovieReview.contentValuesFromObj(item);
                                    }

                                    getApplicationContext().getContentResolver().bulkInsert(
                                            MovieDatabaseContract.MovieReviews.CONTENT_URI,
                                            contentValuesArray
                                    );


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    Intent fetchCompletedIntent = new Intent(Constants.SYNC_REVIEWS_UPDATES_FILTER);
                                    fetchCompletedIntent.putExtra(Constants.SYNC_STATUS, Constants.SYNC_COMPLETED);
                                    fetchCompletedIntent.putExtra(Constants.TOTAL_PAGES, totalPages);
                                    fetchCompletedIntent.putExtra(Constants.LOADED_PAGE, page);
                                    LocalBroadcastManager.getInstance(SyncReviewsService.this).sendBroadcast(fetchCompletedIntent);

                                }
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent fetchFailedIntent = new Intent(Constants.SYNC_REVIEWS_UPDATES_FILTER);
                        fetchFailedIntent.putExtra(Constants.SYNC_STATUS, Constants.SYNC_FAILED);
                        LocalBroadcastManager.getInstance(SyncReviewsService.this).sendBroadcast(fetchFailedIntent);
                    }
                });

        VolleyRequestQueueProvider.getInstance(null).addToRequestQueue(movieReviewRequest);
    }
}
