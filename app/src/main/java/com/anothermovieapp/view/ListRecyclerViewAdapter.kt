/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.common.GlideApp
import com.anothermovieapp.repository.EntityDBMovie
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ListRecyclerViewAdapter(var context: Context, var TAG: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: OnFragmentInteractionListener? = null
    private var _data = mutableListOf<EntityDBMovie>()

    fun setData(newData: List<EntityDBMovie>?) {
        Handler(Looper.getMainLooper()).post {
            if (newData != null && newData.isNotEmpty()) {
                val oldSize = _data.size
                val newSize = newData.size
                when {
                    oldSize - newSize == 1 -> {
                        val index = _data.indexOf(_data.minus(newData)[0])
                        _data.removeAt(index)
                        notifyItemRangeRemoved(index, 1)
                    }
                    newSize >= oldSize -> {
                        _data.clear()
                        _data.addAll(newData)
                        notifyItemRangeInserted(oldSize, newSize)
                    }
                    else -> {
                        _data.clear()
                        _data.addAll(newData)
                        notifyItemRangeRemoved(0, oldSize)
                        notifyItemRangeInserted(0, newSize)
                    }
                }
            } else {
                _data.clear()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.fragment_movie_item, parent, false)
        return MovieViewHolder(view)
    }

    inner class MovieViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val movieTitle: TextView = view.findViewById(R.id.movie_title)
        val movieThumbnail: ImageView = view.findViewById(R.id.image)
        var item: EntityDBMovie? = null
        override fun toString(): String {
            return super.toString() + " '" + movieTitle.text + "'"
        }

        init {
            val displayMetrics = view.context.resources.displayMetrics
            view.minimumWidth = (displayMetrics.widthPixels / displayMetrics.density / 2).toInt()
            view.minimumHeight =
                (displayMetrics.widthPixels / displayMetrics.density / 2 * 1.5).toInt()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie: EntityDBMovie = _data[position]
        val movieHolder = holder as MovieViewHolder
        movieHolder.item = movie
        movieHolder.movieTitle.text = movie.originalTitle
        val uri = Uri.Builder()
            .scheme("https")
            .authority("image.tmdb.org")
            .appendPath("t")
            .appendPath("p")
            .appendPath(Constants.IMAGE_SIZE_W185)
            .appendPath(movie.posterPath)
            .build()
            .normalizeScheme();
        GlideApp.with(movieHolder.itemView.context)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.poster_sample)
            .addListener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any,
                    target: Target<Bitmap?>, isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?, model: Any,
                    target: Target<Bitmap?>, dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Handler(Looper.getMainLooper()).post {
                        movieHolder.movieThumbnail.setImageBitmap(
                            resource
                        )
                    }
                    return true
                }
            })
            .submit()
        holder.itemView.setOnClickListener { listener?.onFragmentMessage(TAG, movie) }
    }

    override fun getItemCount(): Int {
        return _data.size
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }
}