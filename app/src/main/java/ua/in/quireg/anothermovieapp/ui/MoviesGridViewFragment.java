package ua.in.quireg.anothermovieapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.MovieRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.adapters.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;

public class MoviesGridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private CursorRecyclerViewAdapter recyclerViewAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private String fragmentTag;

    private View loadingView = null;
    private View progressBarView = null;
    private View noFavouritesMoviesView = null;
    private TextView pageNumberAndTotal = null;

    private boolean fetchInProgress = false;
    private long last_loaded_page = 0;
    private long currentItem = 0;
    private long totalItems = 0;


    private static final int POPULAR_MOVIE_LOADER = 0;
    private static final int TOP_RATED_MOVIE_LOADER = 1;
    private static final int FAVOURITE_MOVIE_LOADER = 2;
    private final int LOAD_ITEMS_THRESHOLD = 10;

    private Toast fetchFailedToast;

    public MoviesGridViewFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        fragmentTag = (String) getArguments().getSerializable(Constants.FRAGMENT_TAG);
        recyclerViewAdapter = new MovieRecyclerViewAdapter(getActivity(), null, 0, fragmentTag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        noFavouritesMoviesView = view.findViewById(R.id.favourites_no_movies);
        pageNumberAndTotal = (TextView) view.findViewById(R.id.pageNumberAndTotal);
        updateAdapterInfoTextView();

        //Set view to loading state until data is fetched
        loadingView = view.findViewById(R.id.loading);
        loadingView.setVisibility(View.VISIBLE);

        progressBarView = view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);

        // Set the adapter
        final GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), Constants.COLUMNS_NUMBER);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        currentItem = layoutManager.findLastVisibleItemPosition();
        if (!fragmentTag.equals(Constants.FAVOURITES)) {
            fetchNewItems();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                //scrolled down
                if (dy > 0) {

                    currentItem = layoutManager.findLastVisibleItemPosition();
                    updateAdapterInfoTextView();

                    int itemsInAdapter = recyclerView.getAdapter().getItemCount();
                    //check if we have reached the end
                    if (itemsInAdapter - currentItem < LOAD_ITEMS_THRESHOLD && !fragmentTag.equals(Constants.FAVOURITES)) {
                        fetchNewItems();
                    }
                } else if (dy < 0) {
                    currentItem = layoutManager.findLastVisibleItemPosition();
                    updateAdapterInfoTextView();
                }
            }
        });
        return view;
    }

    public void fetchNewItems(){
        if(fetchInProgress){
            return;
        }
        long pageToLoad = last_loaded_page + 1;
        fetchInProgress = true;
        SyncMovieService.startActionFetchMovies(mContext, fragmentTag, pageToLoad + "");
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((mMessageReceiver), new IntentFilter(Constants.SYNC_UPDATES_FILTER));
    }
    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };
    private void handleMessage(Intent msg)
    {
        if(!msg.getStringExtra(Constants.FRAGMENT_TAG).equals(fragmentTag)){
            return;
        }
        fetchInProgress = false;
        switch (msg.getStringExtra(Constants.SYNC_STATUS)){
            case Constants.SYNC_COMPLETED:
                totalItems = msg.getLongExtra(Constants.TOTAL_ITEMS_LOADED, 0);
                last_loaded_page = msg.getLongExtra(Constants.LOADED_PAGE, 0);

                if (progressBarView != null) {
                    progressBarView.setVisibility(View.GONE);
                    updateAdapterInfoTextView();
                }

                break;
            case Constants.SYNC_FAILED:
                if (progressBarView != null) {
                    progressBarView.setVisibility(View.GONE);
                }
                if (fetchFailedToast != null) {
                    fetchFailedToast.cancel();
                    fetchFailedToast = Toast.makeText(getContext(), "Error occurred while fetching new movies", Toast.LENGTH_SHORT);
                    fetchFailedToast.show();
                }
                break;
            default:
                break;
        }
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

                uri = MovieDatabaseContract.PopularMovies.CONTENT_URI;
                break;
            case Constants.TOP_RATED:
                sortOrder = MovieDatabaseContract.TopRatedMovies.COLUMN_PAGE + " ASC, " +
                        MovieDatabaseContract.TopRatedMovies.COLUMN_POSITION + " ASC";

                uri = MovieDatabaseContract.TopRatedMovies.CONTENT_URI;
                break;
            case Constants.FAVOURITES:
                sortOrder = null;

                uri = MovieDatabaseContract.FavouriteMovies.CONTENT_URI;
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

        switch (fragmentTag) {
            case Constants.POPULAR:
            case Constants.TOP_RATED:
                if (cursor.moveToFirst()) {
                    loadingView.setVisibility(View.GONE);
                }
                break;
            case Constants.FAVOURITES:
                if (cursor.moveToFirst()) {
                    loadingView.setVisibility(View.GONE);
                } else {
                    loadingView.setVisibility(View.GONE);
                    noFavouritesMoviesView.setVisibility(View.VISIBLE);
                }
                break;
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


    private void updateAdapterInfoTextView() {
        pageNumberAndTotal.setText(currentItem + "/" + totalItems);
    }

}
