package com.nafanya.words.feature.manageWords.addWord

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.nafanya.words.R
import com.nafanya.words.core.utils.dpToPx

class AdditionalEditTexts private constructor(private val linearLayout: LinearLayout) {

    private val list = mutableListOf<EditText>()

    companion object {
        const val EDIT_TEXT_TOP_MARGIN_DP = 10
    }

    class Builder {

        private lateinit var linearLayout: LinearLayout

        fun attachTo(linearLayout: LinearLayout): Builder {
            this.linearLayout = linearLayout
            return this
        }

        fun build(): AdditionalEditTexts {
            return AdditionalEditTexts(linearLayout)
        }
    }

    fun attachNewTranslationInput() {
        val editText = EditText(linearLayout.context)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            0,
            EDIT_TEXT_TOP_MARGIN_DP.dpToPx(linearLayout.context),
            0,
            0
        )
        editText.layoutParams = layoutParams
        editText.maxLines = 1
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.hint = linearLayout.context.getString(R.string.string_enter_translation)
        var wasNewAttached = false
        editText.addTextChangedListener {
            it?.toString()?.let { text ->
                if (text.isNotBlank() && text.isNotEmpty()) {
                    if (!wasNewAttached) {
                        wasNewAttached = true
                        attachNewTranslationInput()
                    }
                } else {
                    wasNewAttached = false
                    remove(editText)
                }
            }
        }
        add(editText)
    }

    private fun add(editText: EditText) {
        linearLayout.addView(
            editText,
            linearLayout.childCount - 1
        )
        list.add(editText)
        addInputListener(editText)
    }

    private fun addInputListener(editText: EditText) {
        editText.onFocusChangeListener =
            View.OnFocusChangeListener { v, b ->
                if (v?.id == editText.id && !b) {
                    val imm = linearLayout.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(linearLayout.windowToken, 0)
                }
            }
    }

    private fun remove(editText: EditText) {
        linearLayout.removeView(editText)
        list.remove(editText)
    }

    fun forEach(block: (EditText) -> Unit) {
        list.take(list.size - 1).forEach(block)
    }

    fun clearAll() {
        while (list.isNotEmpty()) {
            remove(list[0])
        }
        attachNewTranslationInput()
    }
}
