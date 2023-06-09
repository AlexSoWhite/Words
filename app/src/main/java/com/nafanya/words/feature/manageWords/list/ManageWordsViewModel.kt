package com.nafanya.words.feature.manageWords.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.core.coroutines.inScope
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.core.ui.WordManipulatingViewModel
import com.nafanya.words.feature.word.Word
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ManageWordsViewModel @Inject constructor(
    override val ioCoroutineProvider: IOCoroutineProvider,
    override val wordDatabaseProvider: WordDatabaseProvider
) : WordManipulatingViewModel() {

    private val mWords: MutableLiveData<List<Word>> by lazy {
        MutableLiveData()
    }
    val words: LiveData<List<Word>>
        get() = mWords

    private val query = MutableStateFlow("")

    init {
        ioCoroutineProvider.ioScope.launch {
            combine(
                wordDatabaseProvider.words,
                query
            ) { list, query ->
                mWords.postValue(list.reversed().filter { word ->
                    word.word.contains(query) || word.translation.contains(query)
                })
            }.collect()
        }
    }

    fun deleteWord(word: Word, callback: (WordDatabaseProvider.OperationResult) -> Unit) {
        ioCoroutineProvider.ioScope.launch {
            try {
                wordDatabaseProvider.deleteWord(word)
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Success)
            } catch (exception: Exception) {
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Failure)
            }
        }
    }

    fun updateQuery(text: String) {
        viewModelScope.launch {
            query.emit(text)
        }
    }
}
