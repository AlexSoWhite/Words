package com.nafanya.words.feature.preferences

import com.nafanya.words.feature.preferences.learning.LearningPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DefaultPreferences : AppPreferences {

    private val mLearningPreferences = MutableStateFlow(
        LearningPreferences(
            showNotLearned = true,
            showNotMastered = false,
            showMastered = false
        )
    )
    override val learningPreferences: StateFlow<LearningPreferences>
        get() = mLearningPreferences
}
