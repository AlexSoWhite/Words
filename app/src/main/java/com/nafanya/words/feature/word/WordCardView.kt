package com.nafanya.words.feature.word

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.nafanya.words.databinding.WordCardViewBinding

class WordCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var onLearnedCallback: (() -> Unit)? = null

    private var onVoicePressedCallback: (() -> Unit)? = null

    sealed class SwipeDirection {
        object RIGHT : SwipeDirection()
        object LEFT : SwipeDirection()
    }

    private val binding = WordCardViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        binding.learnCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onLearnedCallback?.invoke()
            }
        }

        binding.wordSound.setOnClickListener {
            onVoicePressedCallback?.invoke()
        }
    }

    fun setOnLearnedCallback(callback: () -> Unit) {
        onLearnedCallback = callback
    }

    fun setOnVoicePressedCallback(callback: () -> Unit) {
        onVoicePressedCallback = callback
    }

    fun setText(text: String) {
        binding.wordText.text = text
        // learned words cannot reach word card view
        binding.learnCheckbox.isChecked = false
    }
}
