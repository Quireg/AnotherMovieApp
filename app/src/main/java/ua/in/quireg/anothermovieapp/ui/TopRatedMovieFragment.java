package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.TopRatedMovieRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;


public class TopRatedMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private TopRatedMovieRecyclerViewAdapter topRatedMovieRecyclerViewAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private static final int TOP_RATED_MOVIE_LOADER = 0;


    public TopRatedMovieFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        topRatedMovieRecyclerViewAdapter = new TopRatedMovieRecyclerViewAdapter(getActivity(), null, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, Constants.COLUMN_NUMBER));
            recyclerView.setAdapter(topRatedMovieRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TOP_RATED_MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                ActionBar abar = activity.getSupportActionBar();
                if (abar != null) {
                    abar.setTitle(mContext.getResources().getString(R.string.top_rated_tab_name));
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieDatabaseContract.TopRatedMovies.COLUMN_PAGE + " ASC, " + MovieDatabaseContract.TopRatedMovies.COLUMN_POSITION + " ASC";

        return new CursorLoader(getActivity(),
                MovieDatabaseContract.TopRatedMovies.buildUri(),
                MovieItem.MOVIES_CLOMUNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        topRatedMovieRecyclerViewAdapter.swapCursor(data);
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MovieItem item);

    }
}
