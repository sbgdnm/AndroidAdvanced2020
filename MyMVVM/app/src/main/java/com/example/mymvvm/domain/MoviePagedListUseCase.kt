package com.example.mymvvm.domain

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.mymvvm.data.model.Movie
import com.example.mymvvm.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MoviePagedListUseCase(val moviePagedListRepository: MoviePagedListRepository) {

    fun getLiveMoviePagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>>{
    return moviePagedListRepository.fetchLiveMoviePagedList(compositeDisposable)
    }

    fun NetworkState(): LiveData<NetworkState>{
        return moviePagedListRepository.getNetworkState()
    }
}