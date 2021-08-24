/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.repository.EntityDBMovie
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    var popularMoviesFragment: MoviesGridViewFragment? = null
    var topRatedMoviesFragment: MoviesGridViewFragment? = null
    var favouritesMoviesFragment: MoviesGridViewFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootView = LayoutInflater.from(this)
            .inflate(R.layout.activity_ama_main_activity, null)

        setContentView(rootView)
        rootView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.primaryColor, theme))
        setSupportActionBar(toolbar)
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        // Set up the ViewPager with the sections adapter.
        val viewPager = findViewById<ViewPager>(R.id.container)
        popularMoviesFragment =
            supportFragmentManager.findFragmentByTag(
                "android:switcher:" + viewPager.id + ":" + POPULAR_MOVIES_TAB_POSITION
            )
                    as MoviesGridViewFragment?
        if (popularMoviesFragment == null) {
            popularMoviesFragment = PopularMoviesGridViewFragment()
        }
        popularMoviesFragment!!.fragmentListener(fragmentListener)
        topRatedMoviesFragment =
            supportFragmentManager.findFragmentByTag(
                "android:switcher:" + viewPager.id + ":" + TOP_RATED_MOVIES_TAB_POSITION
            )
                    as MoviesGridViewFragment?
        if (topRatedMoviesFragment == null) {
            topRatedMoviesFragment = TopRatedMoviesGridViewFragment()
        }
        topRatedMoviesFragment!!.fragmentListener(fragmentListener)
        favouritesMoviesFragment =
            supportFragmentManager.findFragmentByTag(
                "android:switcher:" + viewPager.id + ":" + FAVOURITE_MOVIES_TAB_POSITION
            )
                    as MoviesGridViewFragment?
        if (favouritesMoviesFragment == null) {
            favouritesMoviesFragment = FavouritesMoviesGridViewFragment()
        }
        favouritesMoviesFragment!!.fragmentListener(fragmentListener)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)


        viewPager.adapter = mSectionsPagerAdapter
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, insets: WindowInsetsCompat ->
//            ViewCompat.setOnApplyWindowInsetsListener(rootView, null)
//            appBarLayout.setPadding(
//                    v.paddingLeft,
//                    insets.systemWindowInsetTop,
//                    v.paddingRight,
//                    v.paddingBottom)
//            mSystemInsetTop = insets.systemWindowInsetTop
//            mSystemInsetBottom = insets.systemWindowInsetBottom
//            val pageNumber = findViewById<TextView>(R.id.pageNumberAndTotal)
//            val lp = pageNumber.layoutParams as FrameLayout.LayoutParams
//            lp.bottomMargin += insets.systemWindowInsetBottom
//            pageNumber.layoutParams = lp
//            val pb = findViewById<ProgressBar>(R.id.progressBar)
//            val lp2 = pageNumber.layoutParams as FrameLayout.LayoutParams
//            lp.bottomMargin += insets.systemWindowInsetBottom
//            pb.layoutParams = lp2
//            insets.consumeSystemWindowInsets()
//        }

        val pageNumberAndTotal : TextView = rootView.findViewById(R.id.pageNumberAndTotal)
        val lp= pageNumberAndTotal.layoutParams as FrameLayout.LayoutParams
        lp.bottomMargin = lp.bottomMargin + getNavBarHeight()
        pageNumberAndTotal.layoutParams = lp
        rootView.invalidate()
    }

    open fun getNavBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_ama_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            //            R.id.action_settings -> {
            //                return true
            //            }
            R.id.action_refresh -> {
                popularMoviesFragment?.reload()
                topRatedMoviesFragment?.reload()
                favouritesMoviesFragment?.reload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    val fragmentListener = object : OnFragmentInteractionListener {
        override fun onFragmentMessage(TAG: String?, data: Any?) {
            val i = Intent(this@MainActivity, DetailsActivity::class.java)
            val args = Bundle()
            args.putSerializable(Constants.MOVIE, data as EntityDBMovie?)
            i.putExtras(args)
            startActivity(i)
        }
    }

    inner class SectionsPagerAdapter internal constructor(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                POPULAR_MOVIES_TAB_POSITION -> {
                    popularMoviesFragment!!
                }
                TOP_RATED_MOVIES_TAB_POSITION -> {
                    topRatedMoviesFragment!!
                }
                else -> {
                    favouritesMoviesFragment!!
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    companion object {
        private const val TOP_RATED_MOVIES_TAB_POSITION = 1
        private const val FAVOURITE_MOVIES_TAB_POSITION = 2
        private const val POPULAR_MOVIES_TAB_POSITION = 0
    }
}