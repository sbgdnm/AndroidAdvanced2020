package com.example.mymvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mymvvm.data.repository.NetworkState
import com.example.mymvvm.data.model.MovieDetails
import com.example.mymvvm.domain.MovieDetailsUseCase
import io.reactivex.disposables.CompositeDisposable

class SingleMovieViewModel (private val movieDetailsUseCase: MovieDetailsUseCase , movieId: Int)  : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val  movieDetails : LiveData<MovieDetails> by lazy {
        movieDetailsUseCase.getSingleMovieDetails(compositeDisposable,movieId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        movieDetailsUseCase.getMovieDetailsNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }



}