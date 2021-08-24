/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anothermovieapp.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFavoriteMovies @Inject constructor(private var repo: RepositoryFavoritesList) :
    ViewModel() {

    var movies = MutableLiveData<Resource<List<EntityDBMovie>>>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repo.getFavoriteMovies().collectLatest {
                movies.postValue(Resource.success(it))
            }
        }
    }

    fun addOrRemove(id: Long) {
        viewModelScope.launch(Dispatchers.Default) { repo.addOrRemove(id) }
    }

    fun isFavorite(id: Long): LiveData<Boolean> {
        val ld = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.Default) {
            repo.isFavourite(id).collectLatest {
                ld.postValue(it)
            }
        }
        return ld
    }
}