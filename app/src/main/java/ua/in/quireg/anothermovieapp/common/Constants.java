package ua.in.quireg.anothermovieapp.common;


import ua.in.quireg.anothermovieapp.BuildConfig;

public class Constants {

    public static final String YOUTUBE_BASE_LINK = "https://www.youtube.com/watch?v=";
    public static final String FRAGMENT_TAG = "fragment_tag";
    public static final String POPULAR = "POPULAR";
    public static final String TOP_RATED = "TOP_RATED";
    public static final String FAVOURITES = "FAVOURITES";
    public static final String MOVIE = "movie_bundle";

    public static final String SYNC_UPDATES_FILTER = "ua.in.quireg.anothermovieapp.SYNC_UPDATES_FILTER";
    public static final String TOTAL_ITEMS_LOADED = "TOTAL_ITEMS_LOADED";
    public static final String LOADED_PAGE = "LOADED_PAGE";
    public static final String SYNC_STATUS = "SYNC_STATUS";
    public static final String SYNC_FAILED = "SYNC_FAILED";
    public static final String SYNC_COMPLETED = "SYNC_COMPLETED";

    public static final boolean LOGGING = BuildConfig.DEBUG;

    public static final int COLUMNS_NUMBER = 2;

    public static final String IMAGE_SIZE_ORIGINAL = "original";
    public static final String IMAGE_SIZE_W185 = "w185";

    //fragment interaction
    public static final String ADD_TO_FAVOURITES = "add_to_favourites";
    public static final String REMOVE_FROM_FAVOURITES = "remove_from_favourites";

}
