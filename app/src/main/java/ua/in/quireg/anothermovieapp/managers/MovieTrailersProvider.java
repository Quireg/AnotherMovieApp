package ua.in.quireg.anothermovieapp.managers;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.common.MovieTrailer;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.interfaces.FetchTrailersCallback;
import ua.in.quireg.anothermovieapp.network.VolleyRequestQueueProvider;

public class MovieTrailersProvider {

    public void fetchTrailersList(MovieItem movie, final FetchTrailersCallback callback) {

        Uri uri = UriHelper.getMovieTrailerUriById(String.valueOf(movie.getId()));

        JsonObjectRequest movieTrailersRequest = new JsonObjectRequest(Request.Method.GET, uri.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray arr = response.getJSONArray("results");
                    List<MovieTrailer> trailers = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        MovieTrailer movieTrailer = new MovieTrailer(
                                obj.getString("id"),
                                obj.getString("iso_639_1"),
                                obj.getString("iso_3166_1"),
                                obj.getString("key"),
                                obj.getString("name"),
                                obj.getString("site"),
                                obj.getString("size"),
                                obj.getString("type")
                        );
                        trailers.add(movieTrailer);
                    }
                    callback.onTrailersFetchCompleted(trailers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleyRequestQueueProvider.getInstance(null).addToRequestQueue(movieTrailersRequest);

    }
}
