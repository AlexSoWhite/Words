package com.nafanya.words.feature.word

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

sealed class Mode {
    object WordToTranslation : Mode()
    object TranslationToWord : Mode()
}

@Entity
data class Word(
    @PrimaryKey val word: String,
    @ColumnInfo(name = "transcription", defaultValue = "") val transcription: String = "",
    @ColumnInfo(
        name = "translations",
        defaultValue = ""
    ) val translations: List<String>,
    @ColumnInfo(name = "isLearned", defaultValue = false.toString()) var isLearned: Boolean = false,
    @ColumnInfo(
        name = "testPriority",
        defaultValue = TEST_PRIORITY_MAX.toString()
    ) var testPriority: Int = TEST_PRIORITY_MAX,
    @ColumnInfo(
        name = "testBalancer",
        defaultValue = TEST_BALANCER_MIN.toString()
    ) var testBalancer: Int = TEST_BALANCER_MIN,
    @ColumnInfo(
        name = "accumulatedTestPriority",
        defaultValue = TEST_PRIORITY_MIN.toString()
    ) var accumulatedTestPriority: Int = TEST_BALANCER_MIN
) {
    companion object {
        const val TEST_PRIORITY_MAX = 10
        const val TEST_PRIORITY_MIN = 0
        const val TEST_BALANCER_MIN = 0
    }

    fun first(mode: Mode): String {
        return if (mode is Mode.WordToTranslation) {
            word
        } else {
            translations.joinToString("; ")
        }
    }

    fun firstTranscription(mode: Mode): String {
        return if (mode is Mode.WordToTranslation) {
            transcription.ifEmpty {
                word
            }
        } else {
            translations.joinToString("; ")
        }
    }

    fun second(mode: Mode): String {
        return if (mode is Mode.WordToTranslation) {
            translations.joinToString("; ")
        } else {
            word
        }
    }

    fun secondTranscription(mode: Mode): String {
        return if (mode is Mode.WordToTranslation) {
            translations.joinToString("; ")
        } else {
            transcription.ifEmpty {
                word
            }
        }
    }

    fun joinedTranslations(): String {
        return translations.joinToString("; ")
    }

    fun setMaxTestPriority() {
        testPriority = TEST_PRIORITY_MAX
    }

    fun decreaseTestPriority() {
        if (testPriority > TEST_PRIORITY_MIN) {
            testPriority --
        }
    }

    fun increaseTestPriority() {
        if (testPriority < TEST_PRIORITY_MAX) {
            testPriority ++
        }
    }

    fun increaseBalancer(arraySize: Int, testSize: Int = 10) {
        testBalancer ++
        if (testBalancer >= calculateThreshold(arraySize, testSize)) {
            accumulatedTestPriority ++
            testBalancer = 0
        }
    }

    private fun calculateThreshold(arraySize: Int, testSize: Int): Int {
        if (arraySize <= testSize) {
            return 0
        }
        var numerator = 1
        var denominator = 0
        var tempVariable = testSize - 1
        while (tempVariable >= 0) {
            numerator *= (arraySize - tempVariable)
            tempVariable --
        }
        tempVariable = testSize - 1
        while (tempVariable >= 0) {
            denominator += numerator / (arraySize - tempVariable)
            tempVariable --
        }
        return numerator / denominator
    }

    fun dropTestAccumulators() {
        testBalancer = TEST_BALANCER_MIN
        accumulatedTestPriority = TEST_PRIORITY_MIN
    }
}
