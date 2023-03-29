package com.nafanya.words.feature.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.nafanya.words.R
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentTestResultBinding

class TestResultFragment : BaseFragment<FragmentTestResultBinding>() {

    companion object {
        const val WORDS_COUNT = "words_count"
        const val CORRECT_ANSWERS = "correct_answers"
    }

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentTestResultBinding {
        return FragmentTestResultBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val wordsCount = arguments?.getInt(WORDS_COUNT)
        val correctAnswers = arguments?.getInt(CORRECT_ANSWERS)
        binding.result.text = getString(R.string.string_test_results, correctAnswers, wordsCount)
        binding.returnButton.setOnClickListener {
            moveToTestFragment()
        }
    }

    private fun moveToTestFragment() {
        view?.findNavController()?.navigate(R.id.action_nav_test_results_to_nav_test)
    }
}
