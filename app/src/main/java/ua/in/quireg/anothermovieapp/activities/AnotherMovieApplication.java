package ua.in.quireg.anothermovieapp.activities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.network.VolleyRequestQueueProvider;


public class AnotherMovieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //instantiate Volley on app start
        VolleyRequestQueueProvider.getInstance(getApplicationContext());
        //set sort order for favourites fragment in case it has not been set before
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int default_sort_order = preferences.getInt(Constants.SORT_ORDER, -1);
        if(default_sort_order == -1){
            preferences.edit().putInt(Constants.SORT_ORDER, Constants.SORT_BY_RATING).apply();
        }
    }

}
