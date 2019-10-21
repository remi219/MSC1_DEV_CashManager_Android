package com.example.cashmanager

import android.app.Application
import com.example.cashmanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(appModule))
        }
        super.onCreate()
    }
}

