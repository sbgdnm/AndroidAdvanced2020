package com.example.mymvvm

import android.app.Application
import com.example.mymvvm.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            koin.loadModules(
                listOf(
                    viewModelModule,
                    useCaseModule,
                    repositoryModule,
                    networkModule,
                    sharedPrefModule
                )
            )
            koin.createRootScope()
            // modules(repositoryModule,networkModule, sharedPrefModule, viewModelModule)
        }
    }
}