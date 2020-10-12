package com.example.mymvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.mymvvm.data.repository.NetworkState
import com.example.mymvvm.data.model.Movie
import com.example.mymvvm.domain.MoviePagedListUseCase
import io.reactivex.disposables.CompositeDisposable

class MainActivityViewModel(private val moviePagedListUseCase: MoviePagedListUseCase ) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val  moviePagedList : LiveData<PagedList<Movie>> by lazy {
        moviePagedListUseCase.getLiveMoviePagedList(compositeDisposable)
    }

    val  networkState : LiveData<NetworkState> by lazy {
        moviePagedListUseCase.NetworkState()
    }

    fun listIsEmpty(): Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}