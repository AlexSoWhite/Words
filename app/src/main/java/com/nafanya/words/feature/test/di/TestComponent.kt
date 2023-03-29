package com.nafanya.words.feature.test.di

import com.nafanya.words.feature.test.TestFragment
import dagger.Subcomponent

@Subcomponent(modules = [TestModule::class])
interface TestComponent {

    fun inject(testFragment: TestFragment)
}
