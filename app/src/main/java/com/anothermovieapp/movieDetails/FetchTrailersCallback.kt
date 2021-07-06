package com.anothermovieapp.movieDetails

import com.anothermovieapp.repository.MovieTrailer

interface FetchTrailersCallback {
    fun onTrailersFetchCompleted(trailers: List<MovieTrailer>)
}