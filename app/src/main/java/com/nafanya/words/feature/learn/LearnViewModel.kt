package com.nafanya.words.feature.learn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.core.coroutines.inScope
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.feature.word.Word
import com.nafanya.words.feature.word.WordCardView
import javax.inject.Inject
import kotlinx.coroutines.launch

class LearnViewModel @Inject constructor(
    private val databaseProvider: WordDatabaseProvider,
    private val ioCoroutineProvider: IOCoroutineProvider
) : ViewModel() {

    private var wordList: List<Word>? = null
        set(value) {
            field = value
            mWordNumberText.postValue("word ${currentPosition + 1} of ${wordList!!.size}")
        }

    private val mState: MutableLiveData<LearnFragment.State> by lazy {
        MutableLiveData()
    }
    val state: LiveData<LearnFragment.State>
        get() = mState

    private val mCurrentWord: MutableLiveData<Word> by lazy {
        MutableLiveData()
    }
    val currentWord: LiveData<Word>
        get() = mCurrentWord

    private val mWordNumberText: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val wordNumber: LiveData<String>
        get() = mWordNumberText

    private var currentPosition = 0
        set(value) {
            field = value
            mWordNumberText.postValue("word ${currentPosition + 1} of ${wordList!!.size}")
        }

    init {
        ioCoroutineProvider.ioScope.launch {
            databaseProvider.words.collect {
                if (it.isEmpty()) {
                    mState.postValue(LearnFragment.State.Empty)
                } else {
                    val list: MutableList<Word> = it.filter { word -> !word.isLearned } as MutableList<Word>
                    wordList = if (wordList == null) {
                        list.shuffle()
                        list
                    } else {
                        wordList!!.filter { word -> list.contains(word) }
                    }
                    if (wordList!!.isEmpty()) {
                        mState.postValue(LearnFragment.State.EverythingLearned)
                    } else {
                        mState.postValue(LearnFragment.State.NotEmpty)
                        updateWordQueue()
                    }
                }
            }
        }
    }

    fun addWordToLearned(word: Word, callback: (WordDatabaseProvider.OperationResult) -> Unit) {
        word.isLearned = true
        ioCoroutineProvider.ioScope.launch {
            try {
                databaseProvider.updateWord(word)
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Success)
            } catch (exception: Exception) {
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Failure)
            }
        }
    }

    private fun updateWordQueue() {
        updatePositionIfNeeded()
        mCurrentWord.postValue(wordList!![currentPosition])
    }

    fun swipe(direction: WordCardView.SwipeDirection) {
        when (direction) {
            is WordCardView.SwipeDirection.LEFT -> {
                currentPosition = (currentPosition + 1) % wordList!!.size
            }
            is WordCardView.SwipeDirection.RIGHT -> {
                currentPosition = (currentPosition - 1) % wordList!!.size
                if (currentPosition < 0) {
                    currentPosition = wordList!!.size - 1
                }
            }
        }
        mCurrentWord.value = wordList!![currentPosition]
    }

    private fun updatePositionIfNeeded() {
        if (currentPosition == wordList!!.size) {
            currentPosition --
        }
    }
}
