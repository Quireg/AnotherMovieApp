package ua.in.quireg.anothermovieapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleyRequestQueueProvider {

    private static final String LOG_TAG = VolleyRequestQueueProvider.class.getSimpleName();

    private static VolleyRequestQueueProvider mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;


    private VolleyRequestQueueProvider(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyRequestQueueProvider getInstance(Context context) {
        Log.d(LOG_TAG, "getInstance()");
        if (mInstance == null) {
            mInstance = new VolleyRequestQueueProvider(context);
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