/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.palette.graphics.Palette
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants
import com.anothermovieapp.viewmodel.ViewModelFavoriteMovies
import com.anothermovieapp.repository.EntityDBMovie
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity(), OnFragmentInteractionListener {

    private var mDetailsFragment: DetailsFragment? = null
    private var mMovie: EntityDBMovie? = null
    private lateinit var mToolbarLayout: CollapsingToolbarLayout

    @Inject
    lateinit var viewModel: ViewModelFavoriteMovies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMovie = intent.extras?.getSerializable(Constants.MOVIE) as EntityDBMovie
        setContentView(R.layout.activity_movie_details_mine)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        mToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar)
        mDetailsFragment = DetailsFragment()
        mDetailsFragment!!.arguments = intent.extras

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mDetailsFragment!!, DetailsFragment.TAG)
            .commit()
        if (mMovie?.title != null) {
            val collapsingTBL = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
            collapsingTBL.title = mMovie?.title
        }
        if (mMovie?.backdropPath != null) {
            loadBackdropImage(mMovie?.backdropPath)
        }
        appBarLayout.setBackgroundColor(Color.parseColor("#8C000000"))
        appBarLayout.setOnApplyWindowInsetsListener { v, insets ->
            toolbar.setPadding(
                v.paddingLeft,
                insets.systemWindowInsetTop,
                v.paddingRight,
                v.paddingBottom
            )
            findViewById<View>(R.id.container).setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                insets.systemWindowInsetBottom
            )
            val a = obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
            val actionBarSize = a.getDimensionPixelSize(0, -1)
            a.recycle()
            mToolbarLayout.setScrimVisibleHeightTrigger(
                insets.systemWindowInsetTop + actionBarSize + 1
            )
            insets
        }
    }
    var heartMenu : MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie_menu, menu)
        heartMenu = menu.findItem(R.id.favorite)
        heartMenu?.icon = getDrawable(R.drawable.ic_favorite_black_24dp)
        viewModel.isFavorite(mMovie!!.id).observe(this) {
            heartMenu?.icon = if (it) {
                getDrawable(R.drawable.ic_favorite_red_24dp)
            } else {
                getDrawable(R.drawable.ic_favorite_black_24dp)
            }
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favorite) {
            viewModel.addOrRemove(mMovie!!.id)
            return true
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentMessage(tag: String?, data: Any?) {
    }

    private fun loadBackdropImage(path: String?) {
        val imageUri = Uri.Builder()
            .scheme("https")
            .authority("image.tmdb.org")
            .appendPath("t")
            .appendPath("p")
            .appendPath(Constants.IMAGE_SIZE_ORIGINAL)
            .appendPath(path)
            .build()
            .normalizeScheme();
        val headerImage = findViewById<ImageView>(R.id.toolbar_image)
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .addListener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {

                    Handler(Looper.getMainLooper()).post {
                        if (mDetailsFragment != null) {
                            mDetailsFragment!!.readyShowContent()
                        }
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?, model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    Handler(Looper.getMainLooper()).post {
                        if (mDetailsFragment != null) {
                            mDetailsFragment!!.readyShowContent()
                        }
                        headerImage.setImageBitmap(resource)
                        val animator = ObjectAnimator.ofFloat(
                            headerImage, View.ALPHA,
                            headerImage.alpha, 1f
                        )
                        animator.duration = 300
                        animator.start()
                    }
                    return true
                }
            })
            .submit()
    }

    private fun playWithPalette(
        resource: Bitmap, collapsingTBL: CollapsingToolbarLayout,
        headerImage: ImageView
    ) {
        Palette.from(Bitmap.createBitmap(resource))
            .generate { palette -> //                        FloatingActionButton fab = findViewById(R.id.fab);
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
                        palette.dominantSwatch!!.titleTextColor
                    )
                }
                if (palette.vibrantSwatch != null) {
                    collapsingTBL.setCollapsedTitleTextColor(
                        palette.vibrantSwatch!!.titleTextColor
                    )
                    collapsingTBL.setContentScrimColor(
                        palette.vibrantSwatch!!.rgb
                    )
                }
                val toolbar = findViewById<Toolbar>(R.id.toolbar)
                if (toolbar.navigationIcon != null) {
                    toolbar.navigationIcon!!.setTint(
                        palette.getLightVibrantColor(R.attr.editTextColor)
                    )
                }
                collapsingTBL.setContentScrimColor(
                    palette.getMutedColor(R.attr.colorPrimary)
                )
                collapsingTBL.setStatusBarScrimColor(
                    palette.getDarkMutedColor(R.attr.colorPrimaryDark)
                )
                headerImage.setImageBitmap(resource)
                headerImage.visibility = View.VISIBLE
            }
    }
}