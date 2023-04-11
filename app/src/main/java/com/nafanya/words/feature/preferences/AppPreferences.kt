package com.nafanya.words.feature.preferences

import com.nafanya.words.feature.preferences.learning.LearningPreferences
import kotlinx.coroutines.flow.Flow

interface AppPreferences {

    val learningPreferences: Flow<LearningPreferences>
}
