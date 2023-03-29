package com.nafanya.words.feature.learn.di

import androidx.lifecycle.ViewModel
import com.nafanya.words.core.di.ViewModelKey
import com.nafanya.words.feature.learn.LearnViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface LearnModule {

    @Binds
    @[IntoMap ViewModelKey(LearnViewModel::class)]
    fun providesLearnViewModel(learnViewModel: LearnViewModel): ViewModel
}
