package com.colfinstudio.tactime.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

sealed interface HapticsPlaybackResult {
    data object Played : HapticsPlaybackResult

    data class Unavailable(
        val reason: String,
    ) : HapticsPlaybackResult
}

fun interface HapticsPlayer {
    fun play(plan: PulsePlan): HapticsPlaybackResult
}

class WearHapticsPlayer(
    private val context: Context,
) : HapticsPlayer {
    override fun play(plan: PulsePlan): HapticsPlaybackResult {
        val vibrator = resolveVibrator()
            ?: return HapticsPlaybackResult.Unavailable("No vibrator service is available on this device.")

        if (!vibrator.hasVibrator()) {
            return HapticsPlaybackResult.Unavailable("This device does not report haptic support.")
        }

        vibrator.vibrate(VibrationEffect.createWaveform(plan.toWaveformTimings(), -1))
        return HapticsPlaybackResult.Played
    }

    private fun resolveVibrator(): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
}
