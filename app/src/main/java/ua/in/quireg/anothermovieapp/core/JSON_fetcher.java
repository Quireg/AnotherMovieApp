package ua.in.quireg.anothermovieapp.core;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class JSON_fetcher {

    private static final String LOG_TAG = JSON_fetcher.class.getSimpleName();

    private static JSON_fetcher mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private JSON_fetcher(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized JSON_fetcher getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new JSON_fetcher(context);
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
        Log.d(LOG_TAG, "Request placed to queue");

    }



    public void requestMovieList(URL url) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(LOG_TAG, response.toString());
                            Log.d(LOG_TAG, "Parse started");
                            JSONArray arr = response.getJSONArray("results");


                            MovieItemList mil = MovieItemList.getInstance();
                            List<MovieItem> temp = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject movie = arr.getJSONObject(i);
                                temp.add(new MovieItem(
                                        movie.optString("original_title"),
                                        movie.optString("id"),
                                        movie.optDouble("vote_average"),
                                        movie.optString("backdrop_path"),
                                        movie.optString("poster_path"))
                                );
                            }
                            Log.d(LOG_TAG, "Parse finished");
                            mil.reInitialize(temp);


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