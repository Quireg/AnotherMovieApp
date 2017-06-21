package ua.in.quireg.anothermovieapp.activities;

import android.app.Application;

import ua.in.quireg.anothermovieapp.network.VolleyRequestQueueProvider;


public class AnotherMovieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        VolleyRequestQueueProvider.getInstance(getApplicationContext());

//        SyncMovieService.startActionFetchMovies(getApplicationContext(), Constants.POPULAR, "1");
//        SyncMovieService.startActionFetchMovies(getApplicationContext(), Constants.TOP_RATED, "1");
    }

}
