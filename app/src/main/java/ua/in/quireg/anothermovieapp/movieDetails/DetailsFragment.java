package ua.in.quireg.anothermovieapp.movieDetails;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.common.MovieReview;
import ua.in.quireg.anothermovieapp.common.MovieTrailer;
import ua.in.quireg.anothermovieapp.common.ReviewsDividerItemDecoration;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.managers.MovieTrailersProvider;
import ua.in.quireg.anothermovieapp.common.GlideApp;

public class DetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    public static final String TAG = DetailsFragment.class.getSimpleName();

    private MovieItem mMovie;

    private OnFragmentInteractionListener mListener;
    private LinearLayout mMovieTrailerLinearLayout;
    private LinearLayout mMovieNoTrailersLinearLayout;

    private static final String FETCH_IN_PROGRESS = "fetchInProgress";
    private static final String LAST_LOADED_PAGE = "lastLoadedPage";
    private static final String TOTAL_REVIEW_PAGES = "totalReviewPages";
    private static final int MOVIE_REVIEW_LOADER = 4;
    private static final int LOAD_ITEMS_THRESHOLD = 2;

    private boolean mIsReadyToShowContent = false;
    private boolean mFetchInProgress = false;
    private long mLastLoadedPage = 0;
    private long mTotalReviewPages = 1;
    private ReviewsRecyclerViewAdapter mReviewsRecyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            super.onCreate(savedInstanceState);
            mFetchInProgress = savedInstanceState.getBoolean(FETCH_IN_PROGRESS, false);
            mLastLoadedPage = savedInstanceState.getLong(LAST_LOADED_PAGE, 0);
            mTotalReviewPages = savedInstanceState.getLong(TOTAL_REVIEW_PAGES, 1);
        }
        if (getArguments() != null) {
            mMovie = (MovieItem) getArguments().getSerializable(Constants.MOVIE);
        }
        LocalBroadcastManager.getInstance(
                Objects.requireNonNull(getContext())).registerReceiver((mMessageReceiver),
                new IntentFilter(Constants.SYNC_REVIEWS_UPDATES_FILTER));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        /*Set mMovie description*/
        TextView movie_description_textview = view.findViewById(R.id.movie_description);
        movie_description_textview.setText(mMovie.getOverview());

        /*Set rating*/
        if (mMovie.getVote_average() != 0) {
            TextView movie_rating = view.findViewById(R.id.movie_rating);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String movie_rating_text =
                    String.format(getResources().getString(R.string.ui_details_rating_value), decimalFormat.format(mMovie.getVote_average()));
            movie_rating.setText(movie_rating_text);
        }
        final ImageView movie_poster = view.findViewById(R.id.movie_poster);
        Uri uri = UriHelper.getImageUri(mMovie.getPosterPath(),
                Constants.IMAGE_SIZE_W185);

        GlideApp.with(view.getContext())
                .asDrawable()
                .load(uri)
                .placeholder(R.drawable.poster_sample)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        movie_poster.setImageDrawable(resource);
                        ObjectAnimator animator =
                                ObjectAnimator.ofFloat(movie_poster, View.ALPHA,
                                        movie_poster.getAlpha(), 1f);
                        animator.setDuration(600);
                        animator.start();
                        return true;
                    }
                }).submit();

        TextView movie_original_title_textview = view.findViewById(R.id.original_title_text);
        TextView movie_release_date_textview = view.findViewById(R.id.movie_year);
        TextView movie_votecount_textview = view.findViewById(R.id.movie_vote_count);
        movie_original_title_textview.setText(mMovie.getOriginalTitle());
        movie_release_date_textview.setText(mMovie.getReleaseDate());
        movie_votecount_textview.setText(String.valueOf(mMovie.getVoteCount()));

        /*Trailers*/
        mMovieTrailerLinearLayout = view.findViewById(R.id.movie_trailers_layout);
        MovieTrailersProvider.fetchTrailersList(mMovie, getContext(), new FetchTrailersCallback() {
            @Override
            public void onTrailersFetchCompleted(List<MovieTrailer> trailers) {
                DetailsFragment.this.onTrailersFetchCompleted(trailers);
            }
        });
        mMovieNoTrailersLinearLayout = view.findViewById(R.id.movie_trailers_layout_no_trailers);

        initReviewsRecyclerView(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mIsReadyToShowContent) {
            view.setVisibility(View.VISIBLE);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(FETCH_IN_PROGRESS, mFetchInProgress);
        bundle.putLong(LAST_LOADED_PAGE, mLastLoadedPage);
        bundle.putLong(TOTAL_REVIEW_PAGES, mTotalReviewPages);
    }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        LoaderManager.getInstance(this).initLoader(MOVIE_REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        }
        super.onDestroy();
    }


    public void readyShowContent() {
        //sync with activity to suppress content flick due to image load
        if (getView() != null) {
            getView().setVisibility(View.VISIBLE);
        } else {
            mIsReadyToShowContent = true;
        }
    }

    private void onTrailersFetchCompleted(List<MovieTrailer> trailers) {
        if (!isAdded() || trailers.isEmpty()) {
            return;
        }
        for (final MovieTrailer trailer : trailers) {
            View single_trailer_view = LayoutInflater.from(getContext())
                    .inflate(R.layout.movie_trailer_list_item, mMovieTrailerLinearLayout, false);

            TextView trailer_name = single_trailer_view.findViewById(R.id.trailer_title);
            TextView trailer_quality = single_trailer_view.findViewById(R.id.trailer_quality);
            ImageView trailer_preview = single_trailer_view.findViewById(R.id.trailer_preview);

            Picasso.get()
                    .load(UriHelper.getYouTubeTrailerPreviewLink(trailer.key))
                    .into(trailer_preview);

            trailer_name.setText(trailer.name);
            trailer_quality.setText(String.format(getString(R.string.ui_details_trailer_quality), trailer.size));
            single_trailer_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = UriHelper.getYouTubeLinkToPlay(String.valueOf(trailer.key));
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });
            mMovieTrailerLinearLayout.addView(single_trailer_view);
        }
        //Remove "no trailers view" and add trailers layout
        mMovieNoTrailersLinearLayout.setVisibility(View.GONE);
        mMovieTrailerLinearLayout.setVisibility(View.VISIBLE);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };

    private void handleMessage(Intent msg) {
        if (!msg.getStringExtra(Constants.FRAGMENT_TAG).equals(Constants.REVIEWS)) {
            return;
        }
        switch (msg.getStringExtra(Constants.SYNC_STATUS)) {
            case Constants.SYNC_COMPLETED:
                mLastLoadedPage = msg.getLongExtra(Constants.LOADED_PAGE, 1);
                mTotalReviewPages = msg.getLongExtra(Constants.TOTAL_PAGES, 1);
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

    private void initReviewsRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.reviewsList);
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mReviewsRecyclerViewAdapter = new ReviewsRecyclerViewAdapter();
        recyclerView.setAdapter(mReviewsRecyclerViewAdapter);
        recyclerView.addItemDecoration(new ReviewsDividerItemDecoration(view.getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //scrolled down
                if (dy > 0) {
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    int totalItems = mReviewsRecyclerViewAdapter.getItemCount();
                    if (lastVisibleItem + LOAD_ITEMS_THRESHOLD >= totalItems &&
                            !mFetchInProgress && mLastLoadedPage < mTotalReviewPages) {
                        SyncReviewsService.startActionFetchReviewsForPage(
                                getContext(), String.valueOf(mMovie.getId()),
                                String.valueOf(mLastLoadedPage + 1));
                        mFetchInProgress = true;
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        SyncReviewsService.startActionFetchReviews(getContext(), String.valueOf(mMovie.getId()));
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieDatabaseContract.MovieReviews.buildUri(mMovie.getId());
        return new CursorLoader(Objects.requireNonNull(getContext()),
                uri,
                MovieReview.MOVIES_REVIEWS_CLOMUNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst() && getView() != null && isAdded()) {
            getView().findViewById(R.id.no_reviews_textview).setVisibility(View.GONE);
            getView().findViewById(R.id.reviewsList).setVisibility(View.VISIBLE);
        }
        if (mReviewsRecyclerViewAdapter != null) {
            mReviewsRecyclerViewAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
