package com.anothermovieapp.movieDetails

import com.anothermovieapp.repository.EntityDBMovieTrailer

interface FetchTrailersCallback {
    fun onTrailersFetchCompleted(trailers: List<EntityDBMovieTrailer>)
}