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
import javax.inject.Inject
import kotlinx.coroutines.launch

class TestViewModel @Inject constructor(
    private val databaseProvider: WordDatabaseProvider,
    private val ioCoroutineProvider: IOCoroutineProvider,
    private val ttsProvider: TtsProvider
) : ViewModel() {

    private var mWordList: List<Word>? = null
    private val mState = MutableLiveData<TestFragment.State>()
    val state: LiveData<TestFragment.State>
        get() = mState

    private val mCurrentWord = MutableLiveData<Word>()
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

    private var pendingList = arrayListOf<Word>()

    init {
        ioCoroutineProvider.ioScope.launch {
            databaseProvider.words.collect {
                val list: MutableList<Word> = it.filter { word -> word.isLearned } as MutableList<Word>
                if (list.isEmpty() && mWordList == null) {
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
        val data = source.sortedByDescending {
            word -> word.testPriority + word.accumulatedTestPriority
        }
        list.addAll(data.take(10))
        if (data.size > 10) {
            data.takeLast(data.size - 10).forEach {
                it.increaseBalancer(data.size)
                pendingList.add(it)
            }
        }
        return list.shuffled()
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
        speakOut(mMode is Mode.WordToTranslation)
        val word = mCurrentWord.value!!
        word.dropTestAccumulators()
        val result = if (firstPartInput == word.first(mMode)) {
            true
        } else mMode == Mode.TranslationToWord && word.translations.contains(firstPartInput)
        if (result) {
            correctAnswers ++
            decreaseTestPriorityAndUpdate(word)
        } else {
            increaseTestPriorityAndUpdate(word)
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
            writePendingChanges()
        } else {
            mState.value = TestFragment.State.IsCheckedWord(result)
        }
    }

    private fun writePendingChanges() {
        pendingList.forEach {
            updateWord(it)
        }
    }

    private fun decreaseTestPriorityAndUpdate(word: Word) {
        word.decreaseTestPriority()
        updateWord(word)
    }

    private fun increaseTestPriorityAndUpdate(word: Word) {
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
            mCurrentWord.value!!.firstTranscription(mMode)
        } else {
            mCurrentWord.value!!.secondTranscription(mMode)
        }
        ttsProvider.speak(text)
    }
}
