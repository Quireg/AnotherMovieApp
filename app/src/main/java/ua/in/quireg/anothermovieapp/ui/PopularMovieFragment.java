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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.PopularMovieRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;

public class PopularMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private PopularMovieRecyclerViewAdapter popularMovieRecyclerViewAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private OnFragmentInteractionListener mListener;
    private Context mContext;

    private LayoutInflater mInflater;
    private ViewGroup mContainer;

    private static final int POPULAR_MOVIE_LOADER = 0;
    private int pageNumber = 1;
    private int adapterItemCount = 0;


    public PopularMovieFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        popularMovieRecyclerViewAdapter = new PopularMovieRecyclerViewAdapter(getActivity(), null, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        mInflater = inflater;
        //View view = inflater.inflate(R.layout.fragment_no_data, container, false);
        View view = mInflater.inflate(R.layout.fragment_movie_list, mContainer, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, Constants.COLUMN_NUMBER));
            recyclerView.setAdapter(popularMovieRecyclerViewAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (recyclerView.getAdapter().getItemCount() != 0) {
                        int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1){

                            if(recyclerView.getAdapter().getItemCount() != adapterItemCount) {
                                pageNumber = pageNumber + 1;
                                adapterItemCount = recyclerView.getAdapter().getItemCount();
                                SyncMovieService.startActionFetchMovies(getContext(), Constants.POPULAR, pageNumber + "");
                            }
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                }
            });
        }
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_MOVIE_LOADER, null, this);
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
                    abar.setTitle(mContext.getResources().getString(R.string.popular_tab_name));
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
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieDatabaseContract.PopularMovies.COLUMN_PAGE + " ASC, " +
                MovieDatabaseContract.PopularMovies.COLUMN_POSITION + " ASC";

        return new CursorLoader(getActivity(),
                MovieDatabaseContract.PopularMovies.buildUri(),
                MovieItem.MOVIES_CLOMUNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        popularMovieRecyclerViewAdapter.swapCursor(data);
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
