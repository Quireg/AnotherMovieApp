package ua.in.quireg.anothermovieapp.network;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.IMovieListListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;


public class MovieFetcher {

    private static final String LOG_TAG = MovieFetcher.class.getSimpleName();

    private static MovieFetcher mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private IMovieListListener callback;


    private MovieFetcher(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        if (context instanceof IMovieListListener) {
            callback = (IMovieListListener) context;
        }

    }

    public static synchronized MovieFetcher getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new MovieFetcher(context);
            Log.d(LOG_TAG, "JSON Fetcher created");
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            Log.d(LOG_TAG, "Volley.newRequestQueue requested");

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        Log.d(LOG_TAG, "Request placed to queue " + req.toString());

    }



    public void requestMovieList(final IMovieListListener callback, final String requstedList) {
        Uri uri = UriHelper.getMoviesListUri(requstedList);
        if(uri == null){
            return;
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(LOG_TAG, response.toString());
                            Log.d(LOG_TAG, "Parse started");
                            JSONArray arr = response.getJSONArray("results");


                            List<MovieItem> temp = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject movie = arr.getJSONObject(i);
                                MovieItem item = MovieItem.fromJSON(movie);
                                temp.add(item);
                                mCtx.getContentResolver().insert(MovieDatabaseContract.MovieEntry.CONTENT_URI, MovieItem.contentValuesFromObj(item));

                            }
                            Log.d(LOG_TAG, "Parse finished");

                            callback.setMoviesList(temp, requstedList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        this.addToRequestQueue(jsObjRequest);

    }


}