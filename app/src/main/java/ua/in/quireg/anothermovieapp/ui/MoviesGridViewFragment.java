package ua.in.quireg.anothermovieapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.adapters.MovieRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;

public abstract class MoviesGridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected RecyclerView recyclerView;
    protected CursorRecyclerViewAdapter recyclerViewAdapter;
    protected int mPosition = RecyclerView.NO_POSITION;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private String mFragmentTag;

    protected View loadingView = null;
    protected View progressBarView = null;
    protected View noFavouritesMoviesView = null;
    protected TextView pageNumberAndTotal = null;

    private final int LOAD_ITEMS_THRESHOLD = 10;

    public static final String FETCH_IN_PROGRESS = "mFetchInProgress";
    public static final String LAST_LOADED_PAGE = "mLastLoadedPage";
    public static final String CURRENT_ITEM = "mCurrentItem";
    public static final String TOTAL_ITEMS = "mTotalItems";

    public MoviesGridViewFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mFragmentTag = (String) getArguments().getSerializable(Constants.FRAGMENT_TAG);
        recyclerViewAdapter = new MovieRecyclerViewAdapter(getActivity(), null, 0, mFragmentTag);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((mMessageReceiver), new IntentFilter(Constants.SYNC_MOVIE_UPDATES_FILTER));
        fetchNewItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        noFavouritesMoviesView = view.findViewById(R.id.favourites_no_movies);
        pageNumberAndTotal = (TextView) container.getRootView().findViewById(R.id.pageNumberAndTotal);
        pageNumberAndTotal.setVisibility(View.GONE);
        progressBarView = container.getRootView().findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);
        loadingView = view.findViewById(R.id.loading);

        // Set the adapter
        final GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), Constants.COLUMNS_NUMBER);
        recyclerView.setLayoutManager(layoutManager);
        setCurrentPosition(layoutManager.findLastVisibleItemPosition());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                //scrolled down
                if (dy > 0) {

                    setCurrentPosition(((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition());
                    updateAdapterInfoTextView();

                    int itemsInAdapter = recyclerView.getAdapter().getItemCount();
                    //check if we have reached the end
                    if (itemsInAdapter - getCurrentPosition() < LOAD_ITEMS_THRESHOLD) {
                        fetchNewItems();
                    }
                } else if (dy < 0) {
                    setCurrentPosition(((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition());
                    updateAdapterInfoTextView();
                }
            }
        });
        return view;
    }

    public abstract void fetchNewItems();

    public void updateProgressBarVisibility(){
        if(progressBarView == null){
            return;
        }
        if(ismFetchInProgress()){
            progressBarView.setVisibility(View.VISIBLE);
        }else{
            progressBarView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };

    protected abstract void handleMessage(Intent msg);

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
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    protected void updateAdapterInfoTextView() {
        if(getmTotalItems() == 0){
            pageNumberAndTotal.setVisibility(View.GONE);
        }else{
            pageNumberAndTotal.setVisibility(View.VISIBLE);
            pageNumberAndTotal.setText((getCurrentPosition() + 1) + "/" + getmTotalItems());
        }
    }

    protected abstract long getCurrentPosition();

    protected abstract void setCurrentPosition(long position);

    protected abstract boolean ismFetchInProgress();

    protected abstract void setmFetchInProgress(boolean mFetchInProgress);

    protected abstract long getmLastLoadedPage();

    protected abstract void setmLastLoadedPage(long mLastLoadedPage);

    protected abstract long getmTotalItems();

    protected abstract void setmTotalItems(long mTotalItems);
}
