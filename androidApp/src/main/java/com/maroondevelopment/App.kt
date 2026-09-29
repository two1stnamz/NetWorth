package com.maroondevelopment

import android.app.Application
import com.maroondevelopment.networth.di.initKoin
import com.maroondevelopment.networth.persistence.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(DriverFactory(this)) {
            androidLogger()
            androidContext(this@App)
        }
    }
}
