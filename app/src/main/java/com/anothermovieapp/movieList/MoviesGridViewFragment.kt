package com.anothermovieapp.movieList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.interfaces.OnFragmentInteractionListener

abstract class MoviesGridViewFragment : Fragment() {
    var mRecyclerView: RecyclerView? = null
    var mRecyclerViewAdapter: RecyclerViewAdapter? = null
    var mPosition = RecyclerView.NO_POSITION
    private var mListener: OnFragmentInteractionListener? = null
    abstract var mFragmentTag: String
    var mLoadingView: View? = null
    private var mProgressBarView: View? = null
    private var mPageNumberAndTotal: TextView? = null
    private val LOAD_ITEMS_THRESHOLD = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        mPageNumberAndTotal = container!!.rootView.findViewById(R.id.pageNumberAndTotal)
        mPageNumberAndTotal?.visibility = View.GONE
        mProgressBarView = container.rootView.findViewById(R.id.progressBar)
        mRecyclerView = view.findViewById(R.id.movie_list_recycler_view)
        val a = requireContext().obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        val actionBarSize = a.getDimensionPixelSize(0, -1)
        a.recycle()
        mRecyclerView?.setPadding(
            0, actionBarSize,
            0, 0
        )
        mRecyclerView?.clipToPadding = false
        mLoadingView = view.findViewById(R.id.loadingView)
        // Set the adapter
        val layoutManager = GridLayoutManager(view.context, Constants.COLUMNS_NUMBER)
        mRecyclerView?.layoutManager = layoutManager
        currentPosition = layoutManager.findLastVisibleItemPosition().toLong()
        mRecyclerViewAdapter = RecyclerViewAdapter(requireContext(), mFragmentTag)
        mRecyclerViewAdapter!!.listener = mListener
        mRecyclerView?.adapter = mRecyclerViewAdapter
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //scrolled down
                if (dy > 0) {
                    currentPosition = (recyclerView.layoutManager as GridLayoutManager?)
                        ?.findLastVisibleItemPosition()?.toLong()!!
                    updateAdapterInfoTextView()
                    val itemsInAdapter = recyclerView.adapter!!.itemCount
                    //check if we have reached the end
                    if (itemsInAdapter - currentPosition < LOAD_ITEMS_THRESHOLD) {
                        fetchNewItems()
                    }
                } else if (dy < 0) {
                    currentPosition = (recyclerView.layoutManager as GridLayoutManager?)
                        ?.findLastVisibleItemPosition()?.toLong()!!
                    updateAdapterInfoTextView()
                }
            }
        })
        return view
    }

    abstract fun fetchNewItems()

    abstract fun reload()

    fun fragmentListener(l: OnFragmentInteractionListener) {
        mListener = l
    }

    fun updateProgressBarVisibility() {
        mProgressBarView?.visibility = if (isFetchInProgress) View.VISIBLE else View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun updateAdapterInfoTextView() {
        if (mPageNumberAndTotal == null) {
            return
        }
        if (totalItems == 0L) {
            mPageNumberAndTotal!!.visibility = View.GONE
        } else {
            mPageNumberAndTotal!!.visibility = View.VISIBLE
            mPageNumberAndTotal!!.text = (currentPosition + 1).toString() + "/" + totalItems
        }
    }

    protected abstract var currentPosition: Long
    protected abstract var isFetchInProgress: Boolean
    protected abstract var totalItems: Long

    companion object {
        const val FETCH_IN_PROGRESS = "mFetchInProgress"
        const val LAST_LOADED_PAGE = "mLastLoadedPage"
        const val CURRENT_ITEM = "mCurrentItem"
        const val TOTAL_ITEMS = "mTotalItems"
    }
}