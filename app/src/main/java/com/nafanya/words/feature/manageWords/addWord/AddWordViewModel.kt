package com.nafanya.words.feature.manageWords.addWord

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.core.coroutines.inScope
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.feature.tts.TtsProvider
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.Word
import javax.inject.Inject
import kotlinx.coroutines.launch

class AddWordViewModel @Inject constructor(
    private val databaseProvider: WordDatabaseProvider,
    private val ioCoroutineProvider: IOCoroutineProvider,
    private val ttsProvider: TtsProvider
) : ViewModel() {

    fun addWord(word: Word, callback: (WordDatabaseProvider.OperationResult) -> Unit) {
        ioCoroutineProvider.ioScope.launch {
            try {
                databaseProvider.insertWord(word)
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Success)
            } catch (exception: SQLiteConstraintException) {
                callback.inScope(viewModelScope, WordDatabaseProvider.OperationResult.Failure)
            }
        }
    }

    fun speakOut(text: String) {
        ttsProvider.resetLocale(Mode.WordToTranslation, isVoicingFirstPart = true)
        ttsProvider.speak(text)
    }
}
