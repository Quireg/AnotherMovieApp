package com.anothermovieapp.movieDetails

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.*
import com.anothermovieapp.interfaces.OnFragmentInteractionListener
import com.anothermovieapp.managers.MovieDatabaseContract.MovieReviews
import com.anothermovieapp.managers.MovieTrailersProvider
import com.anothermovieapp.movieList.ViewModelMovieDetails
import com.anothermovieapp.movieList.ViewModelPopularMovies
import com.anothermovieapp.repository.EntityDBMovie
import com.anothermovieapp.repository.EntityDBMovieReview
import com.anothermovieapp.repository.EntityDBMovieTrailer
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var mMovie: EntityDBMovie
    private var mListener: OnFragmentInteractionListener? = null
    private var mMovieTrailerLinearLayout: LinearLayout? = null
    private var mMovieNoTrailersLinearLayout: LinearLayout? = null
    private var mIsReadyToShowContent = false
    private var mFetchInProgress = false
    private var mLastLoadedPage: Long = 0
    private var mTotalReviewPages: Long = 1
    private var mReviewsRecyclerViewAdapter: ReviewsRecyclerViewAdapter? = null

    @Inject
    lateinit var viewModel: ViewModelMovieDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mFetchInProgress = savedInstanceState.getBoolean(FETCH_IN_PROGRESS, false)
            mLastLoadedPage = savedInstanceState.getLong(LAST_LOADED_PAGE, 0)
            mTotalReviewPages = savedInstanceState.getLong(TOTAL_REVIEW_PAGES, 1)
        }
        mMovie = requireArguments().getSerializable(Constants.MOVIE) as EntityDBMovie

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_details, container, false)

        /*Set mMovie description*/
        val movie_description_textview = view.findViewById<TextView>(R.id.movie_description)
        movie_description_textview.text = mMovie.overview

        /*Set rating*/
        if (mMovie.vote_average != 0.0) {
            val movie_rating = view.findViewById<TextView>(R.id.movie_rating)
            val decimalFormat = DecimalFormat("#.##")
            val movie_rating_text = String.format(
                resources.getString(R.string.ui_details_rating_value),
                decimalFormat.format(mMovie.vote_average)
            )
            movie_rating.text = movie_rating_text
        }
        val movie_poster = view.findViewById<ImageView>(R.id.movie_poster)
        val uri = UriHelper.getImageUri(
            mMovie.posterPath,
            Constants.IMAGE_SIZE_W185
        )
        GlideApp.with(view.context)
            .asDrawable()
            .load(uri)
            .placeholder(R.drawable.poster_sample)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any,
                    target: Target<Drawable?>, isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    Handler(Looper.getMainLooper()).post {
                        movie_poster.setImageDrawable(resource)
                        val animator = ObjectAnimator.ofFloat(
                            movie_poster, View.ALPHA,
                            movie_poster.alpha, 1f
                        )
                        animator.duration = 600
                        animator.start()
                    }
                    return true
                }
            }).submit()
        val movie_original_title_textview = view.findViewById<TextView>(R.id.original_title_text)
        val movie_release_date_textview = view.findViewById<TextView>(R.id.movie_year)
        val movie_votecount_textview = view.findViewById<TextView>(R.id.movie_vote_count)
        movie_original_title_textview.text = mMovie.originalTitle
        movie_release_date_textview.text = mMovie.releaseDate
        movie_votecount_textview.text = mMovie.voteCount.toString()

        /*Trailers*/
        mMovieTrailerLinearLayout = view.findViewById(R.id.movie_trailers_layout)
