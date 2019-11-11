package ua.in.quireg.anothermovieapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import ua.in.quireg.anothermovieapp.common.Constants;

public class AnotherMovieApplication extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "123123";

    @Override
    public void onCreate() {
        super.onCreate();

        //set sort order for favourites fragment in case it has not been set before
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int default_sort_order = preferences.getInt(Constants.SORT_ORDER, -1);
        if (default_sort_order == -1) {
            preferences.edit().putInt(Constants.SORT_ORDER, Constants.SORT_BY_RATING).apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                            NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
