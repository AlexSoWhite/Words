package com.nafanya.words.core.di

import android.content.Context
import com.nafanya.words.core.ui.MainActivity
import com.nafanya.words.feature.learn.di.LearnComponentProvider
import com.nafanya.words.feature.manageWords.di.ManageWordsComponentProvider
import com.nafanya.words.feature.test.di.TestComponentProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component
@Singleton
interface ApplicationComponent :
    LearnComponentProvider,
    ManageWordsComponentProvider,
    TestComponentProvider {

    fun inject(mainActivity: MainActivity)

    fun context(): Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }
}
