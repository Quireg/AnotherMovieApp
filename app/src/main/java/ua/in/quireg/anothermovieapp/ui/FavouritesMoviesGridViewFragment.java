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

    private boolean mFetchInProgress = false;
    private long mLastLoadedPage = 0;
    private long mCurrentItem = 0;
    private long mTotalItems = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            super.onCreate(savedInstanceState);
            mFetchInProgress = savedInstanceState.getBoolean(FETCH_IN_PROGRESS, false);
            mLastLoadedPage = savedInstanceState.getLong(LAST_LOADED_PAGE, 0);
            mCurrentItem = savedInstanceState.getLong(CURRENT_ITEM, 0);
            mTotalItems = savedInstanceState.getLong(TOTAL_ITEMS, 0);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(FETCH_IN_PROGRESS, mFetchInProgress);
        bundle.putLong(LAST_LOADED_PAGE, mLastLoadedPage);
        bundle.putLong(CURRENT_ITEM, mCurrentItem);
        bundle.putLong(TOTAL_ITEMS, mTotalItems);
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
        setmTotalItems(recyclerViewAdapter.getItemCount());
        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setmTotalItems(recyclerViewAdapter.getItemCount());
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
        return this.mCurrentItem;
    }

    @Override
    protected void setCurrentPosition(long position) {
        this.mCurrentItem = position;
    }

    protected boolean ismFetchInProgress() {
        return this.mFetchInProgress;
    }

    protected void setmFetchInProgress(boolean mFetchInProgress) {
        this.mFetchInProgress = mFetchInProgress;
    }

    protected long getmLastLoadedPage() {
        return this.mLastLoadedPage;
    }

    protected void setmLastLoadedPage(long mLastLoadedPage) {
        this.mLastLoadedPage = mLastLoadedPage;
    }

    protected long getmTotalItems() {
        return this.mTotalItems;
    }

    protected void setmTotalItems(long mTotalItems) {
        this.mTotalItems = mTotalItems;
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
