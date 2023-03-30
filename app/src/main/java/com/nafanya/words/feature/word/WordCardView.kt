package com.nafanya.words.feature.word

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.nafanya.words.databinding.WordCardViewBinding

@SuppressLint("ClickableViewAccessibility")
class WordCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var onSwipedCallback: ((SwipeDirection) -> Unit)? = null

    private var onLearnedCallback: (() -> Unit)? = null

    private var onVoicePressedCallback: (() -> Unit)? = null

    private var onClickCallback: (() -> Unit)? = null

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
        binding.root.setOnTouchListener(
            object : OnSwipeTouchListener(context) {
                override fun onSwipeLeft() {
                    onSwipedCallback?.invoke(SwipeDirection.LEFT)
                }

                override fun onSwipeRight() {
                    onSwipedCallback?.invoke(SwipeDirection.RIGHT)
                }

                override fun onClick() {
                    onClickCallback?.invoke()
                }
            }
        )

        binding.learnCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onLearnedCallback?.invoke()
            }
        }

        binding.wordSound.setOnClickListener {
            onVoicePressedCallback?.invoke()
        }
    }

    fun setOnSwipeCallback(callback: (SwipeDirection) -> Unit) {
        onSwipedCallback = callback
    }

    fun setOnLearnedCallback(callback: () -> Unit) {
        onLearnedCallback = callback
    }

    fun setOnClickCallback(callback: () -> Unit) {
        onClickCallback = callback
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
