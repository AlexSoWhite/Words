package com.nafanya.words.core.db

import android.content.Context
import androidx.room.Room
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.feature.word.Word
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

@Singleton
class WordDatabaseProvider @Inject constructor(
    context: Context,
    private val ioCoroutineProvider: IOCoroutineProvider
) {

    private val database = Room
        .databaseBuilder(
            context,
            WordDatabase::class.java,
            "word-database"
        )
        .addTypeConverter(ListConverter())
        .build()

    val words: SharedFlow<List<Word>>
        get() = database.wordDao().getAll().shareIn(
            ioCoroutineProvider.ioScope,
            SharingStarted.Eagerly,
            replay = 1
        )

    suspend fun insertWord(word: Word) {
        database.wordDao().insert(word)
    }

    suspend fun deleteWord(word: Word) {
        database.wordDao().delete(word)
    }

    suspend fun updateWord(word: Word) {
        database.wordDao().update(word)
    }

    sealed class OperationResult {
        object Success : OperationResult()
        object Failure : OperationResult()
    }
}
