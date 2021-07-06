package com.anothermovieapp.movieDetails

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anothermovieapp.R
import com.anothermovieapp.common.CursorRecyclerViewAdapter
import com.anothermovieapp.repository.MovieReview

class ReviewsRecyclerViewAdapter : CursorRecyclerViewAdapter<RecyclerView.ViewHolder>(null) {
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, cursor: Cursor?) {
        val movieHolder = viewHolder as MovieReviewViewHolder
        val movie_review: MovieReview = MovieReview.Companion.fromCursor(cursor)
        movieHolder.reviewAuthor.text = movie_review.author
        movieHolder.reviewContent.text = movie_review.content
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
}