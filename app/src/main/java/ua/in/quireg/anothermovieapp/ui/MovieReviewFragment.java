package ua.in.quireg.anothermovieapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.MovieReviewsRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.MovieReview;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.services.SyncReviewsService;


public class MovieReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MOVIE_REVIEW_LOADER = 4;
    private final int LOAD_ITEMS_THRESHOLD = 2;

    private long mMovieId;
    private boolean mFetchInProgress = false;
    private long mLastLoadedPage = 0;
    private long mTotalReviewPages = 1;
    private OnFragmentInteractionListener mListener;
    private MovieReviewsRecyclerViewAdapter mMovieReviewsRecyclerViewAdapter;


    public MovieReviewFragment() {
    }

    @SuppressWarnings("unused")
    public static MovieReviewFragment newInstance(long movieId) {
        MovieReviewFragment fragment = new MovieReviewFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.MOVIE, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getLong(Constants.MOVIE);
        }
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((mMessageReceiver), new IntentFilter(Constants.SYNC_REVIEWS_UPDATES_FILTER));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };

    protected void handleMessage(Intent msg) {
        if(!msg.getStringExtra(Constants.FRAGMENT_TAG).equals(Constants.REVIEWS)){
            return;
        }
        switch (msg.getStringExtra(Constants.SYNC_STATUS)) {
            case Constants.SYNC_COMPLETED:
                this.mLastLoadedPage =  msg.getLongExtra(Constants.LOADED_PAGE, 1);
                this.mTotalReviewPages = msg.getLongExtra(Constants.TOTAL_PAGES, 1);
                mFetchInProgress = false;
                break;
            case Constants.SYNC_FAILED:
                GeneralUtils.showToastMessage(getContext(), getString(R.string.error_fetch_failed));
                mFetchInProgress = false;
                break;
            default:
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_review_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            mMovieReviewsRecyclerViewAdapter = new MovieReviewsRecyclerViewAdapter(getContext(), null);
            recyclerView.setAdapter(mMovieReviewsRecyclerViewAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    //scrolled down
                    if(dy > 0){
                        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        int totalItems = mMovieReviewsRecyclerViewAdapter.getItemCount();
                        if(lastVisibleItem + LOAD_ITEMS_THRESHOLD >= totalItems && !mFetchInProgress && mLastLoadedPage < mTotalReviewPages){
                            SyncReviewsService.startActionFetchReviewsForPage(getContext(), String.valueOf(mMovieId), String.valueOf(mLastLoadedPage + 1));
                            mFetchInProgress = true;
                        }
                    }

                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieDatabaseContract.MovieReviews.buildUri(mMovieId);

        return new CursorLoader(getActivity(),
                uri,
                MovieReview.MOVIES_REVIEWS_CLOMUNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieReviewsRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
