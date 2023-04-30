package com.nafanya.words.core.db.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn("Word", "translation")
class DeleteTranslation : AutoMigrationSpec
