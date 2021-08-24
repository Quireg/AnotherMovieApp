/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.common.GlideApp
import com.anothermovieapp.viewmodel.ViewModelMovieDetails
import com.anothermovieapp.repository.EntityDBMovie
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
class DetailsFragment : Fragment() {
    private lateinit var mMovie: EntityDBMovie
    private var mListener: OnFragmentInteractionListener? = null
    private var mMovieTrailerLinearLayout: LinearLayout? = null
    private var mMovieNoTrailersLinearLayout: LinearLayout? = null
    private var mIsReadyToShowContent = false
    private var mFetchInProgress = false
    private var mLastLoadedPage: Long = 0
    private var mTotalReviewPages: Long = 1
    private var mReviewsRecyclerViewAdapter: ReviewsRecyclerViewAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var noReviewsLayout: TextView? = null

    @Inject
    lateinit var viewModel: ViewModelMovieDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val uri = Uri.Builder()
            .scheme("https")
            .authority("image.tmdb.org")
            .appendPath("t")
            .appendPath("p")
            .appendPath(Constants.IMAGE_SIZE_W185)
            .appendPath(mMovie.posterPath)
            .build()
            .normalizeScheme();
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
        mMovieNoTrailersLinearLayout = view.findViewById(R.id.movie_trailers_layout_no_trailers)
        initReviews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setMovieId(mMovie.id)
        viewModel.trailers.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).post {
                it.data?.let {
                    onTrailersFetchCompleted(it)
                }
            }
        }
        viewModel.reviews.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).post {
                it.data?.let {
                    mReviewsRecyclerViewAdapter?.setData(it.data)
                    if (it.data != null && !it.data!!.isEmpty()) {
                        recyclerView?.visibility = View.VISIBLE
                        noReviewsLayout?.visibility = View.GONE
                    } else {
                        recyclerView?.visibility = View.GONE
                        noReviewsLayout?.visibility = View.VISIBLE
                    }
                }
            }
        }
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

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
        if (trailers.isEmpty()) {
            mMovieTrailerLinearLayout!!.removeAllViews()
        } else {
            for (trailer in trailers) {
                val single_trailer_view = LayoutInflater.from(context)
                    .inflate(R.layout.movie_trailer_list_item, mMovieTrailerLinearLayout, false)
                val trailer_name = single_trailer_view.findViewById<TextView>(R.id.trailer_title)
                val trailer_quality =
                    single_trailer_view.findViewById<TextView>(R.id.trailer_quality)
                val trailer_preview =
                    single_trailer_view.findViewById<ImageView>(R.id.trailer_preview)
                Picasso.get()
                    .load(
                        Uri.Builder()
                            .scheme("https")
                            .authority("i1.ytimg.com")
                            .appendPath("vi")
                            .appendPath(trailer.key)
                            .appendPath("hqdefault.jpg")
                            .build()
                    )
                    .into(trailer_preview)
                trailer_name.text = trailer.name
                trailer_quality.text =
                    String.format(getString(R.string.ui_details_trailer_quality), trailer.size)
                single_trailer_view.setOnClickListener {
                    val uri = Uri.Builder()
                        .scheme("https")
                        .authority("youtube.com")
                        .appendPath("watch")
                        .appendQueryParameter("v", trailer.key)
                        .build()
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                mMovieTrailerLinearLayout!!.addView(single_trailer_view)
            }
        }
        //Remove "no trailers view" and add trailers layout
        mMovieNoTrailersLinearLayout!!.visibility = View.GONE
        mMovieTrailerLinearLayout!!.visibility = View.VISIBLE
    }

    private fun initReviews(view: View) {
        recyclerView = view.findViewById(R.id.reviewsList)
        noReviewsLayout = view.findViewById(R.id.no_reviews_textview)
        val linearLayoutManager = LinearLayoutManager(recyclerView?.context)
        recyclerView?.layoutManager = linearLayoutManager
        mReviewsRecyclerViewAdapter = ReviewsRecyclerViewAdapter()
        recyclerView?.adapter = mReviewsRecyclerViewAdapter
        recyclerView?.addItemDecoration(ReviewsDividerItemDecoration(view.context))
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    companion object {
        private val LOG_TAG = DetailsFragment::class.java.simpleName
        val TAG = DetailsFragment::class.java.simpleName
        private const val FETCH_IN_PROGRESS = "fetchInProgress"
        private const val LAST_LOADED_PAGE = "lastLoadedPage"
        private const val TOTAL_REVIEW_PAGES = "totalReviewPages"
        private const val LOAD_ITEMS_THRESHOLD = 2
    }
}