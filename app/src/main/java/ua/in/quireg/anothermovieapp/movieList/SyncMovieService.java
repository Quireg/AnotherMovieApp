package ua.in.quireg.anothermovieapp.movieList;

import android.app.IntentService;
import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.*;

import static ua.in.quireg.anothermovieapp.AnotherMovieApplication.NOTIFICATION_CHANNEL_ID;

public class SyncMovieService extends IntentService {

    private static final String LOG_TAG = SyncMovieService.class.getSimpleName();
    private static final String ACTION_FETCH_NEW_MOVIES =
            "ua.in.quireg.anothermovieapp.services.action.FETCH_NEW_MOVIES";
    private static final String EXTRA_PARAM_TAG =
            "ua.in.quireg.anothermovieapp.services.extra.PARAM_TAG";
    private static final String EXTRA_PARAM_PAGE_NUMBER =
            "ua.in.quireg.anothermovieapp.services.extra.PARAM_PAGE_NUMBER";

    public SyncMovieService() {
        super("SyncMovieService");
    }

    public static void startFetchMovies(Context context, String tag, String pageNumber) {
        Intent intent = new Intent(context, SyncMovieService.class);
        intent.setAction(ACTION_FETCH_NEW_MOVIES);
        intent.putExtra(EXTRA_PARAM_TAG, tag);
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
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String tag = intent.getStringExtra(EXTRA_PARAM_TAG);
            final String pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER);
            handleActionFetchNewMovies(tag, pageNumber);

        }
    }

    private void handleActionFetchNewMovies(final String tag, final String pageNumber) {
        Log.d(LOG_TAG, "handleActionFetchNewMovies()");

        if (tag == null || pageNumber == null) {
            Log.e(LOG_TAG, "tag or page number is null");
            return;
        }
        Uri uri = UriHelper.getMoviesListPageUri(tag, pageNumber);

        JsonObjectRequest movieListRequest = new JsonObjectRequest
                (Request.Method.GET, uri.toString(), null,
                        new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                long page = 0;
                                long totalResults = 0;
                                try {
                                    Log.d(LOG_TAG, "onResponse()");
                                    Log.d(LOG_TAG, response.toString());
                                    JSONArray arr = response.getJSONArray("results");
                                    page = response.getLong("page");
                                    totalResults = response.getLong("total_results");

                                    ContentValues[] cv = new ContentValues[arr.length()];
                                    ContentValues[] cvArrayIdsOnly =
                                            new ContentValues[arr.length()];

                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject movie = arr.getJSONObject(i);
                                        MovieItem item = MovieItem.fromJSON(movie);

                                        cv[i] = MovieItem.contentValuesFromObj(item);
                                        ContentValues cv2 = new ContentValues();

                                        switch (tag) {
                                            case Constants.POPULAR:
                                                cv2.put(PopularMovies._ID, item.getId());
                                                cv2.put(PopularMovies.COLUMN_PAGE, pageNumber);
                                                cv2.put(PopularMovies.COLUMN_POSITION, i);

                                                cvArrayIdsOnly[i] = cv2;
                                                break;
                                            case Constants.TOP_RATED:
                                                cv2.put(TopRatedMovies._ID, item.getId());
                                                cv2.put(TopRatedMovies.COLUMN_PAGE, pageNumber);
                                                cv2.put(TopRatedMovies.COLUMN_POSITION, i);

                                                cvArrayIdsOnly[i] = cv2;
                                                break;
                                        }
                                    }

                                    getApplicationContext().getContentResolver().bulkInsert(
                                            MovieEntry.CONTENT_URI,
                                            cv
                                    );

                                    switch (tag) {
                                        case Constants.POPULAR:
                                            if (page == 1) {
                                                getApplicationContext().getContentResolver().delete(
                                                        PopularMovies.CONTENT_URI,
                                                        null,
                                                        null
                                                );
                                            }
                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    PopularMovies.CONTENT_URI,
                                                    cvArrayIdsOnly
                                            );
                                            break;
                                        case Constants.TOP_RATED:
                                            if (page == 1) {
                                                getApplicationContext().getContentResolver().delete(
                                                        TopRatedMovies.CONTENT_URI,
                                                        null,
                                                        null
                                                );
                                            }
                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    TopRatedMovies.CONTENT_URI,
                                                    cvArrayIdsOnly
                                            );
                                            break;
                                    }

                                } catch (JSONException e) {
                                    Log.w(LOG_TAG, e);
                                } finally {
                                    Intent i = new Intent(Constants.SYNC_MOVIE_UPDATES_FILTER);
                                    i.putExtra(Constants.SYNC_STATUS, Constants.SYNC_COMPLETED);
                                    i.putExtra(Constants.FRAGMENT_TAG, tag);
                                    i.putExtra(Constants.TOTAL_ITEMS_LOADED, totalResults);
                                    i.putExtra(Constants.LOADED_PAGE, page);
                                    LocalBroadcastManager.getInstance(SyncMovieService.this)
                                            .sendBroadcast(i);
                                }
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent fetchFailedIntent = new Intent(Constants.SYNC_MOVIE_UPDATES_FILTER);
                        fetchFailedIntent.putExtra(Constants.SYNC_STATUS, Constants.SYNC_FAILED);
                        fetchFailedIntent.putExtra(Constants.FRAGMENT_TAG, tag);
                        LocalBroadcastManager.getInstance(SyncMovieService.this)
                                .sendBroadcast(fetchFailedIntent);
                    }
                });
        Volley.newRequestQueue(this).add(movieListRequest);
    }
}
