package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

data class ActiveAlert(
    val id: Long = System.currentTimeMillis(),
    val studentName: String,
    val stageTitle: String,
    val voiceMessage: String,
    val timestamp: Long = System.currentTimeMillis()
)

class VoiceAlertManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var alertLoopJob: Job? = null

    private val _activeAlert = MutableStateFlow<ActiveAlert?>(null)
    val activeAlert: StateFlow<ActiveAlert?> = _activeAlert.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 95)
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "Init error", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                // Try Hindi first, then default to English or device locale
                val hindiLocale = Locale("hi", "IN")
                val result = engine.setLanguage(hindiLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.language = Locale.ENGLISH
                }
                engine.setPitch(1.05f)
                engine.setSpeechRate(0.92f)
                isTtsReady = true
            }
        }
    }

    /**
     * Plays a pleasant harmonic alert chime tune: high-low-high triad
     */
    fun playSuperTune() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
            scope.launch {
                delay(180)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 280)
            }
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "Tune playback failed", e)
        }
    }

    /**
     * Triggers vibration pattern
     */
    fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 200, 150, 200), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 200, 150, 200), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 200, 150, 200), -1)
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "Vibrate error", e)
        }
    }

    /**
     * Speaks once without looping
     */
    fun speakOnce(message: String) {
        playSuperTune()
        triggerVibration()
        scope.launch {
            delay(400) // let tune sound first
            if (isTtsReady) {
                tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "alert_${System.currentTimeMillis()}")
            }
        }
    }

    /**
     * Starts persistent voice alert loop for Parent until dismissed by tapping OK
     */
    fun startPersistentAlert(studentName: String, stageTitle: String, message: String) {
        // Stop any prior loop
        stopPersistentAlert()

        val alert = ActiveAlert(
            studentName = studentName,
            stageTitle = stageTitle,
            voiceMessage = message
        )
        _activeAlert.value = alert

        alertLoopJob = scope.launch {
            while (isActive && _activeAlert.value != null) {
                playSuperTune()
                triggerVibration()
                delay(400)
                if (isTtsReady) {
                    tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "loop_tts")
                }
                // Wait 4.8 seconds before repeating melody & speech until OK is tapped
                delay(4800)
            }
        }
    }

    /**
     * Parent taps OK to acknowledge and stop ringing
     */
    fun stopPersistentAlert() {
        alertLoopJob?.cancel()
        alertLoopJob = null
        _activeAlert.value = null
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "TTS stop error", e)
        }
    }

    fun release() {
        stopPersistentAlert()
        try {
            tts?.shutdown()
            toneGenerator?.release()
        } catch (e: Exception) {
            Log.e("VoiceAlertManager", "Release error", e)
        }
    }
}
