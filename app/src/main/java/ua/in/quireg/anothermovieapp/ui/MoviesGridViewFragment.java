package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.MovieRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.adapters.helpers.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.FetchMoreItemsCallback;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.managers.MoviePageLoader;

public class MoviesGridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FetchMoreItemsCallback {

    private RecyclerView recyclerView;
    private CursorRecyclerViewAdapter recyclerViewAdapter;
    private MoviePageLoader moviePageLoader;
    private int mPosition = RecyclerView.NO_POSITION;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private String fragmentTag;

    private View loadingView = null;
    private View progressBarView = null;


    private static final int POPULAR_MOVIE_LOADER = 0;
    private static final int TOP_RATED_MOVIE_LOADER = 1;
    private static final int FAVOURITE_MOVIE_LOADER = 2;
    private int adapterItemCount = 0;


    public MoviesGridViewFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        fragmentTag = (String) getArguments().getSerializable(Constants.FRAGMENT_TAG);
        recyclerViewAdapter = new MovieRecyclerViewAdapter(getActivity(), null, 0, fragmentTag);
        moviePageLoader = new MoviePageLoader(getContext(), fragmentTag, MoviesGridViewFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        //Set view to loading state until data is fetched
        loadingView = view.findViewById(R.id.loading);
        loadingView.setVisibility(View.VISIBLE);
        progressBarView = view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);

        // Set the adapter
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), Constants.COLUMN_NUMBER);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                //scrolled down
                if (dy > 0) {
                    //check if we have reached the end
                    if (!recyclerView.canScrollVertically(1)) {
                        adapterItemCount = recyclerView.getAdapter().getItemCount();
                        moviePageLoader.fetchNewItems(adapterItemCount);
                    }
                }
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        switch (fragmentTag) {
            case Constants.POPULAR:
                getLoaderManager().initLoader(POPULAR_MOVIE_LOADER, null, this);
                break;
            case Constants.TOP_RATED:
                getLoaderManager().initLoader(TOP_RATED_MOVIE_LOADER, null, this);
                break;
            case Constants.FAVOURITES:
                getLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER, null, this);
                break;
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                ActionBar abar = activity.getSupportActionBar();
                if (abar != null) {
                    switch (fragmentTag) {
                        case Constants.POPULAR:
                            abar.setTitle(mContext.getResources().getString(R.string.popular_tab_name));
                            break;
                        case Constants.TOP_RATED:
                            abar.setTitle(mContext.getResources().getString(R.string.top_rated_tab_name));
                            break;
                        case Constants.FAVOURITES:
                            abar.setTitle(mContext.getResources().getString(R.string.favourites_tab_name));
                            break;
                    }

                }
            }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder;
        Uri uri;
        switch (fragmentTag) {
            case Constants.POPULAR:
                sortOrder = MovieDatabaseContract.PopularMovies.COLUMN_PAGE + " ASC, " +
                        MovieDatabaseContract.PopularMovies.COLUMN_POSITION + " ASC";

                uri = MovieDatabaseContract.PopularMovies.buildUri();
                break;
            case Constants.TOP_RATED:
                sortOrder = MovieDatabaseContract.TopRatedMovies.COLUMN_PAGE + " ASC, " +
                        MovieDatabaseContract.TopRatedMovies.COLUMN_POSITION + " ASC";

                uri = MovieDatabaseContract.TopRatedMovies.buildUri();
                break;
            default:
                return null;
        }

        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIES_CLOMUNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        recyclerViewAdapter.swapCursor(cursor);

        if (cursor.moveToFirst()) {
            loadingView.setVisibility(View.GONE);
        }

        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void fetchStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBarView != null) {
                    progressBarView.setEnabled(true);
                    progressBarView.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    public void fetchCompleted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBarView != null) {
                    progressBarView.setEnabled(false);
                    progressBarView.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void fetchErrorOccured() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBarView != null) {
                    progressBarView.setEnabled(false);
                    progressBarView.setVisibility(View.GONE);
                }
                Toast fetchFailedToast = Toast.makeText(getContext(), "Error occured while fetching new movies", Toast.LENGTH_SHORT);
                fetchFailedToast.show();
            }
        });


    }
}
