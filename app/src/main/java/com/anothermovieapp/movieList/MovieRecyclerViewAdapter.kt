package com.anothermovieapp.movieList

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.*
import com.anothermovieapp.interfaces.OnFragmentInteractionListener
import com.anothermovieapp.repository.Movie
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MovieRecyclerViewAdapter(private var mContext: Context?, c: Cursor?, flags: Int, tag: String?) : CursorRecyclerViewAdapter<RecyclerView.ViewHolder>(c) {
    private val mListener: OnFragmentInteractionListener?
    private val TAG: String?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, cursor: Cursor?) {
        val movie: Movie = Movie.Companion.fromCursor(cursor)
        val movieHolder = holder as MovieViewHolder
        movieHolder.item = movie
        movieHolder.movieTitle.text = movie.originalTitle
        val uri = UriHelper.getImageUri(movie.posterPath, Constants.IMAGE_SIZE_W185)
        Log.d(TAG, "Fetching: $uri")
        GlideApp.with(movieHolder.itemView.context)
                .asBitmap()
                .load(uri)
                .placeholder(R.drawable.poster_sample)
                .addListener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any,
                                              target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any,
                                                 target: Target<Bitmap?>, dataSource: DataSource,
                                                 isFirstResource: Boolean): Boolean {
                        movieHolder.movieThumbnail.setImageBitmap(resource)
                        return true
                    }
                })
                .submit()
        holder.itemView.setOnClickListener { mListener?.onFragmentMessage(TAG, movie) }
    }

    inner class MovieViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val movieTitle: TextView
        val movieThumbnail: ImageView
        var item: Movie? = null
        override fun toString(): String {
            return super.toString() + " '" + movieTitle.text + "'"
        }

        init {
            movieTitle = view.findViewById(R.id.movie_title)
            movieThumbnail = view.findViewById(R.id.image)
            val displayMetrics = view.context.resources.displayMetrics
            view.minimumWidth = (displayMetrics.widthPixels / displayMetrics.density / 2).toInt()
            view.minimumHeight = (displayMetrics.widthPixels / displayMetrics.density / 2 * 1.5).toInt()
        }
    }

    companion object {
        private val LOG_TAG = MovieRecyclerViewAdapter::class.java.simpleName
    }

    init {
        mListener = mContext as OnFragmentInteractionListener?
        TAG = tag
    }
}