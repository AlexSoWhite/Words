package com.nafanya.words.feature.manageWords.di

import com.nafanya.words.feature.manageWords.addWord.AddWordFragment
import com.nafanya.words.feature.manageWords.list.ManageWordsFragment
import dagger.Subcomponent

@Subcomponent(modules = [ManageWordsModule::class])
interface ManageWordsComponent {

    fun inject(manageWordsFragment: ManageWordsFragment)
    fun inject(addWordFragment: AddWordFragment)
}

interface ManageWordsComponentProvider {

    val manageWordsComponent: ManageWordsComponent
}
