package ua.in.quireg.anothermovieapp.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import ua.in.quireg.anothermovieapp.common.MovieAppLogger;
import ua.in.quireg.anothermovieapp.interfaces.FetchImageCallback;

public class ImageFetcher extends AsyncTask<Uri, Void, Bitmap> {

    private static final String LOG_TAG = ImageFetcher.class.getSimpleName();

    private Context mContext;
    private FetchImageCallback callback;


    public ImageFetcher(FetchImageCallback callback, Context context) {
        this.mContext = context;
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        try {
            MovieAppLogger.d(LOG_TAG, "Fetching: " + params[0].toString());
            return Picasso.with(mContext).load(params[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        callback.setImage(bitmap);
    }
}