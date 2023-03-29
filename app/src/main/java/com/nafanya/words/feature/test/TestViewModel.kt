package com.nafanya.words.feature.test

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.feature.test.TestFragment.Companion.FIRST_PART
import com.nafanya.words.feature.test.TestFragment.Companion.SECOND_PART
import com.nafanya.words.feature.test.TestResultFragment.Companion.CORRECT_ANSWERS
import com.nafanya.words.feature.test.TestResultFragment.Companion.WORDS_COUNT
import com.nafanya.words.feature.tts.TtsProvider
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.Word
import com.nafanya.words.feature.word.decreaseTestPriority
import com.nafanya.words.feature.word.first
import com.nafanya.words.feature.word.increaseTestPriority
import com.nafanya.words.feature.word.second
import javax.inject.Inject
import kotlinx.coroutines.launch

class TestViewModel @Inject constructor(
    private val databaseProvider: WordDatabaseProvider,
    private val ioCoroutineProvider: IOCoroutineProvider,
    private val ttsProvider: TtsProvider
) : ViewModel() {

    private var mWordList: List<Word>? = null
    private val mState: MutableLiveData<TestFragment.State> by lazy {
        MutableLiveData()
    }
    val state: LiveData<TestFragment.State>
        get() = mState

    private val mCurrentWord: MutableLiveData<Word> by lazy {
        MutableLiveData()
    }
    val currentWord: LiveData<Bundle>
        get() = mCurrentWord.map {
            bundleOf(
                FIRST_PART to it.first(mMode),
                SECOND_PART to it.second(mMode)
            )
        }

    private var currentPosition = 0

    private var correctAnswers = 0

    private var wordsCount = 0

    private lateinit var mMode: Mode

    init {
        ioCoroutineProvider.ioScope.launch {
            databaseProvider.words.collect {
                val list: MutableList<Word> = it.filter { word -> word.isLearned } as MutableList<Word>
                if (list.isEmpty()) {
                    mState.postValue(TestFragment.State.NoWordsLearned)
                } else if (mWordList == null) {
                    mWordList = createWordList(list)
                    wordsCount = mWordList!!.size
                    mCurrentWord.postValue(mWordList!![currentPosition])
                    mState.postValue(TestFragment.State.IsWaitingForAnswer)
                }
            }
        }
    }

    private fun createWordList(source: List<Word>): List<Word> {
        val list = mutableListOf<Word>()
        source.sortedByDescending {
            word -> word.testPriority
        }.groupBy {
            word -> word.testPriority
        }.entries.forEach { entry: Map.Entry<Int, List<Word>> ->
            if (list.size < 10) {
                entry.value.shuffled().forEach {
                    if (list.size < 10) {
                        list.add(it)
                    }
                }
            }
        }
        return list
    }

    fun setMode(mode: Mode) {
        mMode = mode
    }

    fun moveToNextWord() {
        currentPosition ++
        mState.value = TestFragment.State.IsWaitingForAnswer
        mCurrentWord.value = mWordList!![currentPosition]
    }

    fun checkWord(firstPartInput: String) {
        val result = firstPartInput == mCurrentWord.value!!.first(mMode)
        if (result) {
            correctAnswers ++
            decreaseTestPriority(mCurrentWord.value!!)
        } else {
            increaseTestPriority(mCurrentWord.value!!)
        }
        if (currentPosition == wordsCount - 1) {
            val bundle = bundleOf(
                CORRECT_ANSWERS to correctAnswers,
                WORDS_COUNT to wordsCount
            )
            mState.value = TestFragment.State.IsLast(
                isCorrect = result,
                results = bundle
            )
        } else {
            mState.value = TestFragment.State.IsCheckedWord(result)
        }
    }

    private fun decreaseTestPriority(word: Word) {
        word.decreaseTestPriority()
        updateWord(word)
    }

    private fun increaseTestPriority(word: Word) {
        word.increaseTestPriority()
        updateWord(word)
    }

    private fun updateWord(word: Word) {
        ioCoroutineProvider.ioScope.launch {
            try {
                databaseProvider.updateWord(word)
            } catch (exception: Exception) {
                Log.e("Test", "Failed to decrease priority: ${exception.localizedMessage}")
            }
        }
    }

    fun speakOut(isVoicingFirstPart: Boolean) {
        ttsProvider.resetLocale(mMode, isVoicingFirstPart)
        val text = if (isVoicingFirstPart) {
            mCurrentWord.value!!.first(mMode)
        } else {
            mCurrentWord.value!!.second(mMode)
        }
        ttsProvider.speak(text)
    }
}
