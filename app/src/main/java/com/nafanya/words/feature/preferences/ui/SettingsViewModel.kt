package com.nafanya.words.feature.preferences.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nafanya.words.core.coroutines.IOCoroutineProvider
import com.nafanya.words.feature.preferences.Settings
import com.nafanya.words.feature.preferences.learning.LearningPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel @Inject constructor(
    private val settings: Settings,
    private val ioCoroutineProvider: IOCoroutineProvider
) : ViewModel() {

    private val mLearnPreferences = MutableLiveData<LearningPreferences>()
    val learnPreferences: LiveData<LearningPreferences>
        get() = mLearnPreferences

    init {
        ioCoroutineProvider.ioScope.launch {
            settings.learningPreferences.collectLatest {
                mLearnPreferences.postValue(it)
            }
        }
    }

    fun updateLearningPreferences(
        showNotLearned: Boolean? = null,
        showNotMastered: Boolean? = null,
        showMastered: Boolean? = null
    ) {
        ioCoroutineProvider.ioScope.launch {
            val prefs = mLearnPreferences.value!!
            val newPreferences = LearningPreferences(
                showNotLearned = showNotLearned ?: prefs.showNotLearned,
                showNotMastered = showNotMastered ?: prefs.showNotMastered,
                showMastered = showMastered ?: prefs.showMastered
            )
            settings.updateLearningPreferences(newPreferences)
        }
    }
}
