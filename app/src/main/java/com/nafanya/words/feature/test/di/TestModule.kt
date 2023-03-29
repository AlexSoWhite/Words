package com.nafanya.words.feature.test.di

import androidx.lifecycle.ViewModel
import com.nafanya.words.core.di.ViewModelKey
import com.nafanya.words.feature.test.TestViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TestModule {

    @Binds
    @[IntoMap ViewModelKey(TestViewModel::class)]
    fun provideTestViewModel(testViewModel: TestViewModel): ViewModel
}
