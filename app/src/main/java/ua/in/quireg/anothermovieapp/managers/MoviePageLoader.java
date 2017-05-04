package ua.in.quireg.anothermovieapp.managers;

import android.content.Context;

import ua.in.quireg.anothermovieapp.interfaces.FetchMoreItemsCallback;
import ua.in.quireg.anothermovieapp.services.SyncMovieService;


public class MoviePageLoader implements FetchMoreItemsCallback{
    private FetchMoreItemsCallback fetchMoreItemsCallback;
    private Context mContext;
    private String tag;
    private long last_loaded_page = 0;
    private boolean fetchInProgress = false;

    public MoviePageLoader(Context context, String tag, FetchMoreItemsCallback callback){
        mContext = context;
        this.tag = tag;
        fetchMoreItemsCallback = callback;
    }

    public void fetchNewItems(){
        if(fetchInProgress){
            return;
        }
        long pageToLoad = last_loaded_page + 1;
        fetchInProgress = true;
        SyncMovieService.startActionFetchMoviesForPage(mContext, tag, pageToLoad + "", MoviePageLoader.this);
    }

    @Override
    public void setPageNumber(long pageNumber) {
        fetchMoreItemsCallback.setPageNumber(pageNumber);
        last_loaded_page = pageNumber;
    }

    @Override
    public void setTotalPages(long totalPages) {
        fetchMoreItemsCallback.setTotalPages(totalPages);
    }

    @Override
    public void setTotalResults(long totalResults) {
        fetchMoreItemsCallback.setTotalResults(totalResults);
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
