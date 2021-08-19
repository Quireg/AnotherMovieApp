package com.anothermovieapp.movieList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anothermovieapp.repository.EntityDBMovie
import com.anothermovieapp.repository.RepositoryTopRatedList
import com.anothermovieapp.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTopRatedMovies @Inject constructor(var repo: RepositoryTopRatedList) : ViewModel() {

    var topRatedMovies = MutableLiveData<Resource<List<EntityDBMovie>>>()
    var topRatedMoviesTotalAmount = MutableLiveData<Long>()

    var currentPage = 0;

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repo.getTopRatedMovies().collectLatest {
                it.data?.page.also { page -> page?.toInt()?.also { p -> currentPage = p }  }
                topRatedMovies.postValue(Resource(it.status, it.data?.data, it.message))
            }

        }
        viewModelScope.launch(Dispatchers.Default) {
            repo.getTotalNumber().collectLatest {
                topRatedMoviesTotalAmount.postValue(it)
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (currentPage == 0) {
                repo.fetchMoreTopRated()
            }
        }
    }

    fun fetchMore() {
        viewModelScope.launch(Dispatchers.Default) {
            repo.fetchMoreTopRated()
        }
    }

    fun reload() {
        viewModelScope.launch(Dispatchers.Default) {
            repo.reloadTopRated()
        }
    }
}