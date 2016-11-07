package ua.in.quireg.anothermovieapp.core;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.in.quireg.anothermovieapp.ui.dummy.DummyContent;

public class MovieItemList {

    private static final String LOG_TAG = MovieItemList.class.getSimpleName();

    private static MovieItemList mInstance = new MovieItemList();

    private MovieItemList(){
    }
    public static synchronized MovieItemList getInstance() {
        if (mInstance == null) {
            mInstance = new MovieItemList();
            Log.d(LOG_TAG, "Instance created");
        }
        return mInstance;
    }
    private final List<MovieItem> ITEMS =  Collections.synchronizedList(new ArrayList<MovieItem>());
    private final Map<String, MovieItem> ITEM_MAP = Collections.synchronizedMap(new HashMap<String, MovieItem>());

    private boolean isInitalized = false;

    public boolean isInitialized(){
        return isInitalized;
    }

    public void initialize(String sortType, ArrayList<MovieItem> movieItems){

        if (sortType.equals("popular")){

        }
    }

    public synchronized List<MovieItem> getITEMS() {
        Log.d(LOG_TAG, "List requested");
        return ITEMS;
    }

    public synchronized Map<String, MovieItem> getItemMap() {
        Log.d(LOG_TAG, "Map requested");
        return ITEM_MAP;
    }

    public void addItem(MovieItem item){
        Log.d(LOG_TAG, "New item placed to list");
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
        if(!isInitalized){
            isInitalized = true;
        }
    }
}
