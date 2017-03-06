package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ua.in.quireg.anothermovieapp.BuildConfig;
import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constrains;
import ua.in.quireg.anothermovieapp.core.MovieFetcher;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.IMovieListListener;

public class MainActivity extends AppCompatActivity implements PopularMovieFragment.OnListFragmentInteractionListener, TopRatedMovieFragment.OnListFragmentInteractionListener, IMovieListListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int POPULAR_MOVIES_TAB_POSITION = 0;
    private static final int TOP_RATED_MOVIES_TAB_POSITION = 1;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    private List<MovieItem> popularMoviesList;
    private List<MovieItem> topRatedMoviesList;

    private PopularMovieFragment popularMoviesFragment;
    private TopRatedMovieFragment topRatedMoviesFragment;

    MovieFetcher fetcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ama_main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set title to "Popular" as it is the first fragment to be shown.
        getSupportActionBar().setTitle(getResources().getString(R.string.popular_tab_name));

        fetcher = MovieFetcher.getInstance(getApplicationContext());


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.getApplicationContext());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);




//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);


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

    }

    @Override
    public List<MovieItem> getMoviesList(String requestedList) {
        if (requestedList.equals(Constrains.POPULAR)) {
            return getPopularMoviesList();
        } else if (requestedList.equals(Constrains.TOP_RATED)) {
            return getTopRatedMoviesList();
        }
        return null;
    }

    public List<MovieItem> getPopularMoviesList() {
        if (this.popularMoviesList == null) {
            if (BuildConfig.DEBUG)
                Log.d(LOG_TAG, "Popular movies list requested for the first time");
            fetcher.requestMovieList(this, Constrains.POPULAR);
            return null;
        } else {
            return this.popularMoviesList;
        }
    }

    public List<MovieItem> getTopRatedMoviesList() {
        if (this.topRatedMoviesList == null) {
            if (BuildConfig.DEBUG)
                Log.d(LOG_TAG, "Top rated movies list requested for the first time");
            fetcher.requestMovieList(this, Constrains.TOP_RATED);
            return null;
        } else {
            return this.topRatedMoviesList;
        }
    }

    @Override
    public void setMoviesList(List<MovieItem> list, String tag) {
        if (tag.equals(Constrains.POPULAR)) {
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Popular movies list updated");
            this.popularMoviesList = list;
            reloadFragment(tag);
        } else if (tag.equals(Constrains.TOP_RATED)) {
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Top rated movies list updated");
            this.topRatedMoviesList = list;
            reloadFragment(tag);
        }

    }

    public void reloadFragment(String tag) {
        if (tag.equals(Constrains.POPULAR)) {
            if (popularMoviesFragment != null) {
                popularMoviesFragment.reload();
            }
        }else if(tag.equals(Constrains.TOP_RATED)){
            if(topRatedMoviesFragment != null){
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
            switch(position){
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
