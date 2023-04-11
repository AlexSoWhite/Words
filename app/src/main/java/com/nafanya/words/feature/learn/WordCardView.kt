package com.nafanya.words.feature.learn

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.nafanya.words.databinding.WordCardViewBinding
import com.nafanya.words.feature.Logger.WORD_CARD_VIEW
import com.nafanya.words.feature.word.Mode
import com.nafanya.words.feature.word.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class WordCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var onLearnedPressedCallback: ((Boolean) -> Unit)? = null

    private var onVoicePressedCallback: (() -> Unit)? = null

    private var mWord: Word? = null

    private var mIsShowingFirstPart = true

    private var mMode: Mode = Mode.WordToTranslation

    private val binding = WordCardViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    companion object {
        const val HALF_OF_FLIP_ANIMATION = 250L
        const val CAMERA_MULTIPLIER = 10000
        const val RIGHT_ANGLE = 90f
    }

    init {
        binding.learnCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (
                mWord?.isLearned == true && !isChecked ||
                    mWord?.isLearned == false && isChecked) {
                onLearnedPressedCallback?.invoke(isChecked)
            }
        }

        binding.wordSound.setOnClickListener {
            onVoicePressedCallback?.invoke()
        }
    }

    fun setOnLearnedCallback(callback: (Boolean) -> Unit) {
        onLearnedPressedCallback = callback
    }

    fun setOnVoicePressedCallback(callback: () -> Unit) {
        onVoicePressedCallback = callback
    }

    fun setWord(word: Word) {
        Log.d(WORD_CARD_VIEW, "setting word $word")
        mWord = word
        mIsShowingFirstPart = true
        binding.learnCheckbox.isChecked = word.isLearned
        updateText()
    }

    fun setIsShowingFirstPart(isShowingFirstPart: Boolean) {
        if (mIsShowingFirstPart != isShowingFirstPart) {
            mIsShowingFirstPart = isShowingFirstPart
            updateText()
        }
    }

    suspend fun setIsShowingFirstPart(
        isShowingFirstPart: Boolean,
        suppressFlip: Boolean
    ) {
        Log.d(WORD_CARD_VIEW, "triggered showing part update")
        if (mIsShowingFirstPart != isShowingFirstPart) {
            Log.d(WORD_CARD_VIEW, "updating showing part")
            mIsShowingFirstPart = isShowingFirstPart
            if (!suppressFlip) {
                flipCard()
            } else {
                updateText()
            }
        }
    }

    fun setMode(mode: Mode) {
        if (mMode != mode) {
            mMode = mode
            updateText()
        }
    }

    suspend fun setMode(mode: Mode, suppressFlip: Boolean) {
        if (mMode != mode) {
            mMode = mode
            if (!suppressFlip) {
                flipCard()
            } else {
                updateText()
            }
        }
    }

    private fun updateText() {
        binding.wordText.text = if (mIsShowingFirstPart) {
            mWord?.first(mMode)
        } else {
            mWord?.second(mMode)
        }
    }

    private suspend fun flipCard() {
        Log.d(WORD_CARD_VIEW, "flipped")
        val view = binding.root.parent as View
        val scale = context.resources.displayMetrics.density
        val cameraDist = CAMERA_MULTIPLIER * scale
        val storedElevation = view.elevation
        view.elevation = 0f
        view.cameraDistance = cameraDist
        view.animate()
            .rotationY(RIGHT_ANGLE)
            .setDuration(HALF_OF_FLIP_ANIMATION)
            .start()
        withContext(Dispatchers.Main) {
            delay(HALF_OF_FLIP_ANIMATION)
            updateText()
            view.rotationY = -RIGHT_ANGLE
            view.animate()
                .rotationY(0f)
                .setDuration(HALF_OF_FLIP_ANIMATION)
                .start()
            delay(HALF_OF_FLIP_ANIMATION)
            view.elevation = storedElevation
        }
    }
}
