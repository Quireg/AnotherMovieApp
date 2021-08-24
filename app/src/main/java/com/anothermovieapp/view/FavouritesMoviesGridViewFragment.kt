/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.viewmodel.ViewModelFavoriteMovies
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesMoviesGridViewFragment : MoviesGridViewFragment() {
    override var isFetchInProgress = false
    override var isEmptyData = false
    override var currentPosition: Long = 0
    override var totalItems: Long = 0
    private var mNoFavouritesMoviesView: View? = null
    override var mFragmentTag: String = Constants.FAVOURITES

    @Inject
    lateinit var viewModel: ViewModelFavoriteMovies

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
        updateProgressBarVisibility()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNoFavouritesMoviesView = view.findViewById(R.id.favourites_no_movies)
        totalItems = mListRecyclerViewAdapter!!.itemCount.toLong()
        mListRecyclerViewAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                totalItems = mListRecyclerViewAdapter!!.itemCount.toLong()
                if (mNoFavouritesMoviesView != null) {
                    mNoFavouritesMoviesView!!.visibility =
                        if (mListRecyclerViewAdapter!!.itemCount > 0) View.GONE else View.VISIBLE
                }
            }
        })
        viewModel.movies.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).post {
                mLoadingView!!.visibility = View.GONE
                mNoFavouritesMoviesView!!.visibility =
                    if (it.data == null || it.data.isEmpty()) View.VISIBLE else View.GONE
                it.data?.let {
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val preferred_sort_order = preferences.getInt(Constants.SORT_ORDER, -1)
                    if (preferred_sort_order == -1) {
                        //Preferred sort order is set on application startup
                        //It must not be -1 in any cases.
                        throw RuntimeException()
                    }
                    when (preferred_sort_order) {
                        Constants.SORT_BY_NAME -> it.sortedBy { it.title }
                        Constants.SORT_BY_RATING -> it.sortedBy { it.vote_average }
                        Constants.SORT_BY_RELEASE_DATE -> it.sortedBy { it.releaseDate }
                        else -> it.sortedBy { it.voteCount }
                    }
                    mListRecyclerViewAdapter?.setData(it)
                }
                if (isResumed) {
                    updateProgressBarVisibility()
                    updateAdapterInfoTextView()
                }
            }
        }
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

    override fun fetchNewItems() {
        //do nothing
    }

    override fun reload() {
    }

    companion object {
        private const val DIALOG_FRAGMENT = 1
    }
}