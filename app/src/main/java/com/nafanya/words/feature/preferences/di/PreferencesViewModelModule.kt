package com.nafanya.words.feature.preferences.di

import androidx.lifecycle.ViewModel
import com.nafanya.words.core.di.ViewModelKey
import com.nafanya.words.feature.preferences.ui.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface PreferencesViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(SettingsViewModel::class)]
    fun providesLearnSettingsViewModel(learnSettingsViewModel: SettingsViewModel): ViewModel
}
