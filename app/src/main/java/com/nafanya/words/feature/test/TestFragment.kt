package com.nafanya.words.feature.test

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.nafanya.words.R
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentTestBinding
import com.nafanya.words.feature.tts.TtsProvider
import com.nafanya.words.feature.word.Mode
import javax.inject.Inject

class TestFragment : BaseFragment<FragmentTestBinding>() {

    private val viewModel: TestViewModel by viewModels { factory.get() }

    companion object {
        const val MODE = "mode"
        const val FIRST_PART = "first_part"
        const val SECOND_PART = "second_part"
    }

    sealed class State {
        object NoWordsLearned : State()
        object IsWaitingForAnswer : State()
        data class IsCheckedWord(
            var isCorrect: Boolean
        ) : State()
        class IsLast(
            val isCorrect: Boolean,
            val results: Bundle
        ) : State()
    }

    @Inject
    lateinit var ttsProvider: TtsProvider

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentTestBinding {
        return FragmentTestBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {
        applicationComponent.testComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setMode(
            if (arguments?.getInt(MODE) == 0) {
                Mode.WordToTranslation
            } else {
                Mode.TranslationToWord
            }
        )
        viewModel.currentWord.observe(viewLifecycleOwner) {
            renderWord(it.getString(FIRST_PART)!!, it.getString(SECOND_PART)!!)
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is State.NoWordsLearned -> renderEmptyList()
                is State.IsWaitingForAnswer -> renderWaitingForAnswer()
                is State.IsCheckedWord -> renderCheckedWord(it.isCorrect)
                is State.IsLast -> renderLast(it.isCorrect, it.results)
            }
        }
        binding.firstPartInput.onFocusChangeListener =
            View.OnFocusChangeListener { v, b ->
                if (v?.id == R.id.first_part_input && !b) {
                    val imm: InputMethodManager? =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        binding.voiceSecond.setOnClickListener { viewModel.speakOut(isVoicingFirstPart = false) }
        binding.voiceFirst.setOnClickListener { viewModel.speakOut(isVoicingFirstPart = true) }
    }

    private fun renderEmptyList() {
        resetViews(isNotEmptyList = false)
    }

    private fun renderWaitingForAnswer() = with(binding) {
        resetViews(isNotEmptyList = true)
        submitButton.setOnClickListener {
            val input = firstPartInput.text?.toString()?.trim() ?: ""
            viewModel.checkWord(input)
        }
    }

    private fun renderCheckedWord(isCorrect: Boolean) = with(binding) {
        showFirstPart(isCorrect)
        submitButton.text = getString(R.string.string_continue)
        submitButton.setOnClickListener {
            firstPartInput.text?.clear()
            viewModel.moveToNextWord()
        }
    }

    private fun renderLast(isCorrect: Boolean, results: Bundle) = with(binding) {
        showFirstPart(isCorrect)
        submitButton.text = getString(R.string.string_finish)
        submitButton.setOnClickListener {
            view?.findNavController()?.navigate(
                R.id.action_nav_actual_test_to_nav_test_results,
                results
            )
        }
    }

    private fun showFirstPart(isCorrect: Boolean) = with(binding) {
        firstPart.isVisible = true
        voiceFirst.isVisible = true
        correctnessIndicator.isVisible = true
        if (isCorrect) {
            correctnessIndicator.setImageResource(R.drawable.ic_done)
        } else {
            correctnessIndicator.setImageResource(R.drawable.ic_wrong)
        }
    }

    private fun renderWord(firstPartText: String, secondPartText: String) = with(binding) {
        resetViews(isNotEmptyList = true)
        secondPart.text = secondPartText
        firstPart.text = firstPartText
    }

    private fun resetViews(isNotEmptyList: Boolean) = with(binding) {
        emptyListMockup.isVisible = !isNotEmptyList
        emptyListMockup.text = getString(R.string.string_no_words_learned)
        secondPart.isVisible = isNotEmptyList
        voiceSecond.isVisible = isNotEmptyList
        firstPart.visibility = View.INVISIBLE
        voiceFirst.isVisible = false
        firstPartInput.isVisible = isNotEmptyList
        submitButton.isVisible = isNotEmptyList
        submitButton.text = getString(R.string.string_submit)
        correctnessIndicator.isVisible = false
        firstPart.isSelected = isNotEmptyList
        secondPart.isSelected = isNotEmptyList
    }
}
