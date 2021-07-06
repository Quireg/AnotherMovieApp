package com.anothermovieapp.movieList

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.common.GeneralUtils
import com.anothermovieapp.repository.Movie
import com.anothermovieapp.managers.MovieDatabaseContract.TopRatedMovies

class TopRatedMoviesGridViewFragment : MoviesGridViewFragment() {
    protected override var isFetchInProgress = false
    protected override var lastLoadedPage: Long = 0
    protected override var currentPosition: Long = 0
    protected override var totalItems: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            super.onCreate(savedInstanceState)
            isFetchInProgress = savedInstanceState.getBoolean(MoviesGridViewFragment.Companion.FETCH_IN_PROGRESS, false)
            lastLoadedPage = savedInstanceState.getLong(MoviesGridViewFragment.Companion.LAST_LOADED_PAGE, 0)
            currentPosition = savedInstanceState.getLong(MoviesGridViewFragment.Companion.CURRENT_ITEM, 0)
            totalItems = savedInstanceState.getLong(MoviesGridViewFragment.Companion.TOTAL_ITEMS, 0)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putBoolean(MoviesGridViewFragment.Companion.FETCH_IN_PROGRESS, isFetchInProgress)
        bundle.putLong(MoviesGridViewFragment.Companion.LAST_LOADED_PAGE, lastLoadedPage)
        bundle.putLong(MoviesGridViewFragment.Companion.CURRENT_ITEM, currentPosition)
        bundle.putLong(MoviesGridViewFragment.Companion.TOTAL_ITEMS, totalItems)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val sortOrder = TopRatedMovies.COLUMN_PAGE + " ASC, " +
                TopRatedMovies.COLUMN_POSITION + " ASC"
        val uri = TopRatedMovies.CONTENT_URI
        return CursorLoader(activity!!,
                uri!!,
                Movie.Companion.MOVIES_CLOMUNS,
                null,
                null,
                sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        mRecyclerViewAdapter!!.swapCursor(cursor)
        if (mLoadingView != null) {
            mLoadingView!!.visibility = View.GONE
        }
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView!!.smoothScrollToPosition(mPosition)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loaderManager.initLoader(TOP_RATED_MOVIE_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val activity = activity as AppCompatActivity?
            if (activity != null) {
                val supportActionBar = activity.supportActionBar
                if (supportActionBar != null) {
                    supportActionBar.title = context!!.resources.getString(R.string.top_rated_tab_name)
                    updateAdapterInfoTextView()
                }
            }
        }
    }

    override fun handleMessage(msg: Intent) {
        if (msg.getStringExtra(Constants.FRAGMENT_TAG) != Constants.TOP_RATED) {
            return
        }
        fetchInProgress = false
        updateProgressBarVisibility()
        when (msg.getStringExtra(Constants.SYNC_STATUS)) {
            Constants.SYNC_COMPLETED -> {
                totalItems = msg.getLongExtra(Constants.TOTAL_ITEMS_LOADED, 0)
                lastLoadedPage = msg.getLongExtra(Constants.LOADED_PAGE, 0)
            }
            Constants.SYNC_FAILED -> GeneralUtils.showToastMessage(context, getString(R.string.error_fetch_failed))
            else -> {
            }
        }
    }

    override fun fetchNewItems() {
        if (isFetchInProgress) {
            return
        }
        val pageToLoad = lastLoadedPage + 1
        fetchInProgress = true
        updateProgressBarVisibility()
        SyncMovieService.Companion.startFetchMovies(context, Constants.TOP_RATED, pageToLoad.toString() + "")
    }

    companion object {
        private const val TOP_RATED_MOVIE_LOADER = 1
    }
}