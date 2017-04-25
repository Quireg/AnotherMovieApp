package ua.in.quireg.anothermovieapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.helpers.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MLog;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;


public class TopRatedMovieRecyclerViewAdapter extends CursorRecyclerViewAdapter<TopRatedMovieRecyclerViewAdapter.ViewHolder>{
    private static final String LOG_TAG = TopRatedMovieRecyclerViewAdapter.class.getSimpleName();

    private final OnFragmentInteractionListener mListener;
    private Context appContext;
    private String TAG = LOG_TAG;


    public TopRatedMovieRecyclerViewAdapter(Context context, Cursor c, int flags) {
        super(context, c);
        appContext = context;
        mListener = (OnFragmentInteractionListener) context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        appContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        MLog.v(LOG_TAG, "onBindViewHolder()");
        MovieItem movie = MovieItem.fromCursor(cursor);
        holder.mItem = movie;
        holder.mMovieTitle.setText(movie.getOriginalTitle());

        Uri uri = UriHelper.getImageUri(movie.getPosterPath(), Constants.IMAGE_SIZE_W185);

        Picasso.with(appContext).load(uri).into(holder.mMovieThumbnail);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentMessage(TAG, holder.mItem);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMovieTitle;
        public final ImageView mMovieThumbnail;
        public MovieItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMovieTitle = (TextView) view.findViewById(R.id.id);
            mMovieThumbnail = (ImageView) view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMovieTitle.getText() + "'";
        }
    }


}
