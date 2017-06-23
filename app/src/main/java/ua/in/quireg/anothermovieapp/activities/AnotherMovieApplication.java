package ua.in.quireg.anothermovieapp.activities;

import android.app.Application;

import ua.in.quireg.anothermovieapp.network.VolleyRequestQueueProvider;


public class AnotherMovieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        VolleyRequestQueueProvider.getInstance(getApplicationContext());

    }

}
