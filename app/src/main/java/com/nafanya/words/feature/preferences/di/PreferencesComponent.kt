package com.nafanya.words.feature.preferences.di

import com.nafanya.words.feature.preferences.ui.SettingsFragment
import dagger.Subcomponent

@Subcomponent(modules = [PreferencesViewModelModule::class])
interface PreferencesComponent {

    fun inject(settingsFragment: SettingsFragment)
}

interface PreferencesComponentProvider {

    val preferencesComponent: PreferencesComponent
}
