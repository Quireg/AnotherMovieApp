/*
 * Created by Arcturus Mengsk
 *   2021.
 */

/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.viewmodel

import androidx.lifecycle.*
import com.anothermovieapp.repository.EntityDBMovie
import com.anothermovieapp.repository.RepositoryPopularList
import com.anothermovieapp.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelPopularMovies @Inject constructor(var repo: RepositoryPopularList) : ViewModel() {

    var popularMovies = MutableLiveData<Resource<List<EntityDBMovie>>>()
    var popularMoviesTotalAmount = MutableLiveData<Long>()
    var currentPage = 0;

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repo.getPopularMovies().collectLatest {
                it.data?.page.also { page -> page?.toInt()?.also { p -> currentPage = p }  }
                popularMovies.postValue(Resource(it.status, it.data?.data, it.message))
            }

        }
        viewModelScope.launch(Dispatchers.Default) {
            repo.getTotalNumber().collectLatest {
                popularMoviesTotalAmount.postValue(it)
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (currentPage == 0) {
                repo.fetchMorePopular()
            }
        }
    }

    fun fetchMore() {
        viewModelScope.launch(Dispatchers.Default) {
            repo.fetchMorePopular()
        }
    }

    fun reload() {
        viewModelScope.launch(Dispatchers.Default) {
            repo.reloadPopular()
        }
    }
}