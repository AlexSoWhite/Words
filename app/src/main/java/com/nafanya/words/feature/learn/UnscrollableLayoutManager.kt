package com.nafanya.words.feature.learn

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class UnscrollableLayoutManager(
    context: Context
) : LinearLayoutManager(context) {

    override fun canScrollVertically(): Boolean {
        return false
    }
}
