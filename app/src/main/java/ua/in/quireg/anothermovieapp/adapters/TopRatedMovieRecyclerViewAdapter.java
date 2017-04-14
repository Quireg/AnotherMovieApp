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

import java.util.ArrayList;
import java.util.List;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.helpers.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MLog;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.ui.TopRatedMovieFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ua.in.quireg.anothermovieapp.core.MovieItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */

public class TopRatedMovieRecyclerViewAdapter extends CursorRecyclerViewAdapter<TopRatedMovieRecyclerViewAdapter.ViewHolder> {
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

//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        Uri uri = UriHelper.getImageUri(mValues.get(position).getImageFullSize(), Constants.IMAGE_SIZE_W185);
//        MLog.d(LOG_TAG, "Fetching: " + uri.toString());
//        Picasso.with(appContext).load(uri).into(holder.mImageView);
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        if(mValues == null){
//            return 0;
//        }
//        return mValues.size();
//    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        MovieItem movie = MovieItem.fromCursor(cursor);
        holder.mItem = movie;
        holder.mMovieTitle.setText(movie.getOriginalTitle());

        Uri uri = UriHelper.getImageUri(movie.getPosterPath(), Constants.IMAGE_SIZE_W185);
        MLog.d(LOG_TAG, "Fetching: " + uri.toString());
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


//    public void addAll(List<MovieItem> items){
//        if(items != null){
//            mValues.clear();
//            mValues.addAll(items);
//        }
//    }

//    public String getType(){
//        return this.tag;
//    }


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
