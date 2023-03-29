package com.nafanya.words.feature.manageWords.addWord

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
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
        addInputListeners(R.id.translation_input)
        binding.submitButton.setOnClickListener { submitWord() }
    }

    private fun addInputListeners(@IdRes id: Int) {
        val input = view?.findViewById<EditText>(id)
        input?.onFocusChangeListener =
            View.OnFocusChangeListener { v, b ->
                if (v?.id == id && !b) {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                }
            }
    }

    private fun submitWord() {
        val word = binding.wordInput.text?.toString()?.trim()
        val translation = binding.translationInput.text?.toString()?.trim()
        if (checkWord(word) && checkWord(translation)) {
            viewModel.addWord(Word(word!!, translation!!)) {
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
        binding.translationInput.text!!.clear()
    }
}
