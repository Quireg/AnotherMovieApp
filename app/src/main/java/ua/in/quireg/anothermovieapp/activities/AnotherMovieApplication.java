package ua.in.quireg.anothermovieapp.activities;

import android.app.Application;


public class AnotherMovieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
//        SyncMovieService.startActionFetchMovies(getApplicationContext(), Constants.POPULAR, "1");
//        SyncMovieService.startActionFetchMovies(getApplicationContext(), Constants.TOP_RATED, "1");
    }

}
