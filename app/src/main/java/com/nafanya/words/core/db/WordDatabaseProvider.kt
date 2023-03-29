package com.nafanya.words.core.db

import android.content.Context
import androidx.room.Room
import com.nafanya.words.feature.word.Word
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordDatabaseProvider @Inject constructor(
    context: Context
) {

    private val database = Room
        .databaseBuilder(
            context,
            WordDatabase::class.java,
            "word-database"
        ).build()

    val words = database.wordDao().getAll()

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
