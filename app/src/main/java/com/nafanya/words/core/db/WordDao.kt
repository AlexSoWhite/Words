package com.nafanya.words.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nafanya.words.feature.word.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("select * from word")
    fun getAll(): Flow<List<Word>>

    @Insert
    suspend fun insert(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Update
    suspend fun update(word: Word)
}
