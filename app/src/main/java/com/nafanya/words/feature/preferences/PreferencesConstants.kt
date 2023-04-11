package com.nafanya.words.feature.preferences

object PreferencesConstants {

    const val PREFS_FILE_NAME = "default_preferences"

    const val showNotLearnedKey = "showNotLearned"
    const val showNotMasteredKey = "showNotMastered"
    const val showMasteredKey = "showMastered"

    val learningKeys = listOf(showMasteredKey, showNotLearnedKey, showNotMasteredKey)
}
