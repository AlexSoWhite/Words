package com.nafanya.words.feature.learn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.core.coroutines.inScope
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.feature.Logger.LEARN_VIEW_MODEL
import com.nafanya.words.feature.tts.TtsProvider
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.Word
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LearnViewModel @Inject constructor(
    private val databaseProvider: WordDatabaseProvider,
    private val ioCoroutineProvider: IOCoroutineProvider,
    private val ttsProvider: TtsProvider
) : ViewModel() {

    private var wordList: List<Word>? = null

    private val mWords = MutableLiveData<List<Word>>()

    private val mState = MutableLiveData<LearnFragment.State>()
    val state: LiveData<LearnFragment.State>
        get() = mState

    private var shouldSuppressNextFlip = false
        get() {
            val suppress = field
            field = false
            return suppress
        }
    private val mIsShowingFirstPart = MutableLiveData(true)
    val isShowingFirstPart: LiveData<Pair<Boolean, Boolean>>
        get() = mIsShowingFirstPart.map {
            Pair(it, shouldSuppressNextFlip)
        }

    private val mMode = MutableLiveData<Mode>(Mode.WordToTranslation)
    val mode: LiveData<Pair<Mode, Boolean>>
        get() = mMode.map {
            Pair(it, shouldSuppressNextFlip)
        }

    private val mCurrentPosition = MutableLiveData(0)
    val currentPosition: LiveData<Pair<Int, Int>>
        get() = combine(
            mCurrentPosition.asFlow(),
            mWords.asFlow()
        ) { position: Int, list: List<Word> ->
            Pair(position + 1, list.size)
        }.asLiveData()

    val window: LiveData<Triple<Word, Word, Word>>
        get() = combine(
            mWords.asFlow(),
            mCurrentPosition.asFlow()
        ) { list, position ->
            val previousWord = if (position > 0) {
                list[position - 1]
            } else {
                list[list.size - 1]
            }
            val nextWord = if (position != list.size - 1) {
                list[position + 1]
            } else {
                list[0]
            }
            Triple(previousWord, list[position], nextWord)
        }.asLiveData()

    private val mTriggerForceUpdate = MutableLiveData<Unit>()
    val forceUpdate: LiveData<Unit>
        get() = mTriggerForceUpdate

    sealed class SwipeDirection {
        object RIGHT : SwipeDirection()
        object LEFT : SwipeDirection()
    }

    init {
        ioCoroutineProvider.ioScope.launch {
            databaseProvider.words.collect {
                if (it.isEmpty()) {
                    updateState(LearnFragment.State.Empty)
                } else {
                    val list: MutableList<Word> = it.filter { word -> !word.isLearned } as MutableList<Word>
                    wordList = if (wordList == null) {
                        list.shuffle()
                        list
                    } else {
                        wordList!!.filter { word -> list.contains(word) }
                    }
                    if (wordList!!.isEmpty()) {
                        updateState(LearnFragment.State.EverythingLearned)
                    } else {
                        viewModelScope.launch {
                            updatePositionIfNeeded()
                            updateState(LearnFragment.State.NotEmpty)
                            mWords.value = wordList!!
                            mTriggerForceUpdate.value = Unit
                        }
                    }
                }
            }
        }
    }

    private fun updateState(state: LearnFragment.State) {
        if (mState.value != state) {
            mState.postValue(state)
        }
    }

    private fun updatePositionIfNeeded() {
        if (
            mState.value == LearnFragment.State.NotEmpty &&
            mCurrentPosition.value == wordList!!.size
        ) {
            Log.d(LEARN_VIEW_MODEL, "updating position as dataset changed")
            mCurrentPosition.value = mCurrentPosition.value!! - 1
        }
    }

    fun addWordToLearned(
        word: Word,
        callback: (WordDatabaseProvider.OperationResult) -> Unit
    ) {
        word.isLearned = true
        ioCoroutineProvider.ioScope.launch {
            try {
                databaseProvider.updateWord(word)
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Success)
            } catch (exception: Exception) {
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Failure)
            }
        }
        mIsShowingFirstPart.value = true
    }

    fun onPositionUpdated(position: Int) {
        Log.d(LEARN_VIEW_MODEL, "updating position to $position")
        when (position) {
            0 -> swipe(SwipeDirection.RIGHT)
            2 -> swipe(SwipeDirection.LEFT)
            else -> {
                suppressNextFlip()
                mMode.postValue(mMode.value!!)
                suppressNextFlip()
                mIsShowingFirstPart.postValue(mIsShowingFirstPart.value!!)
            }
        }
    }

    private fun swipe(direction: SwipeDirection) {
        var position = mCurrentPosition.value!!
        viewModelScope.launch {
            when (direction) {
                is SwipeDirection.LEFT -> {
                    mCurrentPosition.value = (position + 1) % wordList!!.size
                    Log.d(
                        LEARN_VIEW_MODEL,
                        "swiped left, new position = ${mCurrentPosition.value!!}"
                    )
                }
                is SwipeDirection.RIGHT -> {
                    position = (position - 1) % wordList!!.size
                    if (position < 0) {
                        mCurrentPosition.value = wordList!!.size - 1
                    } else {
                        mCurrentPosition.value = position
                    }
                    Log.d(
                        LEARN_VIEW_MODEL,
                        "swiped right, new position = ${mCurrentPosition.value!!}"
                    )
                }
            }
            suppressNextFlip()
            mIsShowingFirstPart.value = true
            Log.d(
                LEARN_VIEW_MODEL,
                "updated showing first part to ${mIsShowingFirstPart.value!!}"
            )
        }
    }

    fun updateShowingPart() {
        mIsShowingFirstPart.value = !mIsShowingFirstPart.value!!
        Log.d(LEARN_VIEW_MODEL, "updated showing first part to ${mIsShowingFirstPart.value!!}")
    }

    fun changeMode() {
        mMode.value = when (mMode.value!!) {
            is Mode.WordToTranslation -> Mode.TranslationToWord
            is Mode.TranslationToWord -> Mode.WordToTranslation
        }
    }

    fun speakOut(word: Word) {
        ttsProvider.resetLocale(mMode.value!!, mIsShowingFirstPart.value!!)
        val text = if (mIsShowingFirstPart.value!!) {
            word.first(mMode.value!!)
        } else {
            word.second(mMode.value!!)
        }
        ttsProvider.speak(text)
    }

    fun suppressNextFlip() {
        shouldSuppressNextFlip = true
        Log.d(LEARN_VIEW_MODEL, "suppressing next flip")
    }
}
