package com.nafanya.words.core.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nafanya.words.core.db.migrations.DeleteTranslation
import com.nafanya.words.feature.word.Word

@Database(
    entities = [Word::class],
    version = 7,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7, spec = DeleteTranslation::class)
    ]
)
@TypeConverters(com.nafanya.words.core.db.ListConverter::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
