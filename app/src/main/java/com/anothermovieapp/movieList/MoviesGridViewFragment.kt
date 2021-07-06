package com.anothermovieapp.movieList

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.common.CursorRecyclerViewAdapter
import com.anothermovieapp.interfaces.OnFragmentInteractionListener

abstract class MoviesGridViewFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor?> {
    var mRecyclerView: RecyclerView? = null
    var mRecyclerViewAdapter: CursorRecyclerViewAdapter<*>? = null
    var mPosition = RecyclerView.NO_POSITION
    private var mListener: OnFragmentInteractionListener? = null
    private var mContext: Context? = null
    private var mFragmentTag: String? = null
    var mLoadingView: View? = null
    private var mProgressBarView: View? = null
    private var mPageNumberAndTotal: TextView? = null
    private var mSystemInsetTop = -1
    private var mSystemInsetBottom = -1
    private val LOAD_ITEMS_THRESHOLD = 20
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = context
        if (arguments != null) {
            mFragmentTag = arguments!!.getSerializable(Constants.FRAGMENT_TAG) as String
            mSystemInsetTop = arguments!!.getSerializable(Constants.INSET_TOP) as Int
            mSystemInsetBottom = arguments!!.getSerializable(Constants.INSET_BOT) as Int
        }
        mRecyclerViewAdapter = MovieRecyclerViewAdapter(mContext, null, 0, mFragmentTag)
        LocalBroadcastManager.getInstance(mContext!!).registerReceiver(mMessageReceiver,
                IntentFilter(Constants.SYNC_MOVIE_UPDATES_FILTER))
        fetchNewItems()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        mPageNumberAndTotal = container!!.rootView.findViewById(R.id.pageNumberAndTotal)
        mPageNumberAndTotal?.visibility = View.GONE
        mProgressBarView = container.rootView.findViewById(R.id.progressBar)
        mRecyclerView = view.findViewById(R.id.movie_list_recycler_view)
        val a = mContext!!.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        val actionBarSize = a.getDimensionPixelSize(0, -1)
        a.recycle()
        mRecyclerView?.setPadding(0, mSystemInsetTop + actionBarSize,
                0, mSystemInsetBottom)
        mRecyclerView?.clipToPadding = false
        mLoadingView = view.findViewById(R.id.loadingView)
        // Set the adapter
        val layoutManager = GridLayoutManager(view.context, Constants.COLUMNS_NUMBER)
        mRecyclerView?.layoutManager = layoutManager
        currentPosition = layoutManager.findLastVisibleItemPosition().toLong()
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
    fun updateProgressBarVisibility() {
        if (mProgressBarView == null) {
            return
        }
        if (isFetchInProgress) {
            mProgressBarView!!.visibility = View.VISIBLE
        } else {
            mProgressBarView!!.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(mMessageReceiver)
        }
        super.onDestroy()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleMessage(intent)
        }
    }

    protected abstract fun handleMessage(msg: Intent)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {}

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
    protected abstract var lastLoadedPage: Long
    protected abstract var totalItems: Long

    companion object {
        const val FETCH_IN_PROGRESS = "mFetchInProgress"
        const val LAST_LOADED_PAGE = "mLastLoadedPage"
        const val CURRENT_ITEM = "mCurrentItem"
        const val TOTAL_ITEMS = "mTotalItems"
    }
}