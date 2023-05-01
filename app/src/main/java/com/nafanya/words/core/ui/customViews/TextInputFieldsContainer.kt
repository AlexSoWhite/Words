package com.nafanya.words.core.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.nafanya.words.R
import com.nafanya.words.core.utils.dpToPx
import com.nafanya.words.databinding.TextInputFieldsContainerBinding

class TextInputFieldsContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = TextInputFieldsContainerBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    companion object {
        const val EDIT_TEXT_TOP_MARGIN_DP = 5
    }

    init {
        attachNewTranslationInput()
    }

    private fun attachNewTranslationInput() {
        val textInputField = TextInputField(context)
        val textInputFieldLayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputFieldLayoutParams.setMargins(
            0,
            EDIT_TEXT_TOP_MARGIN_DP.dpToPx(context),
            0,
            0
        )
        textInputField.layoutParams = textInputFieldLayoutParams
        textInputField.inputHint = context.getString(R.string.string_enter_translation)
        var wasNewAttached = false
        textInputField.addTextChangedListener {
            it?.toString()?.let { text ->
                if (text.isNotBlank() && text.isNotEmpty()) {
                    if (!wasNewAttached) {
                        wasNewAttached = true
                        attachNewTranslationInput()
                    }
                } else {
                    wasNewAttached = false
                    remove(textInputField)
                }
            }
        }
        binding.root.post {
            binding.root.addView(textInputField)
        }
    }

    private fun remove(textInputField: TextInputField) {
        if (binding.root.childCount != 1) {
            binding.root.removeView(textInputField)
        }
    }

    fun smartForEach(block: (TextInputField) -> Unit) = with(binding) {
        if (root.childCount == 1) {
            block.invoke(root.getChildAt(0) as TextInputField)
        } else {
            root.children.take(root.childCount - 1).map { it as TextInputField }.forEach(block)
        }
    }

    fun clearAll() {
        while (binding.root.childCount != 0) {
            binding.root.removeViewAt(0)
        }
        attachNewTranslationInput()
    }
}
