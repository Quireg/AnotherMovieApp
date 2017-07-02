package ua.in.quireg.anothermovieapp.ui;


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
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;

public class FavouritesMoviesGridViewFragment extends MoviesGridViewFragment {
    private static final int FAVOURITE_MOVIE_LOADER = 2;

    protected boolean fetchInProgress = false;
    protected long last_loaded_page = 0;
    protected long currentItem = 0;
    protected long totalItems = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTotalItems(recyclerViewAdapter.getItemCount());
        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setTotalItems(recyclerViewAdapter.getItemCount());
                if (recyclerViewAdapter.getItemCount() > 0) {
                    noFavouritesMoviesView.setVisibility(View.GONE);
                } else {
                    noFavouritesMoviesView.setVisibility(View.VISIBLE);
                }
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieDatabaseContract.FavouriteMovies.CONTENT_URI;

        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIES_CLOMUNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        recyclerViewAdapter.swapCursor(cursor);
        loadingView.setVisibility(View.GONE);

        if (!cursor.moveToFirst()) {
            noFavouritesMoviesView.setVisibility(View.VISIBLE);
        }

        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER, null, this);
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
                    supportActionBar.setTitle(getContext().getResources().getString(R.string.favourites_tab_name));
                    updateAdapterInfoTextView();
                }
            }
        }
    }

    @Override
    protected void handleMessage(Intent msg) {
        //do nothing
    }

    @Override
    public void fetchNewItems(){
        //do nothing
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
