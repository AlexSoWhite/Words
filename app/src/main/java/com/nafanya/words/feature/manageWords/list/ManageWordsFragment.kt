package com.nafanya.words.feature.manageWords.list

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nafanya.words.R
import com.nafanya.words.core.db.WordDatabaseProvider
import com.nafanya.words.core.di.ApplicationComponent
import com.nafanya.words.core.ui.WordManipulatingFragment
import com.nafanya.words.core.ui.WordManipulatingViewModel
import com.nafanya.words.databinding.FragmentManageWordsBinding
import com.nafanya.words.feature.word.Word

class ManageWordsFragment : WordManipulatingFragment<FragmentManageWordsBinding>() {

    private val viewModel: ManageWordsViewModel by viewModels { factory.get() }

    override val manipulatingViewModel: WordManipulatingViewModel
        get() = viewModel

    private lateinit var titleBase: String

    companion object {
        const val MENU_CHANGE_LEARNED_STATE = Menu.FIRST
        const val MENU_DELETE = Menu.FIRST + 1
    }

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): FragmentManageWordsBinding {
        return FragmentManageWordsBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onInject(applicationComponent: ApplicationComponent) {
        applicationComponent.manageWordsComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        val wordListAdapter = WordListAdapter(
            onWordPressCallback = {
                viewModel.speakOut(it)
            },
            onDropDownMenuOptionChosen = { word: Word, id: Int ->
                when (id) {
                    MENU_CHANGE_LEARNED_STATE -> manageLearnedState(word)
                    MENU_DELETE -> showDeletionConfirmationDialog(word)
                }
            }
        )
        wordsRecycler.apply {
            adapter = wordListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        viewModel.words.observe(viewLifecycleOwner) {
            wordListAdapter.submitList(it)
            /**
             * Since ListAdapter doesn't hold data changes correctly,
             * we are forced to call this.
             */
            wordListAdapter.notifyDataSetChanged()
            resetTitle(it.size)
            if (it.isEmpty()) {
                renderEmptyList()
            } else {
                renderNotEmptyList()
            }
        }
        titleBase = getString(R.string.menu_manage_words)
    }

    private fun resetTitle(amount: Int) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = if (amount == 0) {
            titleBase
        } else {
            "$titleBase ($amount)"
        }
    }

    private fun showDeletionConfirmationDialog(word: Word) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.string_delete_word_confirmation, word.word))
            .setPositiveButton(getString(R.string.string_yes)) { _, _ ->
                deleteWord(word)
            }
            .setNegativeButton(getString(R.string.string_no)) { _, _ -> }
            .show()
    }

    private fun manageLearnedState(word: Word) {
        if (word.isLearned) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.string_remove_from_learned_confirmation, word.word))
                .setPositiveButton(getString(R.string.string_yes)) { _, _ ->
                    markWordAsNotLearned(word)
                }
                .setNegativeButton(getString(R.string.string_no)) { _, _ -> }
                .show()
        } else {
            markWordAsLearned(word)
        }
    }

    private fun renderEmptyList() = with(binding) {
        emptyWordlistMockup.isVisible = true
        addWordButton.isVisible = false
        emptyWordlistMockup.setOnClickListener {
            view?.findNavController()?.navigate(
                R.id.action_nav_manage_words_to_nav_add_word
            )
        }
    }

    private fun renderNotEmptyList() = with(binding) {
        emptyWordlistMockup.isVisible = false
        addWordButton.isVisible = true
        addWordButton.setOnClickListener {
            view?.findNavController()?.navigate(
                R.id.action_nav_manage_words_to_nav_add_word
            )
        }
    }

    private fun deleteWord(word: Word) {
        viewModel.deleteWord(word) {
            when (it) {
                is WordDatabaseProvider.OperationResult.Success -> showToast(
                    getString(R.string.string_deleted_successfully)
                )
                is WordDatabaseProvider.OperationResult.Failure -> showToast(
                    getString(R.string.string_error_occurred)
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        // setting search dispatcher
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.updateQuery(query)
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.updateQuery(newText)
                    return false
                }
            }
        )
    }
}
