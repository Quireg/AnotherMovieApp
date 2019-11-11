package ua.in.quireg.anothermovieapp.movieDetails;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract.FavouriteMovies;

public class MovieActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String LOG_TAG = MovieActivity.class.getSimpleName();

    private DetailsFragment mDetailsFragment;
    private MovieItem mMovie;
    private CollapsingToolbarLayout mToolbarLayout;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovie = (MovieItem) getIntent().getExtras().getSerializable(Constants.MOVIE);

        setContentView(R.layout.activity_movie_details_mine);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        mToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar);

        mDetailsFragment = new DetailsFragment();
        mDetailsFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mDetailsFragment, DetailsFragment.TAG)
                .commit();

        if (mMovie != null) {
            if (mMovie.getTitle() != null) {
                CollapsingToolbarLayout collapsingTBL = findViewById(R.id.collapsing_toolbar);
                collapsingTBL.setTitle(mMovie.getTitle());
            }
            if (mMovie.getBackdropPath() != null) {
                loadBackdropImage(mMovie.getBackdropPath());
            }
        }
        appBarLayout.setBackgroundColor(Color.parseColor("#8C000000"));
        appBarLayout.setOnApplyWindowInsetsListener(
                new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        toolbar.setPadding(
                                v.getPaddingLeft(),
                                insets.getSystemWindowInsetTop(),
                                v.getPaddingRight(),
                                v.getPaddingBottom());

                        findViewById(R.id.container).setPadding(
                                v.getPaddingLeft(),
                                v.getPaddingTop(),
                                v.getPaddingRight(),
                                insets.getSystemWindowInsetBottom());

                        TypedArray a = obtainStyledAttributes(new int[]{R.attr.actionBarSize});
                        int actionBarSize = a.getDimensionPixelSize(0, -1);
                        a.recycle();

                        mToolbarLayout.setScrimVisibleHeightTrigger(
                                insets.getSystemWindowInsetTop() + actionBarSize + 1);
                        return insets;
                    }
                });

        SyncReviewsService.startActionFetchReviews(this, String.valueOf(mMovie.getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        Drawable d;

        if (isFavourite(mMovie)) {
            d = getDrawable(R.drawable.ic_favorite_red_24dp);
        } else {
            d = getDrawable(R.drawable.ic_favorite_black_24dp);
        }
        menu.findItem(R.id.favorite).setIcon(d);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favorite) {
            onFragmentMessage(
                    isFavourite(mMovie)
                            ? Constants.REMOVE_FROM_FAVOURITES
                            : Constants.ADD_TO_FAVOURITES, mMovie);
            Drawable d;
            if (isFavourite(mMovie)) {
                d = getDrawable(R.drawable.ic_favorite_red_24dp);
            } else {
                d = getDrawable(R.drawable.ic_favorite_black_24dp);
            }

            item.setIcon(d);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFavourite(MovieItem item) {
        try (Cursor c = getContentResolver().query(
                MovieDatabaseContract.FavouriteMovies.buildUri(item.getId()),
                null,
                null,
                null,
                null)) {
            return c != null && c.moveToFirst();
        }
    }

    @Override
    public void onFragmentMessage(String tag, Object data) {
        MovieItem item = (MovieItem) data;
        switch (tag) {
            case Constants.ADD_TO_FAVOURITES:
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavouriteMovies._ID, item.getId());
                getContentResolver().insert(FavouriteMovies.CONTENT_URI, contentValues);
                break;
            case Constants.REMOVE_FROM_FAVOURITES:
                getContentResolver().delete(
                        FavouriteMovies.buildUri(item.getId()), null, null);
                break;
        }
    }

    private void loadBackdropImage(String path) {
        Uri imageUri = UriHelper.getImageUri(path, Constants.IMAGE_SIZE_ORIGINAL);
        Log.d(LOG_TAG, "Fetching: " + imageUri.toString());
        final ImageView headerImage = findViewById(R.id.toolbar_image);

        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target,
                                                boolean isFirstResource) {
                        if (mDetailsFragment != null) {
                            mDetailsFragment.readyShowContent();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Bitmap resource, Object model,
                                                   Target<Bitmap> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        if (mDetailsFragment != null) {
                            mDetailsFragment.readyShowContent();
                        }
                        headerImage.setImageBitmap(resource);
                        ObjectAnimator animator =
                                ObjectAnimator.ofFloat(headerImage, View.ALPHA,
                                        headerImage.getAlpha(), 1f);
                        animator.setDuration(600);
                        animator.start();

                        return true;
                    }
                })
                .submit();
    }

    private void playWithPalette(final Bitmap resource, final CollapsingToolbarLayout collapsingTBL,
                                 final ImageView headerImage) {
        Palette.from(Bitmap.createBitmap(resource)).generate(
                new Palette.PaletteAsyncListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onGenerated(Palette palette) {
//                        FloatingActionButton fab = findViewById(R.id.fab);
//                        Drawable drawable = getResources()
//                                .getDrawable(R.drawable.ic_action_star, getTheme());
//
//                        drawable.setTintMode(PorterDuff.Mode.SRC_IN);
//                        drawable.setTintList(new ColorStateList(
//                                new int[][]{
//                                        new int[]{android.R.attr.state_selected},
//                                        new int[]{-android.R.attr.state_selected}
//                                },
//                                new int[]{
//                                        Color.RED,
//                                        Color.WHITE
//                                }
//                        ));
//                        fab.setImageDrawable(drawable);
//                        fab.setBackgroundTintList(ColorStateList.valueOf(
//                                palette.getMutedColor(R.attr.colorSecondary)));
//                        fab.setRippleColor(ColorStateList.valueOf(
//                                palette.getDarkMutedColor(R.attr.colorSecondary)));

                        if (palette.getDominantSwatch() != null) {
                            collapsingTBL.setExpandedTitleColor(
                                    palette.getDominantSwatch().getTitleTextColor());
                        }

                        if (palette.getVibrantSwatch() != null) {
                            collapsingTBL.setCollapsedTitleTextColor(
                                    palette.getVibrantSwatch().getTitleTextColor());
                            collapsingTBL.setContentScrimColor(
                                    palette.getVibrantSwatch().getRgb());
                        }

                        Toolbar toolbar = findViewById(R.id.toolbar);
                        if (toolbar.getNavigationIcon() != null) {
                            toolbar.getNavigationIcon().setTint(
                                    palette.getLightVibrantColor(R.attr.editTextColor));
                        }

                        collapsingTBL.setContentScrimColor(
                                palette.getMutedColor(R.attr.colorPrimary));
                        collapsingTBL.setStatusBarScrimColor(
                                palette.getDarkMutedColor(R.attr.colorPrimaryDark));

                        headerImage.setImageBitmap(resource);
                        headerImage.setVisibility(View.VISIBLE);
                    }
                });
    }
}
