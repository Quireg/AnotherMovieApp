package ua.in.quireg.anothermovieapp.network;

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

import java.util.List;

import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;


public class MovieFetcher {

    private static final String LOG_TAG = MovieFetcher.class.getSimpleName();

    private static MovieFetcher mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;


    private MovieFetcher(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MovieFetcher getInstance(Context context) {
        Log.d(LOG_TAG, "getInstance()");
        if (mInstance == null) {
            mInstance = new MovieFetcher(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        Log.d(LOG_TAG, "getRequestQueue()");

        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        Log.d(LOG_TAG, "Request placed to queue " + req.toString());

    }


}