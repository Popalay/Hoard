package com.github.popalay.hoard

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.context = this
    }
}