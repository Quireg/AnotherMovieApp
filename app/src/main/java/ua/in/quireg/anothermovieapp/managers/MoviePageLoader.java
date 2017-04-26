package ua.in.quireg.anothermovieapp.managers;

import android.content.Context;

import ua.in.quireg.anothermovieapp.interfaces.FetchMoreItemsCallback;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;


public class MoviePageLoader implements FetchMoreItemsCallback{
    private FetchMoreItemsCallback fetchMoreItemsCallback;
    private Context mContext;
    private String tag;
    private boolean fetchInProgress = false;

    public MoviePageLoader(Context context, String tag, FetchMoreItemsCallback callback){
        mContext = context;
        this.tag = tag;
        fetchMoreItemsCallback = callback;
    }

    public void fetchNewItems(int itemsInAdapter){
        if(fetchInProgress){
            return;
        }
        int pageToLoad = pageToLoad(itemsInAdapter);

        if(pageToLoad == -1){
            return;
        }
        fetchInProgress = true;
        SyncMovieService.startActionFetchMoviesForPage(mContext, tag, pageToLoad + "", MoviePageLoader.this);
    }

    private int pageToLoad(int itemsInAdapter){
        return itemsInAdapter/20 + 1;
    }

    @Override
    public void fetchStarted() {
        fetchMoreItemsCallback.fetchStarted();

    }

    @Override
    public void fetchCompleted() {
        fetchInProgress = false;
        fetchMoreItemsCallback.fetchCompleted();
    }

    @Override
    public void fetchErrorOccured() {
        fetchInProgress = false;
        fetchMoreItemsCallback.fetchErrorOccured();
    }
}
