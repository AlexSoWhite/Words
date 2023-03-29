package com.nafanya.words.feature.learn.di

import com.nafanya.words.feature.learn.LearnFragment
import dagger.Subcomponent

@Subcomponent(modules = [LearnModule::class])
interface LearnComponent {

    fun inject(learnFragment: LearnFragment)
}
