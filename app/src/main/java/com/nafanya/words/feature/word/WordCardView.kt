package com.nafanya.words.feature.word

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.card.MaterialCardView
import com.nafanya.words.databinding.WordCardViewBinding
import com.nafanya.words.feature.tts.TtsProvider

@SuppressLint("ClickableViewAccessibility")
class WordCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var onSwipedCallback: ((SwipeDirection) -> Unit)? = null

    private var onLearnedCallback: ((Word) -> Unit)? = null

    private var isShowingFirstPart = true

    private lateinit var currentWord: Word

    private lateinit var ttsProvider: TtsProvider

    private var mMode: Mode = Mode.WordToTranslation
    val mode
        get() = mMode

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
                    onSwipedCallback?.let {
                        it(SwipeDirection.LEFT)
                    }
                }

                override fun onSwipeRight() {
                    onSwipedCallback?.let {
                        it(SwipeDirection.RIGHT)
                    }
                }

                override fun onClick() {
                    flipCard()
                }
            }
        )

        binding.wordSound.setOnClickListener { speakOut() }

        binding.learnCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onLearnedCallback?.let {
                    it(currentWord)
                }
            }
        }
    }

    fun setOnSwipeCallback(callback: (SwipeDirection) -> Unit) {
        onSwipedCallback = callback
    }

    fun setOnLearnedCallback(callback: (Word) -> Unit) {
        onLearnedCallback = callback
    }

    fun attachTts(provider: TtsProvider) {
        ttsProvider = provider
    }

    fun setWord(word: Word) {
        currentWord = word
        binding.wordText.text = word.first(mMode)
        isShowingFirstPart = true
        binding.learnCheckbox.isChecked = word.isLearned
    }

    private fun flipCard() {
        if (isShowingFirstPart) {
            binding.wordText.text = currentWord.second(mMode)
            isShowingFirstPart = false
        } else {
            binding.wordText.text = currentWord.first(mMode)
            isShowingFirstPart = true
        }
    }

    private fun speakOut() {
        ttsProvider.resetLocale(mode, isShowingFirstPart)
        val text = if (isShowingFirstPart) {
            currentWord.first(mMode)
        } else {
            currentWord.second(mMode)
        }
        ttsProvider.speak(text)
    }

    fun changeMode() {
        mMode = when (mMode) {
            is Mode.WordToTranslation -> Mode.TranslationToWord
            is Mode.TranslationToWord -> Mode.WordToTranslation
        }
        currentWord.let {
            setWord(it)
        }
    }
}
