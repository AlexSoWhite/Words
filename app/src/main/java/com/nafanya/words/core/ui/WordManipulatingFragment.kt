package com.nafanya.words.core.ui

import androidx.viewbinding.ViewBinding
import com.nafanya.words.R
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.feature.word.Word

abstract class WordManipulatingFragment<VB : ViewBinding> : BaseFragment<VB>() {

    abstract val manipulatingViewModel: WordManipulatingViewModel

    protected fun markWordAsNotLearned(word: Word) {
        manipulatingViewModel.markWordAsNotLearned(word) {
            when (it) {
                is WordDatabaseProvider.OperationResult.Success -> showToast(
                    getString(R.string.string_marked_as_not_learned)
                )
                is WordDatabaseProvider.OperationResult.Failure -> showToast(
                    getString(R.string.string_error_occurred)
                )
            }
        }
    }

    protected fun markWordAsLearned(word: Word) {
        manipulatingViewModel.markWordAsLearned(word) {
            when (it) {
                is WordDatabaseProvider.OperationResult.Success -> showToast(
                    getString(R.string.string_marked_as_learned)
                )
                is WordDatabaseProvider.OperationResult.Failure -> showToast(
                    getString(R.string.string_error_occurred)
                )
            }
        }
    }
}
