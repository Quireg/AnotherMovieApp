package com.anothermovieapp.movieList

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.repository.EntityDBMovie
import com.anothermovieapp.managers.MovieDatabaseContract.FavouriteMovies
import com.anothermovieapp.managers.MovieDatabaseContract.MovieEntry

class FavouritesMoviesGridViewFragment : MoviesGridViewFragment() {
    protected override var isFetchInProgress = false
    protected override var currentPosition: Long = 0
    protected override var totalItems: Long = 0
    private var mNoFavouritesMoviesView: View? = null
    override var mFragmentTag: String = Constants.FAVOURITES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title =
            requireContext().resources.getString(R.string.favourites_tab_name)
        updateAdapterInfoTextView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNoFavouritesMoviesView = view.findViewById(R.id.favourites_no_movies)
        totalItems = mRecyclerViewAdapter!!.itemCount.toLong()
        mRecyclerViewAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                totalItems = mRecyclerViewAdapter!!.itemCount.toLong()
                if (mNoFavouritesMoviesView != null) {
                    mNoFavouritesMoviesView!!.visibility =
                        if (mRecyclerViewAdapter!!.itemCount > 0) View.GONE else View.VISIBLE
                }
            }
        })
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putBoolean(MoviesGridViewFragment.Companion.FETCH_IN_PROGRESS, isFetchInProgress)
        bundle.putLong(MoviesGridViewFragment.Companion.CURRENT_ITEM, currentPosition)
        bundle.putLong(MoviesGridViewFragment.Companion.TOTAL_ITEMS, totalItems)
    }

    override fun onCreateOptionsMenu(
        menu: Menu, inflater: MenuInflater
    ) {
        inflater.inflate(R.menu.favourites_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle item selection
        return when (item.itemId) {
            R.id.sort_order -> {
                val sortOrderPickerFragment: DialogFragment = SortOrderPicker()
                sortOrderPickerFragment.setTargetFragment(this, DIALOG_FRAGMENT)
                sortOrderPickerFragment.show(parentFragmentManager, "sortOrderPicker")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }


//    fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
//        val uri = FavouriteMovies.CONTENT_URI
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val preferred_sort_order = preferences.getInt(Constants.SORT_ORDER, -1)
//        if (preferred_sort_order == -1) {
//            //Preferred sort order is set on application startup
//            //It must not be -1 in any cases.
//            throw RuntimeException()
//        }
//        val sortByRow: String
//        sortByRow = when (preferred_sort_order) {
//            Constants.SORT_BY_NAME -> MovieEntry.COLUMN_TITLE
//            Constants.SORT_BY_RATING -> MovieEntry.COLUMN_POPULARITY
//            Constants.SORT_BY_RELEASE_DATE -> MovieEntry.COLUMN_RELEASE_DATE
//            Constants.SORT_BY_VOTES_COUNT -> MovieEntry.COLUMN_VOTE_COUNT
//            else -> MovieEntry.COLUMN_VOTE_COUNT
//        }
//        return CursorLoader(context!!,
//                uri!!,
//                EntityDBMovie.Companion.MOVIES_CLOMUNS,
//                null,
//                null,
//                sortByRow)
//    }
//
//    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
//        mRecyclerViewAdapter!!.swapCursor(cursor)
//        mLoadingView!!.visibility = View.GONE
//        if (mPosition != RecyclerView.NO_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mRecyclerView!!.smoothScrollToPosition(mPosition)
//        }
//    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
////        loaderManager.initLoader(FAVOURITE_MOVIE_LOADER, null, this)
//        super.onActivityCreated(savedInstanceState)
//    }

    override fun fetchNewItems() {
        //do nothing
    }

    override fun reload() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DIALOG_FRAGMENT -> if (resultCode == Activity.RESULT_OK) {
//                loaderManager.restartLoader(FAVOURITE_MOVIE_LOADER, null, this)
            }
        }
    }

    companion object {
        private const val FAVOURITE_MOVIE_LOADER = 2
        private const val DIALOG_FRAGMENT = 1
    }
}