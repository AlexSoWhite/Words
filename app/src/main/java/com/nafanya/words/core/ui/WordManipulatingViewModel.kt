package com.nafanya.words.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.core.coroutines.inScope
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.feature.word.Word
import kotlinx.coroutines.launch

abstract class WordManipulatingViewModel : ViewModel() {

    abstract val ioCoroutineProvider: IOCoroutineProvider

    abstract val wordDatabaseProvider: WordDatabaseProvider

    fun markWordAsNotLearned(word: Word, callback: (WordDatabaseProvider.OperationResult) -> Unit) {
        word.isLearned = false
        word.dropTestAccumulators()
        word.setMaxTestPriority()
        ioCoroutineProvider.ioScope.launch {
            try {
                wordDatabaseProvider.updateWord(word)
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Success)
            } catch (exception: Exception) {
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Failure)
            }
        }
    }

    fun markWordAsLearned(word: Word, callback: (WordDatabaseProvider.OperationResult) -> Unit) {
        word.isLearned = true
        word.dropTestAccumulators()
        word.setMaxTestPriority()
        ioCoroutineProvider.ioScope.launch {
            try {
                wordDatabaseProvider.updateWord(word)
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Success)
            } catch (exception: Exception) {
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Failure)
            }
        }
    }
}
