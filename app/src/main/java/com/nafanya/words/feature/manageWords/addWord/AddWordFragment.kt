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

    private lateinit var additionalEditTexts: AdditionalEditTexts

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
        additionalEditTexts = AdditionalEditTexts.Builder()
            .attachTo(binding.linearLayout)
            .build()
        additionalEditTexts.attachNewTranslationInput()
        binding.submitButton.setOnClickListener { submitWord() }
    }

    private fun submitWord() {
        val word = binding.wordInput.text?.toString()?.trim()
        var isFieldsOk = true
        if (!checkWord(word)) {
            isFieldsOk = false
        }
        val translations = mutableListOf<String>()
        additionalEditTexts.forEach {
            val translation = it.text?.toString()?.trim()
            if (!checkWord(translation)) {
                isFieldsOk = false
            } else {
                translations.add(translation!!)
            }
        }

        if (isFieldsOk) {
            viewModel.addWord(Word(word!!, translations)) {
                when (it) {
                    is WordDatabaseProvider.OperationResult.Success -> showToast(
                        getString(R.string.string_added_successfully)
                    )
                    is WordDatabaseProvider.OperationResult.Failure -> showToast(
                        getString(R.string.string_word_already_exists)
                    )
                }
            }
            clearInputs()
        } else {
            showToast(getString(R.string.string_some_fields_are_empty))
        }
    }

    private fun checkWord(word: String?): Boolean {
        if (word == null) {
            return false
        }
        return word.isNotBlank() && word.isNotEmpty()
    }

    private fun clearInputs() {
        binding.wordInput.text!!.clear()
        additionalEditTexts.clearAll()
    }
}
