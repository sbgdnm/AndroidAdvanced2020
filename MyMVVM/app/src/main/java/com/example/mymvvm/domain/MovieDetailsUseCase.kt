package com.example.mymvvm.domain

import androidx.lifecycle.LiveData
import com.example.mymvvm.data.model.MovieDetails
import com.example.mymvvm.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsUseCase(val movieDetailsRepository: MovieDetailsRepository) {
    fun getSingleMovieDetails(compositeDisposable: CompositeDisposable , movieId: Int): LiveData<MovieDetails>{
        return movieDetailsRepository.fetchSingleMovieDetails(compositeDisposable, movieId)
    }

    fun getMovieDetailsNetworkState(): LiveData<NetworkState>{
        return movieDetailsRepository.getMovieDetailsNetworkState()
    }
}