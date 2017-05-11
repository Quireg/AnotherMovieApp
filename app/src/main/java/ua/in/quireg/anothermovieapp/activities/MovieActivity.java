package ua.in.quireg.anothermovieapp.activities;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.async.ImageFetcher;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.UriHelper;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.FetchImageCallback;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.managers.MovieDatabaseContract;
import ua.in.quireg.anothermovieapp.ui.MovieDetailsActivityFragment;

public class MovieActivity extends AppCompatActivity implements FetchImageCallback, OnFragmentInteractionListener{

    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MovieItem movie = (MovieItem) getIntent().getExtras().getSerializable(Constants.MOVIE);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(movie.getTitle());

        new ImageFetcher(this, getApplicationContext()).execute(UriHelper.getImageUri(movie.getBackdropPath(), Constants.IMAGE_SIZE_ORIGINAL));

        MovieDetailsActivityFragment movieDetailsActivityFragment = new MovieDetailsActivityFragment();
        movieDetailsActivityFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().add(R.id.container, movieDetailsActivityFragment, MovieDetailsActivityFragment.class.getSimpleName()).commit();

    }

    @Override
    public void setImage(Bitmap bitmap) {
        if (collapsingToolbar != null) {
            Drawable background = new BitmapDrawable(getResources(), bitmap);
            collapsingToolbar.setBackground(background);
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
        }
    }
}
