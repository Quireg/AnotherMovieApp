package ua.in.quireg.anothermovieapp.interfaces;


import java.util.List;

import ua.in.quireg.anothermovieapp.common.MovieTrailer;

public interface FetchTrailersCallback {
    void onTrailersFetchCompleted(List<MovieTrailer> trailers);
}
