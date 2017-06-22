package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.async.ImageFetcher;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.common.MovieTrailer;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.interfaces.FetchImageCallback;
import ua.in.quireg.anothermovieapp.interfaces.FetchTrailersCallback;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.managers.MovieTrailersProvider;


public class MovieDetailsActivityFragment extends Fragment implements FetchImageCallback, FetchTrailersCallback {
    private static final String LOG_TAG = MovieDetailsActivityFragment.class.getSimpleName();
    private View view;
    private MovieItem movie;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton floatingActionButton;
    private LinearLayout movie_trailer_linear_layout;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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
        System.out.println(UriHelper.getMovieTrailerUriById(String.valueOf(movie.getId())));

        //Set movie description
        TextView movie_description_textview = (TextView) view.findViewById(R.id.movie_description);
        movie_description_textview.setText(movie.getOverview());

        //Set rating
        if (movie.getVote_average() != 0) {
            TextView movie_rating = (TextView) view.findViewById(R.id.movie_rating);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String movie_rating_text = String.format(getResources().getString(R.string.ui_details_rating_value), decimalFormat.format(movie.getVote_average()));
            movie_rating.setText(movie_rating_text);
        }
        new ImageFetcher(this, getContext())
                .execute(
                        UriHelper.getImageUri(movie.getPosterPath(),
                                Constants.IMAGE_SIZE_W185)
                );

        TextView movie_original_title_textview = (TextView) view.findViewById(R.id.original_title_textview_text);
        TextView movie_release_date_textview = (TextView) view.findViewById(R.id.movie_year);
        TextView movie_votecount_textview = (TextView) view.findViewById(R.id.movie_vote_count);
        movie_original_title_textview.setText(movie.getOriginalTitle());
        movie_release_date_textview.setText(movie.getReleaseDate());
        movie_votecount_textview.setText(String.valueOf(movie.getVoteCount()));

        //Trailers
        movie_trailer_linear_layout = (LinearLayout) view.findViewById(R.id.movie_trailers_layout);
        new MovieTrailersProvider().fetchTrailersList(movie, this);

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

    private void updateFloatingActionBar() {
        if (isFavourite(movie)) {
            floatingActionButton.setImageResource(android.R.drawable.star_big_on);
        } else {
            floatingActionButton.setImageResource(android.R.drawable.star_big_off);
        }
    }

    @Override
    public void onTrailersFetchCompleted(List<MovieTrailer> trailers) {
        for (final MovieTrailer trailer : trailers) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View single_trailer_view = inflater.inflate(R.layout.movie_trailer_list_item, movie_trailer_linear_layout, false);

            TextView trailer_name = (TextView) single_trailer_view.findViewById(R.id.trailer_title);
            TextView trailer_quality = (TextView) single_trailer_view.findViewById(R.id.trailer_quality);
            ImageView trailer_preview = (ImageView) single_trailer_view.findViewById(R.id.trailer_preview);
            final TextView trailer_key = (TextView) single_trailer_view.findViewById(R.id.trailer_key);
            Picasso.with(getContext()).load(UriHelper.getYouTubeTrailerPreviewLink(trailer.key)).into(trailer_preview);
            trailer_name.setText(trailer.name);
            trailer_quality.setText(String.format(getString(R.string.ui_details_trailer_quality), trailer.size));
            trailer_key.setText(trailer.key);
            single_trailer_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = UriHelper.getYouTubeLinkToPlay(String.valueOf(trailer.key));
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });
            movie_trailer_linear_layout.addView(single_trailer_view);
        }
    }
}
