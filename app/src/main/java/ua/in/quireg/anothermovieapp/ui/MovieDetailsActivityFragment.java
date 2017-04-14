package ua.in.quireg.anothermovieapp.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.async.ImageFetcher;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.FetchImageCallback;


public class MovieDetailsActivityFragment extends Fragment implements FetchImageCallback {
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


        //Set movie description
        TextView movie_description_textview = (TextView) rootView.findViewById(R.id.movie_decription);
        movie_description_textview.setText(movie.getOverview());

        //Set rating
        if (movie.getVote_average() != 0) {
            TextView movie_rating = (TextView) rootView.findViewById(R.id.movie_rating);
            movie_rating.setText(movie.getVote_average() + "/10");
        }
        new ImageFetcher(this, getContext()).execute(UriHelper.getImageUri(movie.getPosterPath(), Constants.IMAGE_SIZE_W185));
        return rootView;
    }

    @Override
    public void setImage(Bitmap bitmap) {
        if (rootView != null) {
            Drawable background = new BitmapDrawable(getResources(), bitmap);
            ImageView movie_poster = (ImageView) rootView.findViewById(R.id.movie_poster);
            movie_poster.setImageDrawable(background);
        }
    }
}
