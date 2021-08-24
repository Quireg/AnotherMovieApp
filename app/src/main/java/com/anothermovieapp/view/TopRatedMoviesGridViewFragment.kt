/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.common.GeneralUtils
import com.anothermovieapp.repository.Status
import com.anothermovieapp.viewmodel.ViewModelTopRatedMovies
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TopRatedMoviesGridViewFragment : MoviesGridViewFragment() {
    override var isFetchInProgress = false
    override var currentPosition: Long = 0
    override var totalItems: Long = 0
    override var mFragmentTag: String = Constants.TOP_RATED
    override var isEmptyData = true

    @Inject
    lateinit var viewModel: ViewModelTopRatedMovies

    override fun onResume() {
        super.onResume()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.title =
            requireContext().resources.getString(R.string.top_rated_tab_name)
        updateAdapterInfoTextView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingView?.visibility = View.VISIBLE
        viewModel.topRatedMovies.observe(viewLifecycleOwner) {
            isEmptyData = it.data == null || it.data.isEmpty()
            it.data?.sortedBy { movie -> movie.vote_average }
            mLoadingView?.visibility = if (it.data == null) View.VISIBLE else View.GONE
            mListRecyclerViewAdapter?.setData(it.data)
            isFetchInProgress = it.status == Status.LOADING
            if (isResumed) {
                updateProgressBarVisibility()
                updateAdapterInfoTextView()
            }
            if (it.status == Status.ERROR) {
                GeneralUtils.showToastMessage(
                    requireContext(),
                    getString(R.string.error_fetch_failed)
                )
            }
        }
        viewModel.topRatedMoviesTotalAmount.observe(viewLifecycleOwner) {
            totalItems = it
            if (isResumed) {
                updateAdapterInfoTextView()
            }
        }
    }

    override fun fetchNewItems() {
        if (isFetchInProgress) {
            return
        }
        updateProgressBarVisibility()
        viewModel.fetchMore()
    }

    override fun reload() {
        currentPosition = 0L
        viewModel.reload()
    }
}