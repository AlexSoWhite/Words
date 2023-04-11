package com.nafanya.words.feature.preferences.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModels { factory.get() }

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {
        applicationComponent.preferencesComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.learnPreferences.observe(viewLifecycleOwner) {
            showNotLearned.isChecked = it.showNotLearned
            showNotMastered.isChecked = it.showNotMastered
            showMastered.isChecked = it.showMastered
        }
        showNotLearned.setOnClickListener {
            viewModel.updateLearningPreferences(
                showNotLearned = !showNotLearned.isChecked
            )
        }
        showNotMastered.setOnClickListener {
            viewModel.updateLearningPreferences(
                showNotMastered = !showNotMastered.isChecked
            )
        }
        showMastered.setOnClickListener {
            viewModel.updateLearningPreferences(
                showMastered = !showMastered.isChecked
            )
        }
    }
}
