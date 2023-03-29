package com.nafanya.words.feature.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.nafanya.words.feature.word.Mode
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsProvider @Inject constructor(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    fun initialize() {
        tts = TextToSpeech(context, this)
    }

    fun resetLocale(mode: Mode, isVoicingFirstPart: Boolean) {
        val locale = if (isVoicingFirstPart && mode is Mode.WordToTranslation ||
            !isVoicingFirstPart && mode is Mode.TranslationToWord
        ) {
            Locale.JAPAN
        } else {
            Locale("ru")
        }
        tts!!.language = locale
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Tts", "successfully started tts")
        } else {
            Log.e("Tts", "an error occurred")
        }
    }

    fun speak(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun releaseTts() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }
}
