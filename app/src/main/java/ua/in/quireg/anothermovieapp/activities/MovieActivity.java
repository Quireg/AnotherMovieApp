package ua.in.quireg.anothermovieapp.activities;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.async.ImageFetcher;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.GeneralUtils;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.FetchImageCallback;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.services.SyncReviewsService;
import ua.in.quireg.anothermovieapp.ui.MovieDetailsActivityFragment;
import ua.in.quireg.anothermovieapp.ui.MovieReviewFragment;

public class MovieActivity extends AppCompatActivity implements FetchImageCallback, OnFragmentInteractionListener{

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView collapsingToolbarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MovieItem movie = (MovieItem) getIntent().getExtras().getSerializable(Constants.MOVIE);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(movie.getTitle());
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //some other code here
                ViewCompat.setElevation(appBarLayout, GeneralUtils.dipToPixels(getApplicationContext(), 6));
            }
        });
        collapsingToolbarImageView = (ImageView) findViewById(R.id.toolbar_imageview);

        new ImageFetcher(this, getApplicationContext()).execute(UriHelper.getImageUri(movie.getBackdropPath(), Constants.IMAGE_SIZE_ORIGINAL));

        MovieDetailsActivityFragment movieDetailsActivityFragment = new MovieDetailsActivityFragment();
        movieDetailsActivityFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().add(R.id.container, movieDetailsActivityFragment, MovieDetailsActivityFragment.class.getSimpleName()).commit();

    }

    @Override
    public void setImage(Bitmap bitmap) {
        if (collapsingToolbarLayout != null && collapsingToolbarImageView != null) {
            collapsingToolbarImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onFragmentMessage(String tag, Object data) {
        MovieItem item = (MovieItem) data;
        switch (tag){
            case Constants.ADD_TO_FAVOURITES:
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieDatabaseContract.FavouriteMovies._ID, item.getId());
                getContentResolver().insert(MovieDatabaseContract.FavouriteMovies.CONTENT_URI, contentValues);
                break;
            case Constants.REMOVE_FROM_FAVOURITES:
                getContentResolver().delete(
                        MovieDatabaseContract.FavouriteMovies.buildUri(item.getId()),
                        null,
                        null
                );
                break;
            case Constants.OPEN_REVIEWS:
                MovieReviewFragment movieReviewFragment = MovieReviewFragment.newInstance(item.getId());
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, movieReviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                SyncReviewsService.startActionFetchReviews(this, String.valueOf(item.getId()));
                break;
        }
    }
}
