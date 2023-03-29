package com.nafanya.words.core.di

import android.app.Application

class WordApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
            .builder()
            .context(this)
            .build()
    }
}
