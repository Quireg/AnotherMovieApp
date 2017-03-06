package ua.in.quireg.anothermovieapp.adapters;

import android.content.Context;
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
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.ui.PopularMovieFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ua.in.quireg.anothermovieapp.core.MovieItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */

public class PopularMovieRecyclerViewAdapter extends RecyclerView.Adapter<PopularMovieRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private List<MovieItem> mValues;
    private String tag;

    private Context appContext;

    public PopularMovieRecyclerViewAdapter(OnListFragmentInteractionListener listener, String tag) {
        this.mValues = new ArrayList<>();
        this.mListener = listener;
        this.tag = tag;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        appContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
        String url = "http://image.tmdb.org/t/p/" + "w185/" + mValues.get(position).getImageFullSize();
        Picasso.with(appContext).load(url).into(holder.mImageView);

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
        if(mValues == null){
            return 0;
        }
        return mValues.size();
    }


    public void addAll(List<MovieItem> items){
        if(items != null){
            mValues.clear();
            mValues.addAll(items);
        }
    }

    public String getType(){
        return this.tag;
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
