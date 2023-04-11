package com.nafanya.words.feature.preferences.di

import android.content.Context
import android.content.SharedPreferences
import com.nafanya.words.feature.preferences.PreferencesConstants.PREFS_FILE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule {

    @Provides
    @Singleton
    fun getDefaultSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }
}
