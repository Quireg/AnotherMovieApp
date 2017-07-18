package ua.in.quireg.anothermovieapp.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;

public class FavouritesMoviesGridViewFragment extends MoviesGridViewFragment {
    private static final int FAVOURITE_MOVIE_LOADER = 2;
    private static final int DIALOG_FRAGMENT = 1;

    protected boolean fetchInProgress = false;
    protected long last_loaded_page = 0;
    protected long currentItem = 0;
    protected long totalItems = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favourites_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.sort_order:
                DialogFragment sortOrderPickerFragment = new SortOrderPicker();
                sortOrderPickerFragment.setTargetFragment(this, DIALOG_FRAGMENT);
                sortOrderPickerFragment.show(getFragmentManager(), "sortOrderPicker");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int preferred_sort_order = preferences.getInt(Constants.SORT_ORDER, -1);
        if(preferred_sort_order == -1){
            //Preferred sort order is set on application startup
            //It must not be -1 in any cases.
            throw new RuntimeException();
        }
        String sortByRow;
        switch (preferred_sort_order){
            case Constants.SORT_BY_NAME:
                sortByRow = MovieDatabaseContract.MovieEntry.COLUMN_TITLE;
                break;
            case Constants.SORT_BY_RATING:
                sortByRow = MovieDatabaseContract.MovieEntry.COLUMN_POPULARITY;
                break;
            case Constants.SORT_BY_RELEASE_DATE:
                sortByRow = MovieDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE;
                break;
            case Constants.SORT_BY_VOTES_COUNT:
                sortByRow = MovieDatabaseContract.MovieEntry.COLUMN_VOTE_COUNT;
                break;
            default:
                throw new RuntimeException();
        }
        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIES_CLOMUNS,
                null,
                null,
                sortByRow);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    getLoaderManager().restartLoader(FAVOURITE_MOVIE_LOADER, null, this);
                }
                break;
        }
    }

}
