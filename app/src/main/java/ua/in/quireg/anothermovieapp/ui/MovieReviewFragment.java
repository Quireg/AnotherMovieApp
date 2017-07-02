package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.MovieReviewsRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieReview;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;


public class MovieReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MOVIE_REVIEW_LOADER = 4;


    private long mMovieId;
    private OnFragmentInteractionListener mListener;
    private MovieReviewsRecyclerViewAdapter mMovieReviewsRecyclerViewAdapter;


    public MovieReviewFragment() {
    }

    @SuppressWarnings("unused")
    public static MovieReviewFragment newInstance(long movieId) {
        MovieReviewFragment fragment = new MovieReviewFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.MOVIE, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getLong(Constants.MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_review_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mMovieReviewsRecyclerViewAdapter = new MovieReviewsRecyclerViewAdapter(getContext(), null);
            recyclerView.setAdapter(mMovieReviewsRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieDatabaseContract.MovieReviews.buildUri(mMovieId);

        return new CursorLoader(getActivity(),
                uri,
                MovieReview.MOVIES_REVIEWS_CLOMUNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieReviewsRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
