package com.anothermovieapp.movieList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anothermovieapp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewModelMovieDetails @Inject constructor(var repo: RepositoryMovies) : ViewModel() {

    var movie = MutableLiveData<Resource<List<EntityDBMovie>>>()
    var trailers = MutableLiveData<Resource<List<EntityDBMovieTrailer>>>()
    var reviews = MutableLiveData<Resource<ModelReviewsListRespond>>()

    val jobs = mutableListOf<Job>()

    fun setMovieId(id: Long) {
        for (job in jobs) {
            job.cancel()
        }
        jobs.clear()
        viewModelScope.launch(Dispatchers.Default) {
            repo.getMovies().collectLatest {
                movie.postValue(it)
            }
        }.also { jobs.add(it) }
        viewModelScope.launch(Dispatchers.Default) {
            repo.getMovieTrailers(id.toString()).collectLatest {
                trailers.postValue(it)
            }
        }.also { jobs.add(it) }
        viewModelScope.launch(Dispatchers.Default) {
            repo.getMovieReviews(id.toString()).collectLatest {
                reviews.postValue(it)
            }
        }.also { jobs.add(it) }
    }
}