package ua.in.quireg.anothermovieapp.movieList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;

public class TopRatedMoviesGridViewFragment extends MoviesGridViewFragment {

    private static final int TOP_RATED_MOVIE_LOADER = 1;

    protected boolean mFetchInProgress = false;
    protected long mLastLoadedPage = 0;
    protected long mCurrentItem = 0;
    protected long mTotalItems = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            super.onCreate(savedInstanceState);
            mFetchInProgress = savedInstanceState.getBoolean(FETCH_IN_PROGRESS, false);
            mLastLoadedPage = savedInstanceState.getLong(LAST_LOADED_PAGE, 0);
            mCurrentItem = savedInstanceState.getLong(CURRENT_ITEM, 0);
            mTotalItems = savedInstanceState.getLong(TOTAL_ITEMS, 0);
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieDatabaseContract.TopRatedMovies.COLUMN_PAGE + " ASC, " +
                MovieDatabaseContract.TopRatedMovies.COLUMN_POSITION + " ASC";

        Uri uri = MovieDatabaseContract.TopRatedMovies.CONTENT_URI;

        return new CursorLoader(getActivity(),
                uri,
                MovieItem.MOVIES_CLOMUNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mRecyclerViewAdapter.swapCursor(cursor);
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TOP_RATED_MOVIE_LOADER, null, this);
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
                    supportActionBar.setTitle(getContext().getResources().getString(R.string.top_rated_tab_name));
                    updateAdapterInfoTextView();
                }
            }
        }
    }

    @Override
    protected void handleMessage(Intent msg) {
        if (!msg.getStringExtra(Constants.FRAGMENT_TAG).equals(Constants.TOP_RATED)) {
            return;
        }
        setFetchInProgress(false);
        updateProgressBarVisibility();
        switch (msg.getStringExtra(Constants.SYNC_STATUS)) {
            case Constants.SYNC_COMPLETED:
                setTotalItems(msg.getLongExtra(Constants.TOTAL_ITEMS_LOADED, 0));
                setLastLoadedPage(msg.getLongExtra(Constants.LOADED_PAGE, 0));
                break;
            case Constants.SYNC_FAILED:
                GeneralUtils.showToastMessage(getContext(), getString(R.string.error_fetch_failed));
                break;
            default:
                break;
        }
    }

    public void fetchNewItems() {
        if (isFetchInProgress()) {
            return;
        }
        long pageToLoad = getLastLoadedPage() + 1;
        setFetchInProgress(true);
        updateProgressBarVisibility();
        SyncMovieService.startFetchMovies(getContext(), Constants.TOP_RATED, pageToLoad + "");
    }

    @Override
    protected long getCurrentPosition() {
        return this.mCurrentItem;
    }

    @Override
    protected void setCurrentPosition(long position) {
        this.mCurrentItem = position;
    }

    protected boolean isFetchInProgress() {
        return this.mFetchInProgress;
    }

    protected void setFetchInProgress(boolean mFetchInProgress) {
        this.mFetchInProgress = mFetchInProgress;
    }

    protected long getLastLoadedPage() {
        return this.mLastLoadedPage;
    }

    protected void setLastLoadedPage(long mLastLoadedPage) {
        this.mLastLoadedPage = mLastLoadedPage;
    }

    protected long getTotalItems() {
        return this.mTotalItems;
    }

    protected void setTotalItems(long mTotalItems) {
        this.mTotalItems = mTotalItems;
    }
}
