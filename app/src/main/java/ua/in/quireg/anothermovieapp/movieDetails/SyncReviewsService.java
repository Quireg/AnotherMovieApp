package ua.in.quireg.anothermovieapp.movieDetails;

import android.app.IntentService;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.in.quireg.anothermovieapp.common.MovieReview;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.movieList.SyncMovieService;

import static ua.in.quireg.anothermovieapp.AnotherMovieApplication.NOTIFICATION_CHANNEL_ID;
import static ua.in.quireg.anothermovieapp.common.Constants.FRAGMENT_TAG;
import static ua.in.quireg.anothermovieapp.common.Constants.LOADED_PAGE;
import static ua.in.quireg.anothermovieapp.common.Constants.REVIEWS;
import static ua.in.quireg.anothermovieapp.common.Constants.SYNC_COMPLETED;
import static ua.in.quireg.anothermovieapp.common.Constants.SYNC_FAILED;
import static ua.in.quireg.anothermovieapp.common.Constants.SYNC_REVIEWS_UPDATES_FILTER;
import static ua.in.quireg.anothermovieapp.common.Constants.SYNC_STATUS;
import static ua.in.quireg.anothermovieapp.common.Constants.TOTAL_PAGES;

public class SyncReviewsService extends IntentService {

    private static final String TAG = SyncReviewsService.class.getSimpleName();
    private static final String LOG_TAG = SyncMovieService.class.getSimpleName();

    private static final String ACTION_FETCH_REVIEWS =
            "ua.in.quireg.anothermovieapp.services.action.FETCH_REVIEWS";
    private static final String EXTRA_PARAM_MOVIE_ID =
            "ua.in.quireg.anothermovieapp.services.extra.MOVIE_ID";
    private static final String EXTRA_PARAM_PAGE_NUMBER =
            "ua.in.quireg.anothermovieapp.services.extra.PARAM_PAGE_NUMBER";
    private static final String FIRST_PAGE = "1";

    public SyncReviewsService() {
        super(TAG);
    }

    public static void startActionFetchReviews(Context context, String movieId) {
        Log.d(TAG, "startActionFetchReviews()");
        startActionFetchReviewsForPage(context, movieId, FIRST_PAGE);
    }

    public static void startActionFetchReviewsForPage(Context context, String movieId,
                                                      String pageNumber) {
        Log.d(TAG, "startActionFetchReviewsForPage()");
        Intent intent = new Intent(context, SyncReviewsService.class);
        intent.setAction(ACTION_FETCH_REVIEWS);
        intent.putExtra(EXTRA_PARAM_MOVIE_ID, movieId);
        intent.putExtra(EXTRA_PARAM_PAGE_NUMBER, pageNumber);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification n = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID).build();
            startForeground(1, n);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent()");
        if (intent != null) {
            final String movieId = intent.getStringExtra(EXTRA_PARAM_MOVIE_ID);
            final String pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER);
            handleActionFetchReviews(movieId, pageNumber);
        }
    }

    private void handleActionFetchReviews(String movieId, String pageNumber) {
        Log.d(TAG, "handleActionFetchReviews()");

        if (movieId == null) {
            Log.e(TAG, "Movie id is null!");
            return;
        }
        Uri uri;
        if (pageNumber != null) {
            uri = UriHelper.getMovieReviewsUriForPage(movieId, pageNumber);
        } else {
            uri = UriHelper.getMovieReviewsUri(movieId);
        }

        JsonObjectRequest movieReviewRequest = new JsonObjectRequest
                (Request.Method.GET, uri.toString(), null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(final JSONObject response) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "onResponse()");
                                        long page = 0;
                                        String totalPages = null;
                                        String movieId;
                                        try {
                                            Log.v(TAG, response.toString());
                                            movieId = response.getString(MovieReview.JSON_ID);
                                            page = response.getLong(MovieReview.JSON_PAGE);
                                            totalPages = response.getString(MovieReview.JSON_TOTAL_PAGES);
                                            JSONArray arr =
                                                    response.getJSONArray(MovieReview.JSON_ARRAY_RESULTS);

                                            ContentValues[] contentValuesArray =
                                                    new ContentValues[arr.length()];

                                            for (int i = 0; i < arr.length(); i++) {
                                                JSONObject movie_review = arr.getJSONObject(i);
                                                MovieReview item =
                                                        MovieReview.fromJSON(movie_review, movieId);
                                                contentValuesArray[i] =
                                                        MovieReview.contentValuesFromObj(item);
                                            }

                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    MovieDatabaseContract.MovieReviews.CONTENT_URI,
                                                    contentValuesArray
                                            );

                                        } catch (JSONException e) {
                                            Log.w(TAG, e.getMessage());
                                        } finally {
                                            Intent i = new Intent(SYNC_REVIEWS_UPDATES_FILTER);
                                            i.putExtra(SYNC_STATUS, SYNC_COMPLETED);
                                            i.putExtra(FRAGMENT_TAG, REVIEWS);
                                            if (totalPages != null) {
                                                i.putExtra(TOTAL_PAGES, Long.valueOf(totalPages));
                                            }
                                            i.putExtra(LOADED_PAGE, page);
                                            LocalBroadcastManager.getInstance(SyncReviewsService.this)
                                                    .sendBroadcast(i);

                                        }
                                    }
                                }).start();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(LOG_TAG, error);
                        Intent fetchFailedIntent = new Intent(SYNC_REVIEWS_UPDATES_FILTER);
                        fetchFailedIntent.putExtra(SYNC_STATUS, SYNC_FAILED);
                        fetchFailedIntent.putExtra(FRAGMENT_TAG, REVIEWS);

                        LocalBroadcastManager.getInstance(SyncReviewsService.this)
                                .sendBroadcast(fetchFailedIntent);
                    }
                });
        Log.d(TAG, "Fetching: " + uri.toString());
        Volley.newRequestQueue(this).add(movieReviewRequest);
    }
}
