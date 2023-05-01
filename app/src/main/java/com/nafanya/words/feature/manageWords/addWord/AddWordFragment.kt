package com.nafanya.words.feature.manageWords.addWord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nafanya.words.R
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.BaseFragment
import com.nafanya.words.databinding.FragmentAddWordBinding
import com.nafanya.words.feature.word.Word

class AddWordFragment : BaseFragment<FragmentAddWordBinding>() {

    private val viewModel: AddWordViewModel by viewModels { factory.get() }

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentAddWordBinding {
        return FragmentAddWordBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {
        applicationComponent.manageWordsComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButton.setOnClickListener { submitWord() }
    }

    private fun submitWord() {
        val (word, isWordFieldOk) = binding.wordInput.textWithCheck
        val transcription = binding.transcriptionInput.textWithCheck.first ?: ""
        val translations = mutableListOf<String>()
        var areTranslationsOk = true

        binding.translations.smartForEach {
            val (translation, isTranslationFieldOk) = it.textWithCheck
            if (!isTranslationFieldOk) {
                areTranslationsOk = false
            } else {
                translations.add(translation!!)
            }
        }

        if (isWordFieldOk && areTranslationsOk) {
            viewModel.addWord(Word(word!!, transcription, translations)) {
                when (it) {
                    is WordDatabaseProvider.OperationResult.Success -> {
                        showToast(
                            getString(R.string.string_added_successfully)
                        )
                        clearInputs()
                    }
                    is WordDatabaseProvider.OperationResult.Failure -> showToast(
                        getString(R.string.string_word_already_exists)
                    )
                }
            }
        } else {
            showToast(getString(R.string.string_some_fields_are_empty))
        }
    }

    private fun clearInputs() {
        binding.wordInput.clear()
        binding.transcriptionInput.clear()
        binding.translations.clearAll()
    }
}
