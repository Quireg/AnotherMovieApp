package com.anothermovieapp.movieDetails

import android.animation.ObjectAnimator
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.palette.graphics.Palette
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.repository.Movie
import com.anothermovieapp.common.UriHelper
import com.anothermovieapp.interfaces.OnFragmentInteractionListener
import com.anothermovieapp.managers.MovieDatabaseContract.FavouriteMovies
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

class MovieActivity : AppCompatActivity(), OnFragmentInteractionListener {
    private var mDetailsFragment: DetailsFragment? = null
    private var mMovie: Movie? = null
    private var mToolbarLayout: CollapsingToolbarLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMovie = intent.extras.getSerializable(Constants.MOVIE) as Movie
        setContentView(R.layout.activity_movie_details_mine)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        mToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar)
        mDetailsFragment = DetailsFragment()
        mDetailsFragment!!.arguments = intent.extras
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, mDetailsFragment!!, DetailsFragment.Companion.TAG)
                .commit()
        if (mMovie != null) {
            if (mMovie!!.title != null) {
                val collapsingTBL = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
                collapsingTBL.title = mMovie!!.title
            }
            if (mMovie!!.backdropPath != null) {
                loadBackdropImage(mMovie!!.backdropPath)
            }
        }
        appBarLayout.setBackgroundColor(Color.parseColor("#8C000000"))
        appBarLayout.setOnApplyWindowInsetsListener { v, insets ->
            toolbar.setPadding(
                    v.paddingLeft,
                    insets.systemWindowInsetTop,
                    v.paddingRight,
                    v.paddingBottom)
            findViewById<View>(R.id.container).setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    insets.systemWindowInsetBottom)
            val a = obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
            val actionBarSize = a.getDimensionPixelSize(0, -1)
            a.recycle()
            mToolbarLayout.setScrimVisibleHeightTrigger(
                    insets.systemWindowInsetTop + actionBarSize + 1)
            insets
        }
        SyncReviewsService.Companion.startActionFetchReviews(this, mMovie.getId().toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie_menu, menu)
        val d: Drawable
        d = if (isFavourite(mMovie)) {
            getDrawable(R.drawable.ic_favorite_red_24dp)
        } else {
            getDrawable(R.drawable.ic_favorite_black_24dp)
        }
        menu.findItem(R.id.favorite).icon = d
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favorite) {
            onFragmentMessage(
                    if (isFavourite(mMovie)) Constants.REMOVE_FROM_FAVOURITES else Constants.ADD_TO_FAVOURITES, mMovie)
            val d: Drawable
            d = if (isFavourite(mMovie)) {
                getDrawable(R.drawable.ic_favorite_red_24dp)
            } else {
                getDrawable(R.drawable.ic_favorite_black_24dp)
            }
            item.icon = d
            return true
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isFavourite(item: Movie?): Boolean {
        contentResolver.query(
                FavouriteMovies.buildUri(item.getId()),
                null,
                null,
                null,
                null).use { c -> return c != null && c.moveToFirst() }
    }

    override fun onFragmentMessage(tag: String?, data: Any?) {
        val item = data as Movie?
        when (tag) {
            Constants.ADD_TO_FAVOURITES -> {
                val contentValues = ContentValues()
                contentValues.put(FavouriteMovies._ID, item.getId())
                contentResolver.insert(FavouriteMovies.CONTENT_URI, contentValues)
            }
            Constants.REMOVE_FROM_FAVOURITES -> contentResolver.delete(
                    FavouriteMovies.buildUri(item.getId()), null, null)
        }
    }

    private fun loadBackdropImage(path: String?) {
        val imageUri = UriHelper.getImageUri(path, Constants.IMAGE_SIZE_ORIGINAL)
        Log.d(LOG_TAG, "Fetching: $imageUri")
        val headerImage = findViewById<ImageView>(R.id.toolbar_image)
        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .addListener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any,
                                              target: Target<Bitmap?>,
                                              isFirstResource: Boolean): Boolean {
                        if (mDetailsFragment != null) {
                            mDetailsFragment!!.readyShowContent()
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any,
                                                 target: Target<Bitmap?>,
                                                 dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        if (mDetailsFragment != null) {
                            mDetailsFragment!!.readyShowContent()
                        }
                        headerImage.setImageBitmap(resource)
                        val animator = ObjectAnimator.ofFloat(headerImage, View.ALPHA,
                                headerImage.alpha, 1f)
                        animator.duration = 600
                        animator.start()
                        return true
                    }
                })
                .submit()
    }

    private fun playWithPalette(resource: Bitmap, collapsingTBL: CollapsingToolbarLayout,
                                headerImage: ImageView) {
        Palette.from(Bitmap.createBitmap(resource)).generate { palette -> //                        FloatingActionButton fab = findViewById(R.id.fab);
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
            if (palette!!.dominantSwatch != null) {
                collapsingTBL.setExpandedTitleColor(
                        palette.dominantSwatch!!.titleTextColor)
            }
            if (palette.vibrantSwatch != null) {
                collapsingTBL.setCollapsedTitleTextColor(
                        palette.vibrantSwatch!!.titleTextColor)
                collapsingTBL.setContentScrimColor(
                        palette.vibrantSwatch!!.rgb)
            }
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            if (toolbar.navigationIcon != null) {
                toolbar.navigationIcon!!.setTint(
                        palette.getLightVibrantColor(R.attr.editTextColor))
            }
            collapsingTBL.setContentScrimColor(
                    palette.getMutedColor(R.attr.colorPrimary))
            collapsingTBL.setStatusBarScrimColor(
                    palette.getDarkMutedColor(R.attr.colorPrimaryDark))
            headerImage.setImageBitmap(resource)
            headerImage.visibility = View.VISIBLE
        }
    }

    companion object {
        private val LOG_TAG = MovieActivity::class.java.simpleName
    }
}