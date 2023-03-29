package com.nafanya.words.feature.manageWords.di

import androidx.lifecycle.ViewModel
import com.nafanya.words.core.di.ViewModelKey
import com.nafanya.words.feature.manageWords.addWord.AddWordViewModel
import com.nafanya.words.feature.manageWords.list.ManageWordsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ManageWordsModule {

    @Binds
    @[IntoMap ViewModelKey(ManageWordsViewModel::class)]
    fun provideManageWordsViewModel(manageWordsViewModel: ManageWordsViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(AddWordViewModel::class)]
    fun provideAddWordViewModel(addWordViewModel: AddWordViewModel): ViewModel
}
