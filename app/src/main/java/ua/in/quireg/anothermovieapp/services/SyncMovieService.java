package ua.in.quireg.anothermovieapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.FetchMoreItemsCallback;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.network.MovieFetcher;

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

    private static FetchMoreItemsCallback fetchMoreItemsCallback = null;

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
    public static void startActionFetchMoviesForPage(Context context, String tag, String pageNumber, FetchMoreItemsCallback callback) {
        fetchMoreItemsCallback = callback;
        fetchMoreItemsCallback.fetchStarted();
        startActionFetchMoviesInitial(context, tag, pageNumber);
    }

    public static void startActionFetchMoviesInitial(Context context, String tag, String pageNumber) {
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
                final String listType = intent.getStringExtra(EXTRA_PARAM_TAG);
                final String pageNumber = intent.getStringExtra(EXTRA_PARAM_PAGE_NUMBER);
                handleActionFetchNewMovies(listType, pageNumber);
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
    private void handleActionFetchNewMovies(final String listType, final String pageNumber) {


        Uri uri = UriHelper.getMoviesListPageUri(listType, pageNumber);

        JsonObjectRequest movieListRequest = new JsonObjectRequest
                (Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d(LOG_TAG, "onResponse()");
                                    Log.d(LOG_TAG, response.toString());
                                    JSONArray arr = response.getJSONArray("results");

                                    ContentValues[] contentValuesArray = new ContentValues[arr.length()];
                                    ContentValues[] contentValuesArrayIdsOnly = new ContentValues[arr.length()];


                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject movie = arr.getJSONObject(i);
                                        MovieItem item = MovieItem.fromJSON(movie);

                                        contentValuesArray[i] = MovieItem.contentValuesFromObj(item);
                                        ContentValues contentValues = new ContentValues();

                                        switch (listType) {
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

                                    switch (listType) {
                                        case Constants.POPULAR:
                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    MovieDatabaseContract.PopularMovies.CONTENT_URI,
                                                    contentValuesArrayIdsOnly
                                            );
                                            break;
                                        case Constants.TOP_RATED:
                                            getApplicationContext().getContentResolver().bulkInsert(
                                                    MovieDatabaseContract.TopRatedMovies.CONTENT_URI,
                                                    contentValuesArrayIdsOnly
                                            );
                                            break;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    if(fetchMoreItemsCallback != null){
                                        fetchMoreItemsCallback.fetchCompleted();
                                    }
                                }
                            }
                        }).start();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(fetchMoreItemsCallback != null){
                            fetchMoreItemsCallback.fetchErrorOccured();
                        }
                    }
                });

        MovieFetcher.getInstance(getApplicationContext()).addToRequestQueue(movieListRequest);


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
