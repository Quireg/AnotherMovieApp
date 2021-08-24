/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anothermovieapp.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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
        movie.value = Resource.loading(null)
        trailers.value = Resource.loading(null)
        reviews.value = Resource.loading(null)
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