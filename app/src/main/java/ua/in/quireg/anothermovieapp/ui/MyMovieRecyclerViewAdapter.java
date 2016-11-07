package ua.in.quireg.anothermovieapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.ui.MovieFragment.OnListFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.ui.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ua.in.quireg.anothermovieapp.core.MovieItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */ 
public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder> {

    private final List<MovieItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMovieRecyclerViewAdapter(List<MovieItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getId());
        //holder.mImageView.setImageBitmap(mValues.get(position).image);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView mImageView;
        public MovieItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mImageView = (ImageView) view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
