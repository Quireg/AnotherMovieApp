package com.anothermovieapp.movieDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.repository.EntityDBMovieReview

class ReviewsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected var innerData = mutableListOf<EntityDBMovieReview>()

    fun setData(newData: List<EntityDBMovieReview>?) {
        newData?.let {
            val prevSize = innerData.size
            val newSize = newData.size
            innerData.clear()
            innerData.addAll(newData)
            notifyItemRangeInserted(prevSize, newSize)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val appContext = parent.context
        val view = LayoutInflater.from(appContext)
                .inflate(R.layout.fragment_movie_review_item, parent, false)
        return MovieReviewViewHolder(view)
    }

    inner class MovieReviewViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var reviewAuthor: TextView
        var reviewContent: TextView
        override fun toString(): String {
            return super.toString() + " '" + reviewAuthor.text + "'"
        }

        init {
            reviewAuthor = view.findViewById(R.id.review_author)
            reviewContent = view.findViewById(R.id.review_content)
        }
    }

    companion object {
        private val LOG_TAG = ReviewsRecyclerViewAdapter::class.java.simpleName
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movieHolder = holder as MovieReviewViewHolder
        val movie_review: EntityDBMovieReview = innerData[position]
        movieHolder.reviewAuthor.text = movie_review.author
        movieHolder.reviewContent.text = movie_review.content
    }

    override fun getItemCount(): Int {
        return innerData.size
    }

}