//        MovieTrailersProvider.fetchTrailersList(mMovie, context, object : FetchTrailersCallback {
//            override fun onTrailersFetchCompleted(trailers: List<EntityDBMovieTrailer>) {
//                onTrailersFetchCompleted(trailers)
//            }
//        })
        mMovieNoTrailersLinearLayout = view.findViewById(R.id.movie_trailers_layout_no_trailers)
        initReviewsRecyclerView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.trailers.observe(viewLifecycleOwner) {
            it.data?.let {
                onTrailersFetchCompleted(it)
            }
        }
        viewModel.reviews.observe(viewLifecycleOwner) {
            it.data?.let {
                mReviewsRecyclerViewAdapter?.setData(it.data)
            }
        }
        viewModel.setMovieId(mMovie.id)
        if (mIsReadyToShowContent) {
            view.visibility = View.VISIBLE
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putBoolean(FETCH_IN_PROGRESS, mFetchInProgress)
        bundle.putLong(LAST_LOADED_PAGE, mLastLoadedPage)
        bundle.putLong(TOTAL_REVIEW_PAGES, mTotalReviewPages)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        LoaderManager.getInstance(this).initLoader(MOVIE_REVIEW_LOADER, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        if (context != null) {
//            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        }
        super.onDestroy()
    }

    fun readyShowContent() {
        //sync with activity to suppress content flick due to image load
        if (view != null) {
            view?.visibility = View.VISIBLE
        } else {
            mIsReadyToShowContent = true
        }
    }

    private fun onTrailersFetchCompleted(trailers: List<EntityDBMovieTrailer>) {
        if (!isAdded || trailers.isEmpty()) {
            return
        }
        for (trailer in trailers) {
            val single_trailer_view = LayoutInflater.from(context)
                .inflate(R.layout.movie_trailer_list_item, mMovieTrailerLinearLayout, false)
            val trailer_name = single_trailer_view.findViewById<TextView>(R.id.trailer_title)
            val trailer_quality = single_trailer_view.findViewById<TextView>(R.id.trailer_quality)
            val trailer_preview = single_trailer_view.findViewById<ImageView>(R.id.trailer_preview)
            Picasso.get()
                .load(UriHelper.getYouTubeTrailerPreviewLink(trailer.key))
                .into(trailer_preview)
            trailer_name.text = trailer.name
            trailer_quality.text =
                String.format(getString(R.string.ui_details_trailer_quality), trailer.size)
            single_trailer_view.setOnClickListener {
                val uri = UriHelper.getYouTubeLinkToPlay(trailer.key.toString())
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
            mMovieTrailerLinearLayout!!.addView(single_trailer_view)
        }
        //Remove "no trailers view" and add trailers layout
        mMovieNoTrailersLinearLayout!!.visibility = View.GONE
        mMovieTrailerLinearLayout!!.visibility = View.VISIBLE
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleMessage(intent)
        }
    }

    private fun handleMessage(msg: Intent) {
        if (msg.getStringExtra(Constants.FRAGMENT_TAG) != Constants.REVIEWS) {
            return
        }
        when (msg.getStringExtra(Constants.SYNC_STATUS)) {
            Constants.SYNC_COMPLETED -> {
                mLastLoadedPage = msg.getLongExtra(Constants.LOADED_PAGE, 1)
                mTotalReviewPages = msg.getLongExtra(Constants.TOTAL_PAGES, 1)
                mFetchInProgress = false
            }
            Constants.SYNC_FAILED -> {
                GeneralUtils.showToastMessage(context, getString(R.string.error_fetch_failed))
                mFetchInProgress = false
            }
            else -> {
            }
        }
    }

    private fun initReviewsRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.reviewsList)
        val linearLayoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.layoutManager = linearLayoutManager
        mReviewsRecyclerViewAdapter = ReviewsRecyclerViewAdapter()
        recyclerView.adapter = mReviewsRecyclerViewAdapter
        recyclerView.addItemDecoration(ReviewsDividerItemDecoration(view.context))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //scrolled down
                if (dy > 0) {
                    val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    val totalItems = mReviewsRecyclerViewAdapter!!.itemCount
                    if (lastVisibleItem + LOAD_ITEMS_THRESHOLD >= totalItems &&
                        !mFetchInProgress && mLastLoadedPage < mTotalReviewPages
                    ) {
                        //here
                        mFetchInProgress = true
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = MovieReviews.buildUri(mMovie.id)
        return CursorLoader(
            requireContext(),
            uri,
            EntityDBMovieReview.Companion.MOVIES_REVIEWS_CLOMUNS,
            null,
            null,
            null
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (data.moveToFirst() && view != null && isAdded) {
            view?.findViewById<View>(R.id.no_reviews_textview)?.visibility = View.GONE
            view?.findViewById<View>(R.id.reviewsList)?.visibility = View.VISIBLE
        }
        if (mReviewsRecyclerViewAdapter != null) {
//            mReviewsRecyclerViewAdapter!!.swapCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    companion object {
        private val LOG_TAG = DetailsFragment::class.java.simpleName
        val TAG = DetailsFragment::class.java.simpleName
        private const val FETCH_IN_PROGRESS = "fetchInProgress"
        private const val LAST_LOADED_PAGE = "lastLoadedPage"
        private const val TOTAL_REVIEW_PAGES = "totalReviewPages"
        private const val MOVIE_REVIEW_LOADER = 4
        private const val LOAD_ITEMS_THRESHOLD = 2
    }
}