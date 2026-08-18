package com.coloroslauncher.app

import android.app.Application
import com.coloroslauncher.app.di.ServiceLocator

class LauncherApplication : Application() {

    lateinit var serviceLocator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        serviceLocator = ServiceLocator(this)
    }
}
