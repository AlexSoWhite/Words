package com.nafanya.words.feature.learn

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.nafanya.words.R
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentLearnBinding
import com.nafanya.words.feature.Logger.LEARN_FRAGMENT
import com.nafanya.words.feature.learn.TripleWordAdapter.Companion.WORD_CARD_TAG
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.Word
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class LearnFragment : BaseFragment<FragmentLearnBinding>() {

    private val viewModel: LearnViewModel by viewModels { factory.get() }

    private var isPagerInitializationComplete = false

    private lateinit var adapter: TripleWordAdapter

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
        pager.isVisible = false
        changeMode.isVisible = false
        modeDescription.isVisible = false
        wordNumber.isVisible = false
    }

    private fun renderNotEmptyDictionary() = with(binding) {
        emptyDictionaryMockup.isVisible = false
        pager.isVisible = true
        changeMode.isVisible = true
        modeDescription.isVisible = true
        wordNumber.isVisible = true
        adapter = TripleWordAdapter()
        adapter.setOnLearnedListener { word ->
            Log.d(LEARN_FRAGMENT, "marking as learned $word")
            viewModel.addWordToLearned(word) {
                when (it) {
                    is WordDatabaseProvider.OperationResult.Success ->
                        showToast(getString(R.string.string_marked_as_learned))
                    is WordDatabaseProvider.OperationResult.Failure ->
                        showToast(getString(R.string.string_error_occurred))
                }
            }
        }
        adapter.setOnWordClickListener {
            Log.d(LEARN_FRAGMENT, "updating showing part")
            viewModel.updateShowingPart()
        }
        adapter.setOnVoicePressedListener {
            Log.d(LEARN_FRAGMENT, "voicing")
            viewModel.speakOut(it)
        }
        viewModel.window.observe(viewLifecycleOwner) {
            updateWindow(it)
        }
        viewModel.forceUpdate.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
        pager.adapter = adapter
        viewModel.suppressNextFlip()
        viewModel.isShowingFirstPart.observe(viewLifecycleOwner) {
            updateShowingPart(it)
        }
        viewModel.suppressNextFlip()
        viewModel.mode.observe(viewLifecycleOwner) {
            updateMode(it)
        }
        viewModel.currentPosition.observe(viewLifecycleOwner) {
            wordNumber.text = requireContext().getString(
                R.string.string_word_number,
                it.first,
                it.second
            )
        }
        changeMode.setOnClickListener {
            viewModel.changeMode()
        }
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Log.d(LEARN_FRAGMENT, "updating current position to $position")
            binding.pager.setCurrentItem(position, true)
            if (position != 1) {
                viewModel.onPositionUpdated(position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                binding.pager.setCurrentItem(1, false)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateWindow(window: Triple<Word, Word, Word>) = with(binding) {
        Log.d(LEARN_FRAGMENT, "new window received")
        adapter.submitWords(window) // Pending data, will be submitted next time adapter.notifyDatasetChanged() called
        if (!isPagerInitializationComplete) {
            isPagerInitializationComplete = true
            pager.post {
                pager.setCurrentItem(1, false)
                pager.registerOnPageChangeCallback(onPageChangeCallback)
            }
        }
    }

    private fun updateShowingPart(value: Pair<Boolean, Boolean>) = with(binding) {
        Log.d(LEARN_FRAGMENT, "updating showing first part to ${value.first}")
        val wordCard = view?.findViewWithTag<WordCardView>(WORD_CARD_TAG)
        if (wordCard != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                wordCard.setIsShowingFirstPart(value.first, value.second)
            }
        } else {
            adapter.forceSetShowingPart(value.first)
        }
    }

    private fun updateMode(value: Pair<Mode, Boolean>) = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            view?.findViewWithTag<WordCardView>(WORD_CARD_TAG)
                ?.setMode(value.first, value.second)
        }
        adapter.setMode(value.first)
        modeDescription.text = if (value.first is Mode.WordToTranslation) {
            getString(R.string.string_mode_description_japanese_to_native)
        } else {
            getString(R.string.string_mode_description_native_to_japanese)
        }
    }
}
