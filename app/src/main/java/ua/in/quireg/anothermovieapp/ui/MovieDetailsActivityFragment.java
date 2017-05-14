package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;


public class MovieDetailsActivityFragment extends Fragment implements FetchImageCallback {
    private static final String LOG_TAG = MovieDetailsActivityFragment.class.getSimpleName();
    private View view;
    private MovieItem movie;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton floatingActionButton;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        movie = (MovieItem) getArguments().getSerializable(Constants.MOVIE);

        floatingActionButton = (FloatingActionButton) container.getRootView().findViewById(R.id.fab);
        updateFloatingActionBar();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavourite(movie)) {
                    mListener.onFragmentMessage(Constants.ADD_TO_FAVOURITES, movie);
                } else {
                    mListener.onFragmentMessage(Constants.REMOVE_FROM_FAVOURITES, movie);
                }
                updateFloatingActionBar();
            }
        });

        //Set movie description
        TextView movie_description_textview = (TextView) view.findViewById(R.id.movie_decription);
        movie_description_textview.setText(movie.getOverview());

        //Set rating
        if (movie.getVote_average() != 0) {
            TextView movie_rating = (TextView) view.findViewById(R.id.movie_rating);
            movie_rating.setText(movie.getVote_average() + "/10");
        }
        new ImageFetcher(this, getContext()).execute(UriHelper.getImageUri(movie.getPosterPath(), Constants.IMAGE_SIZE_W185));

        TextView movie_original_title_textview = (TextView) view.findViewById(R.id.original_title_textview_text);
        movie_original_title_textview.setText(movie.getOriginalTitle());

        TextView movie_release_date_textview = (TextView) view.findViewById(R.id.movie_year);
        movie_release_date_textview.setText(movie.getReleaseDate());

        TextView movie_duration_textview = (TextView) view.findViewById(R.id.movie_duration);
        movie_duration_textview.setText(String.valueOf(movie.getRuntime()));

        return view;
    }

    @Override
    public void setImage(Bitmap bitmap) {
        if (view != null) {
            Drawable background = new BitmapDrawable(getResources(), bitmap);
            ImageView movie_poster = (ImageView) view.findViewById(R.id.movie_poster);
            movie_poster.setImageDrawable(background);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean isFavourite(MovieItem item) {
        Cursor c = null;
        try {
            c = getContext().getContentResolver().query(
                    MovieDatabaseContract.FavouriteMovies.buildUri(item.getId()),
                    null,
                    null,
                    null,
                    null
            );

            return c != null && c.moveToFirst();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void updateFloatingActionBar(){
        if (isFavourite(movie)) {
            floatingActionButton.setImageResource(android.R.drawable.star_big_on);
        } else {
            floatingActionButton.setImageResource(android.R.drawable.star_big_off);
        }
    }
}
