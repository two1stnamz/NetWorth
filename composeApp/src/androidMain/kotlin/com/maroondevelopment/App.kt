package com.maroondevelopment

import android.app.Application
import com.maroondevelopment.networth.di.Factory
import com.maroondevelopment.networth.persistence.DriverFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Factory.initialize(DriverFactory(this))
    }
}