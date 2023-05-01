package com.nafanya.words.core.ui.customViews

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.nafanya.words.R
import com.nafanya.words.databinding.TextInputFieldBinding

class TextInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.textInputLayoutStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val binding = TextInputFieldBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    val text: String?
        get() = binding.input.text?.toString()

    val textWithCheck: Pair<String?, Boolean>
        get() {
            val text = binding.input.text?.toString()?.trim()
            return Pair(text, checkField(this))
        }

    var inputHint: CharSequence?
        get() = binding.root.hint
        set(value) {
            binding.root.hint = value
        }

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.TextInputField
        )
        binding.root.hint = typedArray.getString(R.styleable.TextInputField_hint)
        binding.input.onFocusChangeListener =
            OnFocusChangeListener { v, b ->
                if (v?.id == binding.input.id && !b) {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0)
                } else if (v?.id == binding.input.id) {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.input, SHOW_IMPLICIT)
                }
            }
        typedArray.recycle()
    }

    fun clear() {
        binding.input.text?.clear()
    }

    fun addTextChangedListener(block: (Editable?) -> Unit) {
        binding.input.addTextChangedListener {
            block.invoke(it)
        }
    }

    private fun checkField(textInputField: TextInputField): Boolean {
        val text = textInputField.text?.trim()
        return checkWord(text)
//        if (!checkWord(text)) {
//            textInputField.error = context.getString(R.string.string_field_must_have_text)
//            return false
//        }
//        return true
    }

    private fun checkWord(word: String?): Boolean {
        if (word == null) {
            return false
        }
        return word.isNotBlank() && word.isNotEmpty()
    }
}
