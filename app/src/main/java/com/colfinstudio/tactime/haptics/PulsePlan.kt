package com.colfinstudio.tactime.haptics

data class PulseGroup(
    val pulseCount: Int,
)

data class PulsePlan(
    val groups: List<PulseGroup>,
    val pulseDurationMs: Long = DEFAULT_PULSE_DURATION_MS,
    val pulseGapDurationMs: Long = DEFAULT_PULSE_GAP_DURATION_MS,
    val groupPauseDurationMs: Long = DEFAULT_GROUP_PAUSE_DURATION_MS,
) {
    init {
        require(groups.isNotEmpty()) { "Pulse plan must contain at least one group." }
        require(groups.all { it.pulseCount > 0 }) { "Each pulse group must contain at least one pulse." }
    }

    fun toWaveformTimings(): LongArray {
        val timings = mutableListOf(0L)

        groups.forEachIndexed { groupIndex, group ->
            repeat(group.pulseCount) { pulseIndex ->
                timings += pulseDurationMs
                val isLastPulseInGroup = pulseIndex == group.pulseCount - 1
                val isLastGroup = groupIndex == groups.lastIndex

                if (!isLastPulseInGroup) {
                    timings += pulseGapDurationMs
                } else if (!isLastGroup) {
                    timings += groupPauseDurationMs
                }
            }
        }

        return timings.toLongArray()
    }

    companion object {
        const val DEFAULT_PULSE_DURATION_MS = 120L
        const val DEFAULT_PULSE_GAP_DURATION_MS = 120L
        const val DEFAULT_GROUP_PAUSE_DURATION_MS = 360L
    }
}
