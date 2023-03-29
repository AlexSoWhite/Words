package com.nafanya.words.feature.manageWords.list

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nafanya.words.R
import com.nafanya.words.databinding.WordListItemViewBinding
import com.nafanya.words.feature.manageWords.list.ManageWordsFragment.Companion.MENU_CHANGE_LEARNED_STATE
import com.nafanya.words.feature.manageWords.list.ManageWordsFragment.Companion.MENU_DELETE
import com.nafanya.words.feature.word.Word

class WordListAdapter(
    private val onWordPressCallback: (Word) -> Unit,
    private val onDropDownMenuOptionChosen: (Word, Int) -> Unit
) : ListAdapter<Word, WordListAdapter.WordViewHolder>(diffUtilCallback) {

    companion object {
        const val PROGRESS_MULTIPLIER = 10
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.setWord(currentList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.word_list_item_view, parent, false)

        return WordViewHolder(
            view,
            parent.context,
            onWordPressCallback,
            onDropDownMenuOptionChosen
        )
    }

    class WordViewHolder(
        itemView: View,
        private val context: Context,
        private val onWordPressCallback: (Word) -> Unit,
        private val onDropDownMenuOptionChosen: (Word, Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding = WordListItemViewBinding.bind(itemView)

        fun setWord(word: Word) {
            binding.wordItemText.text = word.word
            binding.wordItemTranslation.text = word.translation
            binding.root.setOnClickListener {
                onWordPressCallback(word)
            }
            binding.actionButton.setOnClickListener {
                showDropDownMenu(word)
            }
            if (word.isLearned) {
                binding.masteredText.text = context.getString(
                    R.string.string_word_mastered,
                    Word.TEST_PRIORITY_MAX - word.testPriority,
                    Word.TEST_PRIORITY_MAX
                )
                binding.masteredProgress.progress =
                    (Word.TEST_PRIORITY_MAX - word.testPriority) * PROGRESS_MULTIPLIER
            }
            binding.masteredRoot.isVisible = word.isLearned
            binding.learnedIndicator.isVisible = word.isLearned
        }

        private fun showDropDownMenu(word: Word) {
            val wrapper = ContextThemeWrapper(context, R.style.dropdownMenuStyle)
            val popup = PopupMenu(wrapper, binding.actionButton)
            popup.menu
                .add(
                    0,
                    MENU_CHANGE_LEARNED_STATE,
                    Menu.NONE,
                    context.getString(
                        if (word.isLearned) {
                            R.string.string_mark_as_not_learned
                        } else {
                            R.string.string_mark_as_learned
                        }
                    )
                )
            popup.menu.add(0, MENU_DELETE, Menu.NONE, context.getString(R.string.string_delete_word))
            popup.setOnMenuItemClickListener { item ->
                onDropDownMenuOptionChosen(word, item.itemId)
                false
            }
            popup.show()
        }
    }
}

val diffUtilCallback = object : DiffUtil.ItemCallback<Word>() {
    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.word == newItem.word &&
                oldItem.translation == newItem.translation &&
                oldItem.isLearned == newItem.isLearned
    }

    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.word == newItem.word &&
                oldItem.translation == newItem.translation
    }
}
