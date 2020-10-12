package com.example.mymvvm.di

import android.content.SharedPreferences
import com.example.mymvvm.data.api.TheMovieDBClient
import com.example.mymvvm.data.repository.MovieDataSource
import com.example.mymvvm.data.repository.MovieDataSourceFactory
import com.example.mymvvm.data.repository.MovieDetailsNetworkDataSource
import com.example.mymvvm.data.repository.NetworkState
import com.example.mymvvm.domain.MovieDetailsRepository
import com.example.mymvvm.domain.MovieDetailsUseCase
import com.example.mymvvm.domain.MoviePagedListRepository
import com.example.mymvvm.domain.MoviePagedListUseCase
import com.example.mymvvm.viewmodel.MainActivityViewModel
import com.example.mymvvm.viewmodel.SingleMovieViewModel

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val viewModelModule = module {
    viewModel { MainActivityViewModel(get()) }
    viewModel { SingleMovieViewModel(movieDetailsUseCase = get() , movieId = get()) }
}
val useCaseModule = module {
    single { MoviePagedListUseCase(get()) }
    single { MovieDetailsUseCase(get()) }
}
val repositoryModule = module {
    single { NetworkState(status = get() , msg = get()) }
    single { MovieDataSource(
        apiService = get() , compositeDisposable = get()
    ) }
    single { MovieDataSourceFactory(
        apiService = get() , compositeDisposable = get()
    ) }
    single { MovieDetailsNetworkDataSource(
        apiService = get() , compositeDisposable = get()
    ) }
    single { MoviePagedListRepository(apiService = get())}
    single { MovieDetailsRepository(apiService = get()) }
}

val networkModule = module {
    single { TheMovieDBClient.getClient() }

}
val sharedPrefModule = module {
    single {
        androidApplication().getSharedPreferences("default", android.content.Context.MODE_PRIVATE)
    }

    single<SharedPreferences.Editor> {
        androidApplication().getSharedPreferences("default", android.content.Context.MODE_PRIVATE)
            .edit()
    }
}