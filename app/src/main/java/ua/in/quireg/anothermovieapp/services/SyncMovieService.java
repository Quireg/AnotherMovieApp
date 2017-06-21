package ua.in.quireg.anothermovieapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.FetchItemsCallback;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.network.VolleyRequestQueueProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SyncMovieService extends IntentService {

    private static final String LOG_TAG = SyncMovieService.class.getSimpleName();

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FETCH_NEW_MOVIES = "ua.in.quireg.anothermovieapp.services.action.FETCH_NEW_MOVIES";
    private static final String ACTION_BAZ = "ua.in.quireg.anothermovieapp.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM_TAG = "ua.in.quireg.anothermovieapp.services.extra.PARAM_TAG";
    private static final String EXTRA_PARAM_PAGE_NUMBER = "ua.in.quireg.anothermovieapp.services.extra.PARAM_PAGE_NUMBER";

    private static FetchItemsCallback fetchMoreItemsCallback = null;

    public SyncMovieService() {
        super("SyncMovieService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method

    public static void startActionFetchMovies(Context context, String tag, String pageNumber) {
        Intent intent = new Intent(context, SyncMovieService.class);
        intent.setAction(ACTION_FETCH_NEW_MOVIES);
        intent.putExtra(EXTRA_PARAM_TAG, tag);
        intent.putExtra(EXTRA_PARAM_PAGE_NUMBER, pageNumber);
        context.startService(intent);
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
//    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, SyncMovieService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_NEW_MOVIES.equals(action)) {
                final String tag = intent.getStringExtra(EXTRA_PARAM_TAG);
                final String pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER);
                handleActionFetchNewMovies(tag, pageNumber);
            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchNewMovies(final String tag, final String pageNumber) {


        Uri uri = UriHelper.getMoviesListPageUri(tag, pageNumber);

        JsonObjectRequest movieListRequest = new JsonObjectRequest
                (Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                long page = 0;
                                long totalPages = 0;
                                long totalResults = 0;
                                try {
                                    Log.d(LOG_TAG, "onResponse()");
                                    Log.d(LOG_TAG, response.toString());
                                    JSONArray arr = response.getJSONArray("results");
                                    page = response.getLong("page");
                                    totalResults = response.getLong("total_results");
                                    totalPages = response.getLong("total_pages");

                                    ContentValues[] contentValuesArray = new ContentValues[arr.length()];
                                    ContentValues[] contentValuesArrayIdsOnly = new ContentValues[arr.length()];


                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject movie = arr.getJSONObject(i);
                                        MovieItem item = MovieItem.fromJSON(movie);

                                        contentValuesArray[i] = MovieItem.contentValuesFromObj(item);
                                        ContentValues contentValues = new ContentValues();

                                        switch (tag) {
                                            case Constants.POPULAR:
                                                contentValues.put(MovieDatabaseContract.PopularMovies._ID, item.getId());
                                                contentValues.put(MovieDatabaseContract.PopularMovies.COLUMN_PAGE, pageNumber);
                                                contentValues.put(MovieDatabaseContract.PopularMovies.COLUMN_POSITION, i);

                                                contentValuesArrayIdsOnly[i] = contentValues;
                                                break;
                                            case Constants.TOP_RATED:
                                                contentValues.put(MovieDatabaseContract.TopRatedMovies._ID, item.getId());
                                                contentValues.put(MovieDatabaseContract.TopRatedMovies.COLUMN_PAGE, pageNumber);
                                                contentValues.put(MovieDatabaseContract.TopRatedMovies.COLUMN_POSITION, i);

                                                contentValuesArrayIdsOnly[i] = contentValues;
                                                break;
                                        }
                                    }

                                    getApplicationContext().getContentResolver().bulkInsert(
                                            MovieDatabaseContract.MovieEntry.CONTENT_URI,
                                            contentValuesArray
                                    );

                                    switch (tag) {
                                        case Constants.POPULAR:
                                            if(page == 1){
                                                getApplicationContext().getContentResolver().delete(
                                                        MovieDatabaseContract.PopularMovies.CONTENT_URI,
                                                        null,
                                                        null
                                                );
                                            }
                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    MovieDatabaseContract.PopularMovies.CONTENT_URI,
                                                    contentValuesArrayIdsOnly
                                            );
                                            break;
                                        case Constants.TOP_RATED:
                                            if(page == 1){
                                                getApplicationContext().getContentResolver().delete(
                                                        MovieDatabaseContract.TopRatedMovies.CONTENT_URI,
                                                        null,
                                                        null
                                                );
                                            }
                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    MovieDatabaseContract.TopRatedMovies.CONTENT_URI,
                                                    contentValuesArrayIdsOnly
                                            );
                                            break;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    Intent fetchCompletedIntent = new Intent(Constants.SYNC_UPDATES_FILTER);
                                    fetchCompletedIntent.putExtra(Constants.SYNC_STATUS, Constants.SYNC_COMPLETED);
                                    fetchCompletedIntent.putExtra(Constants.FRAGMENT_TAG, tag);
                                    fetchCompletedIntent.putExtra(Constants.TOTAL_ITEMS_LOADED, totalResults);
                                    fetchCompletedIntent.putExtra(Constants.LOADED_PAGE, page);
                                    LocalBroadcastManager.getInstance(SyncMovieService.this).sendBroadcast(fetchCompletedIntent);

                                }
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent fetchCompletedIntent = new Intent(Constants.SYNC_UPDATES_FILTER);
                        fetchCompletedIntent.putExtra(Constants.SYNC_STATUS, Constants.SYNC_FAILED);
                        fetchCompletedIntent.putExtra(Constants.FRAGMENT_TAG, tag);
                        LocalBroadcastManager.getInstance(SyncMovieService.this).sendBroadcast(fetchCompletedIntent);
                    }
                });

        VolleyRequestQueueProvider.getInstance(null).addToRequestQueue(movieListRequest);


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
