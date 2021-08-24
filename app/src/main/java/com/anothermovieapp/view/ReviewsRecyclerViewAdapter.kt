/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.repository.EntityDBMovieReview

class ReviewsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var innerData = mutableListOf<EntityDBMovieReview>()

    fun setData(newData: List<EntityDBMovieReview>?) {
        Handler(Looper.getMainLooper()).post {
            newData?.let {
                val prevSize = innerData.size
                val newSize = newData.size
                innerData.clear()
                innerData.addAll(newData)
                if (newSize > prevSize) {
                    notifyItemRangeInserted(prevSize, newSize)
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val appContext = parent.context
        val view = LayoutInflater.from(appContext)
                .inflate(R.layout.fragment_movie_review_item, parent, false)
        return MovieReviewViewHolder(view)
    }

    inner class MovieReviewViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var reviewAuthor: TextView = view.findViewById(R.id.review_author)
        var reviewContent: TextView = view.findViewById(R.id.review_content)
        override fun toString(): String {
            return super.toString() + " '" + reviewAuthor.text + "'"
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movieHolder = holder as MovieReviewViewHolder
        val movie_review = innerData[position]
        movieHolder.reviewAuthor.text = movie_review.author
        movieHolder.reviewContent.text = movie_review.content
    }

    override fun getItemCount(): Int {
        return innerData.size
    }
}