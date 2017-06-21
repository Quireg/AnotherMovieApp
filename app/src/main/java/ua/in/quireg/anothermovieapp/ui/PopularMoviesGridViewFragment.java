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
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;

public class PopularMoviesGridViewFragment extends MoviesGridViewFragment {
    private static final int POPULAR_MOVIE_LOADER = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
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
                }
            }
        }
    }

    @Override
    protected void handleMessage(Intent msg) {
        fetchInProgress = false;
        switch (msg.getStringExtra(Constants.SYNC_STATUS)) {
            case Constants.SYNC_COMPLETED:
                totalItems = msg.getLongExtra(Constants.TOTAL_ITEMS_LOADED, 0);
                last_loaded_page = msg.getLongExtra(Constants.LOADED_PAGE, 0);

                updateProgressBarVisibility();
                updateAdapterInfoTextView();
                break;
            case Constants.SYNC_FAILED:
                updateProgressBarVisibility();
                GeneralUtils.showToastMessage(getContext(), getString(R.string.error_fetch_failed));
                break;
            default:
                break;
        }
    }
}
