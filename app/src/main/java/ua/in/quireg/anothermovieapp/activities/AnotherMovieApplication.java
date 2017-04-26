package ua.in.quireg.anothermovieapp.activities;

import android.app.Application;

import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.interfaces.FetchMoreItemsCallback;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;


public class AnotherMovieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SyncMovieService.startActionFetchMoviesInitial(getApplicationContext(), Constants.POPULAR, "1");
        SyncMovieService.startActionFetchMoviesInitial(getApplicationContext(), Constants.TOP_RATED, "1");
    }

}
