package com.maroondevelopment

import android.app.Application
import com.maroondevelopment.networth.di.Factory
import com.maroondevelopment.networth.persistence.DriverFactory

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Factory.initialize(DriverFactory())
    }
}