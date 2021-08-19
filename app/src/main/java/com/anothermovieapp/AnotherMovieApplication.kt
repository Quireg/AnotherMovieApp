package com.anothermovieapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import com.anothermovieapp.common.Constants
import com.anothermovieapp.repository.ProviderMovieDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AnotherMovieApplication : Application() {

    private lateinit var dbp : ProviderMovieDatabase

    override fun onCreate() {
        super.onCreate()
        dbp = ProviderMovieDatabase(applicationContext)

        //set sort order for favourites fragment in case it has not been set before
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val default_sort_order = preferences.getInt(Constants.SORT_ORDER, -1)
        if (default_sort_order == -1) {
            preferences.edit().putInt(Constants.SORT_ORDER, Constants.SORT_BY_RATING).apply()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "123123"
    }
}