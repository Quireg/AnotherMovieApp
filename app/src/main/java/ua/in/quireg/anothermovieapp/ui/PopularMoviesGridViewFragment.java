package ua.in.quireg.anothermovieapp.ui;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;

public class PopularMoviesGridViewFragment extends MoviesGridViewFragment {
    private static final int POPULAR_MOVIE_LOADER = 1;

    protected boolean fetchInProgress = false;
    protected long last_loaded_page = 0;
    protected long currentItem = 0;
    protected long totalItems = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieDatabaseContract.PopularMovies.COLUMN_PAGE + " ASC, " +
                MovieDatabaseContract.PopularMovies.COLUMN_POSITION + " ASC";
        Uri uri = MovieDatabaseContract.PopularMovies.CONTENT_URI;

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
        loadingView.setVisibility(View.GONE);

        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                ActionBar supportActionBar = activity.getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle(getContext().getResources().getString(R.string.popular_tab_name));
                    updateAdapterInfoTextView();
                }
            }
        }
    }

    @Override
    protected void handleMessage(Intent msg) {
        if(!msg.getStringExtra(Constants.FRAGMENT_TAG).equals(Constants.POPULAR)){
            return;
        }
        setFetchInProgress(false);
        updateProgressBarVisibility();
        switch (msg.getStringExtra(Constants.SYNC_STATUS)) {
            case Constants.SYNC_COMPLETED:
                setTotalItems(msg.getLongExtra(Constants.TOTAL_ITEMS_LOADED, 0));
                setLast_loaded_page(msg.getLongExtra(Constants.LOADED_PAGE, 0));
                break;
            case Constants.SYNC_FAILED:
                GeneralUtils.showToastMessage(getContext(), getString(R.string.error_fetch_failed));
                break;
            default:
                break;
        }
    }

    public void fetchNewItems(){
        if(isFetchInProgress()){
            return;
        }
        long pageToLoad = getLast_loaded_page() + 1;
        setFetchInProgress(true);
        updateProgressBarVisibility();
        SyncMovieService.startActionFetchMovies(getContext(), Constants.POPULAR, pageToLoad + "");
    }

    @Override
    protected long getCurrentPosition() {
        return this.currentItem;
    }

    @Override
    protected void setCurrentPosition(long position) {
        this.currentItem = position;
    }

    @Override
    protected boolean isFetchInProgress() {
        return this.fetchInProgress;
    }

    @Override
    protected void setFetchInProgress(boolean fetchInProgress) {
        this.fetchInProgress = fetchInProgress;
    }

    @Override
    protected long getLast_loaded_page() {
        return this.last_loaded_page;
    }

    @Override
    protected void setLast_loaded_page(long last_loaded_page) {
        this.last_loaded_page = last_loaded_page;
    }

    @Override
    protected long getTotalItems() {
        return this.totalItems;
    }

    @Override
    protected void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }
}
