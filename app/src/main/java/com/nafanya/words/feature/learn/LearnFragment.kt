package com.nafanya.words.feature.learn

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.nafanya.words.R
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentLearnBinding
import com.nafanya.words.feature.tts.TtsProvider
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.SwipeTouchListener
import com.nafanya.words.feature.word.WordCardView
import javax.inject.Inject

class LearnFragment : BaseFragment<FragmentLearnBinding>() {

    private val viewModel: LearnViewModel by viewModels { factory.get() }

    @Inject
    lateinit var ttsProvider: TtsProvider

    sealed class State {
        object Empty : State()
        object NotEmpty : State()
        object EverythingLearned : State()
    }

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentLearnBinding {
        return FragmentLearnBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {
        applicationComponent.learnComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is State.Empty -> renderEmptyDictionary(
                    getString(R.string.string_no_words_in_dictionary)
                )
                is State.NotEmpty -> renderNotEmptyDictionary()
                is State.EverythingLearned -> renderEmptyDictionary(
                    getString(R.string.string_everything_learned)
                )
            }
        }
    }

    private fun renderEmptyDictionary(message: String) = with(binding) {
        emptyDictionaryMockup.isVisible = true
        emptyDictionaryMockup.text = message
        wordCard.isVisible = false
        changeMode.isVisible = false
        modeDescription.isVisible = false
        wordNumber.isVisible = false
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun renderNotEmptyDictionary() = with(binding) {
        emptyDictionaryMockup.isVisible = false
        wordCard.isVisible = true
        changeMode.isVisible = true
        modeDescription.isVisible = true
        wordNumber.isVisible = true
        viewModel.text.observe(viewLifecycleOwner) {
            wordCard.setText(it)
        }
        viewModel.wordNumber.observe(viewLifecycleOwner) {
            wordNumber.text = it
        }
        viewModel.mode.observe(viewLifecycleOwner) {
            renderMode(it)
        }
        val swipeTouchListener = SwipeTouchListener(
            requireContext(),
            wordCard.x
        )
        swipeTouchListener.setOnSwipeLeftListener {
            viewModel.swipe(WordCardView.SwipeDirection.LEFT)
        }
        swipeTouchListener.setOnSwipeRightListener {
            viewModel.swipe(WordCardView.SwipeDirection.RIGHT)
        }
        wordCard.setOnTouchListener(swipeTouchListener)
        wordCard.setOnVoicePressedCallback {
            viewModel.speakOut()
        }
        wordCard.setOnClickListener {
            viewModel.triggerCardFlip()
        }
        wordCard.setOnLearnedCallback {
            viewModel.addWordToLearned {
                when (it) {
                    is WordDatabaseProvider.OperationResult.Success ->
                        showToast(getString(R.string.string_marked_as_learned))
                    is WordDatabaseProvider.OperationResult.Failure ->
                        showToast(getString(R.string.string_error_occurred))
                }
            }
        }
        changeMode.setOnClickListener {
            viewModel.changeMode()
        }
    }

    private fun renderMode(mode: Mode) = with(binding) {
        modeDescription.text = if (mode is Mode.WordToTranslation) {
            getString(R.string.string_mode_description_japanese_to_native)
        } else {
            getString(R.string.string_mode_description_native_to_japanese)
        }
    }
}
