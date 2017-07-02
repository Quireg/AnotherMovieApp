package ua.in.quireg.anothermovieapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.MovieAppLogger;
import ua.in.quireg.anothermovieapp.common.MovieReview;


public class MovieReviewsRecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = MovieReviewsRecyclerViewAdapter.class.getSimpleName();

    public MovieReviewsRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        MovieAppLogger.v(LOG_TAG, "onBindViewHolder()");

        MovieReview movie_review = MovieReview.fromCursor(cursor);
        ((MovieReviewViewHolder) viewHolder).mReviewAuthor.setText(movie_review.author);
        ((MovieReviewViewHolder) viewHolder).mReviewContent.setText(movie_review.content);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MovieAppLogger.v(LOG_TAG, "onCreateViewHolder()");
        Context appContext = parent.getContext();
        View view = LayoutInflater.from(appContext)
                .inflate(R.layout.fragment_movie_review, parent, false);

        return new MovieReviewViewHolder(view);
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mReviewAuthor;
        public final TextView mReviewContent;
        public MovieReview item;

        public MovieReviewViewHolder(View view) {
            super(view);
            mView = view;
            mReviewAuthor = (TextView) view.findViewById(R.id.review_author);
            mReviewContent = (TextView) view.findViewById(R.id.review_content);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mReviewAuthor.getText() + "'";
        }
    }
}
