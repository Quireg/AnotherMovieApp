package ua.in.quireg.anothermovieapp.movieList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.CursorRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;

public abstract class MoviesGridViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mRecyclerView;
    CursorRecyclerViewAdapter mRecyclerViewAdapter;
    int mPosition = RecyclerView.NO_POSITION;
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private String mFragmentTag;

    View mLoadingView;
    private View mProgressBarView;
    private TextView mPageNumberAndTotal;

    private int mSystemInsetTop = -1;
    private int mSystemInsetBottom = -1;

    private final int LOAD_ITEMS_THRESHOLD = 20;

    static final String FETCH_IN_PROGRESS = "mFetchInProgress";
    static final String LAST_LOADED_PAGE = "mLastLoadedPage";
    static final String CURRENT_ITEM = "mCurrentItem";
    static final String TOTAL_ITEMS = "mTotalItems";

    public MoviesGridViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null) {
            mFragmentTag = (String) getArguments().getSerializable(Constants.FRAGMENT_TAG);
            mSystemInsetTop = (int) getArguments().getSerializable(Constants.INSET_TOP);
            mSystemInsetBottom = (int) getArguments().getSerializable(Constants.INSET_BOT);
        }
        mRecyclerViewAdapter = new MovieRecyclerViewAdapter(mContext, null, 0, mFragmentTag);
        LocalBroadcastManager.getInstance(mContext).registerReceiver((mMessageReceiver),
                new IntentFilter(Constants.SYNC_MOVIE_UPDATES_FILTER));
        fetchNewItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mPageNumberAndTotal = container.getRootView().findViewById(R.id.pageNumberAndTotal);
        mPageNumberAndTotal.setVisibility(View.GONE);
        mProgressBarView = container.getRootView().findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.movie_list_recycler_view);

        TypedArray a = mContext.obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int actionBarSize = a.getDimensionPixelSize(0, -1);
        a.recycle();

        mRecyclerView.setPadding(0, mSystemInsetTop + actionBarSize,
                0 , mSystemInsetBottom);

        mRecyclerView.setClipToPadding(false);
        mLoadingView = view.findViewById(R.id.loadingView);
        // Set the adapter
        final GridLayoutManager layoutManager =
                new GridLayoutManager(view.getContext(), Constants.COLUMNS_NUMBER);
        mRecyclerView.setLayoutManager(layoutManager);
        setCurrentPosition(layoutManager.findLastVisibleItemPosition());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                //scrolled down
                if (dy > 0) {

                    setCurrentPosition(((GridLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition());
                    updateAdapterInfoTextView();

                    int itemsInAdapter = recyclerView.getAdapter().getItemCount();
                    //check if we have reached the end
                    if (itemsInAdapter - getCurrentPosition() < LOAD_ITEMS_THRESHOLD) {
                        fetchNewItems();
                    }
                } else if (dy < 0) {
                    setCurrentPosition(((GridLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition());
                    updateAdapterInfoTextView();
                }
            }
        });
        return view;
    }

    public abstract void fetchNewItems();

    void updateProgressBarVisibility() {
        if (mProgressBarView == null) {
            return;
        }
        if (isFetchInProgress()) {
            mProgressBarView.setVisibility(View.VISIBLE);
        } else {
            mProgressBarView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext()))
                .unregisterReceiver(mMessageReceiver);
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
    public void onAttach(@NonNull Context context) {
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
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @SuppressLint("SetTextI18n")
    void updateAdapterInfoTextView() {
        if (mPageNumberAndTotal == null) {
            return;
        }
        if (getTotalItems() == 0) {
            mPageNumberAndTotal.setVisibility(View.GONE);
        } else {
            mPageNumberAndTotal.setVisibility(View.VISIBLE);
            mPageNumberAndTotal.setText((getCurrentPosition() + 1) + "/" + getTotalItems());
        }
    }

    protected abstract long getCurrentPosition();

    protected abstract void setCurrentPosition(long position);

    protected abstract boolean isFetchInProgress();

    protected abstract void setFetchInProgress(boolean fetchInProgress);

    protected abstract long getLastLoadedPage();

    protected abstract void setLastLoadedPage(long lastLoadedPage);

    protected abstract long getTotalItems();

    protected abstract void setTotalItems(long totalItems);
}
