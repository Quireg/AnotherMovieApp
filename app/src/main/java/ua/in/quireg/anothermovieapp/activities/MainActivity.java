package ua.in.quireg.anothermovieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;
import ua.in.quireg.anothermovieapp.ui.FavouritesMoviesGridViewFragment;
import ua.in.quireg.anothermovieapp.ui.MoviesGridViewFragment;
import ua.in.quireg.anothermovieapp.ui.PopularMoviesGridViewFragment;
import ua.in.quireg.anothermovieapp.ui.TopRatedMoviesGridViewFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int POPULAR_MOVIES_TAB_POSITION = 0;
    private static final int TOP_RATED_MOVIES_TAB_POSITION = 1;
    private static final int FAVOURITE_MOVIES_TAB_POSITION = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ama_main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set title to "Popular" as it is the first fragment to be shown.
        getSupportActionBar().setTitle(getResources().getString(R.string.popular_tab_name));
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ama_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {
            SyncMovieService.startActionFetchMovies(getApplicationContext(), Constants.POPULAR, "1");
            SyncMovieService.startActionFetchMovies(getApplicationContext(), Constants.TOP_RATED, "1");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentMessage(String TAG, Object data) {
        Intent i = new Intent(MainActivity.this, MovieActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.MOVIE, (MovieItem)data);
        i.putExtras(args);
        startActivity(i);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private MoviesGridViewFragment popularMoviesFragment;
        private MoviesGridViewFragment topRatedMoviesFragment;
        private MoviesGridViewFragment favouritesMoviesFragment;


        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POPULAR_MOVIES_TAB_POSITION:
                    if(popularMoviesFragment == null) {
                        popularMoviesFragment = new PopularMoviesGridViewFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.FRAGMENT_TAG,Constants.POPULAR);
                        popularMoviesFragment.setArguments(args);

                    }
                    return popularMoviesFragment;
                case TOP_RATED_MOVIES_TAB_POSITION:
                    if(topRatedMoviesFragment == null) {
                        topRatedMoviesFragment = new TopRatedMoviesGridViewFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.FRAGMENT_TAG,Constants.TOP_RATED);
                        topRatedMoviesFragment.setArguments(args);
                    }
                    return topRatedMoviesFragment;
                case FAVOURITE_MOVIES_TAB_POSITION:
                    if(favouritesMoviesFragment == null) {
                        favouritesMoviesFragment = new FavouritesMoviesGridViewFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.FRAGMENT_TAG, Constants.FAVOURITES);
                        favouritesMoviesFragment.setArguments(args);
                    }
                    return favouritesMoviesFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getResources().getString(R.string.popular_tab_name);
                case 1:
                    return mContext.getResources().getString(R.string.top_rated_tab_name);
                case 2:
                    return mContext.getResources().getString(R.string.favourites_tab_name);
            }
            return null;
        }
    }
}
