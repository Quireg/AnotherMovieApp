package ua.in.quireg.anothermovieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MLog;
import ua.in.quireg.anothermovieapp.core.MovieFetcher;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.IMovieListListener;
import ua.in.quireg.anothermovieapp.ui.PopularMovieFragment;
import ua.in.quireg.anothermovieapp.ui.TopRatedMovieFragment;

public class MainActivity extends AppCompatActivity implements PopularMovieFragment.OnListFragmentInteractionListener,
        TopRatedMovieFragment.OnListFragmentInteractionListener, IMovieListListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int POPULAR_MOVIES_TAB_POSITION = 0;
    private static final int TOP_RATED_MOVIES_TAB_POSITION = 1;

    private MovieFetcher fetcher;

    private List<MovieItem> popularMoviesList;
    private List<MovieItem> topRatedMoviesList;

    private PopularMovieFragment popularMoviesFragment;
    private TopRatedMovieFragment topRatedMoviesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ama_main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set title to "Popular" as it is the first fragment to be shown.
        getSupportActionBar().setTitle(getResources().getString(R.string.popular_tab_name));

        fetcher = MovieFetcher.getInstance(getApplicationContext());


        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.getApplicationContext());


        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(MovieItem item) {
        Intent i = new Intent(MainActivity.this, MovieDetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.MOVIE, item);
        i.putExtras(args);
        startActivity(i);
    }

    @Override
    public List<MovieItem> getMoviesList(String requestedList) {
        if (requestedList.equals(Constants.POPULAR)) {
            return getPopularMoviesList();
        } else if (requestedList.equals(Constants.TOP_RATED)) {
            return getTopRatedMoviesList();
        }
        return null;
    }

    public List<MovieItem> getPopularMoviesList() {
        if (this.popularMoviesList == null) {
            MLog.d(LOG_TAG, "Popular movies list requested for the first time");

            fetcher.requestMovieList(this, Constants.POPULAR);
            return null;
        } else {
            return this.popularMoviesList;
        }
    }

    public List<MovieItem> getTopRatedMoviesList() {
        if (this.topRatedMoviesList == null) {
            MLog.d(LOG_TAG, "Top rated movies list requested for the first time");

            fetcher.requestMovieList(this, Constants.TOP_RATED);
            return null;
        } else {
            return this.topRatedMoviesList;
        }
    }

    @Override
    public void setMoviesList(List<MovieItem> list, String tag) {
        if (tag.equals(Constants.POPULAR)) {
            MLog.d(LOG_TAG, "Popular movies list updated");

            this.popularMoviesList = list;
            reloadFragment(tag);

        } else if (tag.equals(Constants.TOP_RATED)) {
            MLog.d(LOG_TAG, "Top rated movies list updated");

            this.topRatedMoviesList = list;
            reloadFragment(tag);
        }

    }

    public void reloadFragment(String tag) {
        if (tag.equals(Constants.POPULAR)) {
            if (popularMoviesFragment != null) {
                popularMoviesFragment.reload();
            }
        } else if (tag.equals(Constants.TOP_RATED)) {
            if (topRatedMoviesFragment != null) {
                topRatedMoviesFragment.reload();
            }
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;


        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POPULAR_MOVIES_TAB_POSITION:
                    popularMoviesFragment = new PopularMovieFragment();
                    return popularMoviesFragment;
                case TOP_RATED_MOVIES_TAB_POSITION:
                    topRatedMoviesFragment = new TopRatedMovieFragment();
                    return topRatedMoviesFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getResources().getString(R.string.popular_tab_name);
                case 1:
                    return mContext.getResources().getString(R.string.top_rated_tab_name);
//                case 2:
//                    return "SECTION 3";
            }
            return null;
        }
    }
}