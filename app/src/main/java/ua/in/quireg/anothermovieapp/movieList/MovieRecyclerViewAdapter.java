package ua.in.quireg.anothermovieapp.movieList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.common.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.GlideApp;

public class MovieRecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = MovieRecyclerViewAdapter.class.getSimpleName();

    private final OnFragmentInteractionListener mListener;
    private Context mContext;
    private String TAG;

    public MovieRecyclerViewAdapter(Context context, Cursor c, int flags, String tag) {
        super(c);
        mContext = context;
        mListener = (OnFragmentInteractionListener) context;
        TAG = tag;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_movie_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        final MovieItem movie = MovieItem.fromCursor(cursor);

        final MovieViewHolder movieHolder = (MovieViewHolder) holder;
        movieHolder.item = movie;
        movieHolder.movieTitle.setText(movie.getOriginalTitle());

        Uri uri = UriHelper.getImageUri(movie.getPosterPath(), Constants.IMAGE_SIZE_W185);
        Log.d(TAG, "Fetching: " + uri.toString());

        GlideApp.with(movieHolder.itemView.getContext())
                .asBitmap()
                .load(uri)
                .placeholder(R.drawable.poster_sample)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        movieHolder.movieThumbnail.setImageBitmap(resource);
                        return true;
                    }
                })
                .submit();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentMessage(TAG, movie);
                }
            }
        });
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        final TextView movieTitle;
        final ImageView movieThumbnail;
        MovieItem item;

        MovieViewHolder(View view) {
            super(view);
            movieTitle = view.findViewById(R.id.movie_title);
            movieThumbnail = view.findViewById(R.id.image);

            DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();

            view.setMinimumWidth(
                    (int) ((displayMetrics.widthPixels / displayMetrics.density) / 2));
            view.setMinimumHeight(
                    (int) (((displayMetrics.widthPixels / displayMetrics.density) / 2) * 1.5));
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + movieTitle.getText() + "'";
        }
    }

}
