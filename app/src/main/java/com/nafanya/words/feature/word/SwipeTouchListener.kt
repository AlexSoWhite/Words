package com.nafanya.words.feature.word

import android.content.Context
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SwipeTouchListener(private val context: Context) : View.OnTouchListener {

    private var mayBeClick = false

    private var startX: Float = 0f

    private var onSwipeLeftCallback: (() -> Unit)? = null

    private var onSwipeRightCallback: (() -> Unit)? = null

    companion object {
        const val SWIPE_THRESHOLD = 200
        const val SINGLE_MOVE_THRESHOLD = 100
        const val RETURN_ANIMATION_DURATION = 150L
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val displayMetrics = context.resources.displayMetrics
        val viewWidth = view.width
        val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (viewWidth / 2)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mayBeClick = true
                startX = event.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                // get the new coordinate of the event on X-axis
                val newX = event.rawX

                if (
                    abs(startX - newX) < viewWidth / 2 &&
                    abs(startX - newX) > SINGLE_MOVE_THRESHOLD
                ) {
                    mayBeClick = false
                    view.animate()
                        .x(
                            newX - startX
                        )
                        .setDuration(0)
                        .start()
                }
            }

            MotionEvent.ACTION_UP -> {
                val currentX = event.rawX
                view.animate()
                    .x(cardStart)
                    .setDuration(RETURN_ANIMATION_DURATION)
                    .start()

                if (abs(currentX - startX) > SWIPE_THRESHOLD) {
                    if (currentX > startX) {
                        onSwipeRightCallback?.invoke()
                    } else {
                        onSwipeLeftCallback?.invoke()
                    }
                }

                if (mayBeClick) {
                    view.performClick()
                }
            }
        }
        return true
    }

    fun setOnSwipeRightListener(callback: () -> Unit) {
        onSwipeRightCallback = callback
    }

    fun setOnSwipeLeftListener(callback: () -> Unit) {
        onSwipeLeftCallback = callback
    }
}
