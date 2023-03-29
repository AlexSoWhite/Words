package com.nafanya.words.core.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.nafanya.words.feature.word.Word

@Database(
    entities = [Word::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
