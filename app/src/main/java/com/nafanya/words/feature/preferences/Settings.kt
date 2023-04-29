package com.nafanya.words.feature.preferences

import android.content.SharedPreferences
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.feature.preferences.learning.LearningPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Settings @Inject constructor(
    private val preferences: SharedPreferences,
    private val ioCoroutineProvider: IOCoroutineProvider
) : AppPreferences {

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (PreferencesConstants.learningKeys.contains(key)) {
                ioCoroutineProvider.ioScope.launch {
                    mLearningPreferences.emit(currentLearningPreferences)
                }
            }
        }

    private lateinit var defaultLearningPreferences: LearningPreferences
    private var currentLearningPreferences: LearningPreferences
        get() {
            val showNotLearned = preferences.getBoolean(
                PreferencesConstants.showNotLearnedKey,
                defaultLearningPreferences.showNotLearned
            )
            val showNotMastered = preferences.getBoolean(
                PreferencesConstants.showNotMasteredKey,
                defaultLearningPreferences.showNotMastered
            )
            val showMastered = preferences.getBoolean(
                PreferencesConstants.showMasteredKey,
                defaultLearningPreferences.showMastered
            )
            return LearningPreferences(showNotLearned, showNotMastered, showMastered)
        }
        set(value) {
            preferences.edit()
                .putBoolean(
                    PreferencesConstants.showNotLearnedKey,
                    value.showNotLearned
                )
                .putBoolean(
                    PreferencesConstants.showNotMasteredKey,
                    value.showNotMastered
                )
                .putBoolean(
                    PreferencesConstants.showMasteredKey,
                    value.showMastered
                )
                .apply()
        }

    private val mLearningPreferences = MutableSharedFlow<LearningPreferences>(replay = 1)
    override val learningPreferences: SharedFlow<LearningPreferences>
        get() = mLearningPreferences

    init {
        preferences.registerOnSharedPreferenceChangeListener(listener)
        ioCoroutineProvider.ioScope.launch {
            DefaultPreferences.learningPreferences.collectLatest {
                defaultLearningPreferences = it
                mLearningPreferences.emit(currentLearningPreferences)
            }
        }
    }

    fun updateLearningPreferences(value: LearningPreferences) {
        currentLearningPreferences = value
    }
}
