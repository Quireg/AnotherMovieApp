package ua.in.quireg.anothermovieapp.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MLog;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;


public class MovieDetailsActivityFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailsActivityFragment.class.getSimpleName();
    private View rootView;
    MovieItem movie;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        movie = (MovieItem) getArguments().getSerializable(Constants.MOVIE);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar abar = activity.getSupportActionBar();
            if (abar != null) {
                abar.setTitle(movie.getOriginalTitle());
            }
        }
        //Set movie title
        TextView movie_title_textview = (TextView) rootView.findViewById(R.id.movie_title);
        movie_title_textview.getBackground().setAlpha(95);
        movie_title_textview.setText(movie.getTitle());

        //Set movie description
        TextView movie_description_textview = (TextView) rootView.findViewById(R.id.movie_decription);
        movie_description_textview.setText(movie.getDescription());

        new FetchImage().execute(UriHelper.getOriginalSizeBitmapUri(movie.getImageFullSize()));
        return rootView;
    }

    private void setBackground(Bitmap bitmap){
        if(rootView != null){
            Drawable background = new BitmapDrawable(getResources(), bitmap);
            rootView.setBackground(background);
        }
    }

    private class FetchImage extends AsyncTask<Uri, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Uri... params) {
            try {
                MLog.d(LOG_TAG, "Fetching: " + params[0].toString());
                Bitmap bitmap = Picasso.with(getContext()).load(params[0]).get();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            setBackground(bitmap);
        }
    }

}
