package com.nafanya.words.feature.learn

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nafanya.words.R
import com.nafanya.words.feature.Logger.LEARN_ADAPTER
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.Word

class TripleWordAdapter : RecyclerView.Adapter<TripleWordAdapter.WordViewHolder>() {

    companion object {
        const val WORD_CARD_TAG = "word_card_tag"
        const val ITEMS_IN_ADAPTER_BY_DESIGN = 3
    }

    private var onLearnedPressedCallback: ((Word, Boolean) -> Unit)? = null

    private var onVoicePressedCallback: ((Word) -> Unit)? = null

    private var onWordClickCallback: ((Word) -> Unit)? = null

    private var words: Triple<Word, Word, Word>? = null

    private var mMode: Mode = Mode.WordToTranslation

    private var mIsShowingFirstPart: Boolean? = null
        get() {
            val value = field
            field = null
            return value
        }

    fun setOnLearnedPressedListener(callback: (Word, Boolean) -> Unit) {
        onLearnedPressedCallback = callback
    }

    fun setOnVoicePressedListener(callback: (Word) -> Unit) {
        onVoicePressedCallback = callback
    }

    fun setOnWordClickListener(callback: (Word) -> Unit) {
        onWordClickCallback = callback
    }

    fun submitWords(payload: Triple<Word, Word, Word>) {
        words = payload
    }

    fun setMode(mode: Mode) {
        mMode = mode
    }

    @SuppressLint("NotifyDataSetChanged")
    fun forceSetShowingPart(isShowingFirstPart: Boolean) {
        Log.d(LEARN_ADAPTER, "forcing set showing first part to $isShowingFirstPart")
        mIsShowingFirstPart = isShowingFirstPart
        try {
            notifyDataSetChanged()
        } catch (exception: IllegalStateException) {
            Log.d(LEARN_ADAPTER, "unable to update dataset: ${exception.localizedMessage}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = WordCardView(parent.context)
        view.tag = WORD_CARD_TAG
        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return WordViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (words != null) {
            ITEMS_IN_ADAPTER_BY_DESIGN
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        Log.d(LEARN_ADAPTER, "binding to position $position")
        words?.let {
            when (position) {
                0 -> holder.setWord(it.first, mMode)
                1 -> holder.setWord(it.second, mMode, mIsShowingFirstPart)
                2 -> holder.setWord(it.third, mMode)
            }
        }
    }

    inner class WordViewHolder(private val view: WordCardView) : RecyclerView.ViewHolder(view) {

        fun setWord(
            word: Word,
            mode: Mode,
            forcedShowingFirstPart: Boolean? = null
        ) {
            Log.d(LEARN_ADAPTER, "setting word in adapter $word")
            view.setMode(mode)
            view.setWord(word)
            forcedShowingFirstPart?.let {
                view.setIsShowingFirstPart(it)
            }
            view.setOnLearnedCallback {
                Log.d(LEARN_ADAPTER, "clicked to learn $word")
                onLearnedPressedCallback?.invoke(word, it)
            }
            view.setOnVoicePressedCallback {
                Log.d(LEARN_ADAPTER, "clicked to voice $word")
                onVoicePressedCallback?.invoke(word)
            }
            view.findViewById<ConstraintLayout>(R.id.content_root).setOnClickListener {
                Log.d(LEARN_ADAPTER, "clicked on $word")
                onWordClickCallback?.invoke(word)
            }
        }
    }
}
