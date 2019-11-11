package ua.in.quireg.anothermovieapp.movieDetails;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.MovieReview;

public class ReviewsRecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = ReviewsRecyclerViewAdapter.class.getSimpleName();

    public ReviewsRecyclerViewAdapter() {
        super(null);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        MovieReviewViewHolder movieHolder = (MovieReviewViewHolder) viewHolder;

        MovieReview movie_review = MovieReview.fromCursor(cursor);
        movieHolder.reviewAuthor.setText(movie_review.author);
        movieHolder.reviewContent.setText(movie_review.content);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context appContext = parent.getContext();
        View view = LayoutInflater.from(appContext)
                .inflate(R.layout.fragment_movie_review_item, parent, false);
        return new MovieReviewViewHolder(view);
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewAuthor;
        TextView reviewContent;

        MovieReviewViewHolder(View view) {
            super(view);
            reviewAuthor = view.findViewById(R.id.review_author);
            reviewContent = view.findViewById(R.id.review_content);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + reviewAuthor.getText() + "'";
        }
    }
}
