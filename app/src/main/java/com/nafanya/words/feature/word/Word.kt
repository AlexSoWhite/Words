package com.nafanya.words.feature.word

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nafanya.words.feature.word.Word.Companion.TEST_PRIORITY_MAX
import com.nafanya.words.feature.word.Word.Companion.TEST_PRIORITY_MIN

@Entity
data class Word(
    @PrimaryKey val word: String,
    @ColumnInfo(name = "translation") val translation: String,
    @ColumnInfo(name = "isLearned", defaultValue = false.toString()) var isLearned: Boolean = false,
    @ColumnInfo(
        name = "testPriority",
        defaultValue = TEST_PRIORITY_MAX.toString()
    ) var testPriority: Int = TEST_PRIORITY_MAX
) {
    companion object {
        const val TEST_PRIORITY_MAX = 10
        const val TEST_PRIORITY_MIN = 0
    }
}

sealed class Mode {
    object WordToTranslation : Mode()
    object TranslationToWord : Mode()
}

fun Word.first(mode: Mode): String {
    return if (mode is Mode.WordToTranslation) {
        word
    } else {
        translation
    }
}

fun Word.second(mode: Mode): String {
    return if (mode is Mode.WordToTranslation) {
        translation
    } else {
        word
    }
}

fun Word.setMaxTestPriority() {
    testPriority = TEST_PRIORITY_MAX
}

fun Word.decreaseTestPriority() {
    if (testPriority > TEST_PRIORITY_MIN) {
        testPriority --
    }
}

fun Word.increaseTestPriority() {
    if (testPriority < TEST_PRIORITY_MAX) {
        testPriority ++
    }
}
