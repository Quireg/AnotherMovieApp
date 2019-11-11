package ua.in.quireg.anothermovieapp.movieList;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.common.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.OnFragmentInteractionListener;
import ua.in.quireg.anothermovieapp.movieDetails.MovieActivity;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int TOP_RATED_MOVIES_TAB_POSITION = 0;
    private static final int FAVOURITE_MOVIES_TAB_POSITION = 1;
    private static final int POPULAR_MOVIES_TAB_POSITION = 2;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private int mSystemInsetTop = -1;
    private int mSystemInsetBottom = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_ama_main_activity, null);
        setContentView(rootView);
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryColor));
        setSupportActionBar(toolbar);
        final AppBarLayout appBarLayout = findViewById(R.id.appbar);

        mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            appBarLayout.setPadding(
                    v.getPaddingLeft(),
                    insets.getSystemWindowInsetTop(),
                    v.getPaddingRight(),
                    v.getPaddingBottom());


            mSystemInsetTop = insets.getSystemWindowInsetTop();
            mSystemInsetBottom = insets.getSystemWindowInsetBottom();

            TextView pageNumber = findViewById(R.id.pageNumberAndTotal);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageNumber.getLayoutParams();
            lp.bottomMargin += insets.getSystemWindowInsetBottom();
            pageNumber.setLayoutParams(lp);

            ProgressBar pb = findViewById(R.id.progressBar);
            FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) pageNumber.getLayoutParams();
            lp.bottomMargin += insets.getSystemWindowInsetBottom();
            pb.setLayoutParams(lp2);

            return insets.consumeSystemWindowInsets();
        });
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setCurrentItem(FAVOURITE_MOVIES_TAB_POSITION);
        mSectionsPagerAdapter.getItem(FAVOURITE_MOVIES_TAB_POSITION).setUserVisibleHint(true);
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
            SyncMovieService.startFetchMovies(this, Constants.POPULAR, "1");
            SyncMovieService.startFetchMovies(this, Constants.TOP_RATED, "1");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentMessage(String TAG, Object data) {
        Intent i = new Intent(MainActivity.this, MovieActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.MOVIE, (MovieItem) data);
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

        SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POPULAR_MOVIES_TAB_POSITION:
                    if (popularMoviesFragment == null) {
                        popularMoviesFragment = new PopularMoviesGridViewFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.FRAGMENT_TAG, Constants.POPULAR);
                        args.putSerializable(Constants.INSET_TOP, mSystemInsetTop);
                        args.putSerializable(Constants.INSET_BOT, mSystemInsetBottom);
                        popularMoviesFragment.setArguments(args);
                    }
                    return popularMoviesFragment;
                case TOP_RATED_MOVIES_TAB_POSITION:
                    if (topRatedMoviesFragment == null) {
                        topRatedMoviesFragment = new TopRatedMoviesGridViewFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.FRAGMENT_TAG, Constants.TOP_RATED);
                        args.putSerializable(Constants.INSET_TOP, mSystemInsetTop);
                        args.putSerializable(Constants.INSET_BOT, mSystemInsetBottom);
                        topRatedMoviesFragment.setArguments(args);
                    }
                    return topRatedMoviesFragment;
                case FAVOURITE_MOVIES_TAB_POSITION:
                default:
                    if (favouritesMoviesFragment == null) {
                        favouritesMoviesFragment = new FavouritesMoviesGridViewFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.FRAGMENT_TAG, Constants.FAVOURITES);
                        args.putSerializable(Constants.INSET_TOP, mSystemInsetTop);
                        args.putSerializable(Constants.INSET_BOT, mSystemInsetBottom);
                        favouritesMoviesFragment.setArguments(args);
                    }
                    return favouritesMoviesFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POPULAR_MOVIES_TAB_POSITION:
                    return mContext.getResources().getString(R.string.popular_tab_name);
                case TOP_RATED_MOVIES_TAB_POSITION:
                    return mContext.getResources().getString(R.string.top_rated_tab_name);
                case FAVOURITE_MOVIES_TAB_POSITION:
                default:
                    return mContext.getResources().getString(R.string.favourites_tab_name);
            }
        }
    }
}
