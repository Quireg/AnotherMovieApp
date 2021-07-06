package com.anothermovieapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.anothermovieapp.AnotherMovieApplication


class MoviesRepository(private val context: Context,
                       private val db: MoviesDatabase =
                               (context.applicationContext as AnotherMovieApplication)
                                       .getDatabase()!!,
                       val movies: LiveData<List<Movie>> = db.movieDao().getMovies(),
                       val trailers: LiveData<List<MovieTrailer>> = db.movieTrailerDao().getMovieTrailers(),
                       val reviews: LiveData<List<MovieReview>> = db.movieReviewDao().getMovieReviews()
) {

  fun load() : Unit {
      movies.observeForever(Observer {

      })
  }

    fun popular(page: String) : LiveData<List<Long>> {
        val data : MutableLiveData<List<Long>>

        val interactor = PopularMoviesInteractor(context, db)
        interactor.fetchPopular(page, object: PopularMoviesInteractor.Callback {
            override fun onComplete(result: List<Long>) {
                TODO("Not yet implemented")
            }
        })

        return MutableLiveData<List<Long>>()
    }

    val data : MutableLiveData<List<Long>> = {

        return List<Long>
    }


    fun movies(): LiveData<List<Movie>> = movies
    fun trailers(): LiveData<List<MovieTrailer>> = trailers
    fun reviews(): LiveData<List<MovieReview>> = reviews
}