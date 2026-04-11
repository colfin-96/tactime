package com.colfinstudio.tactime.haptics

data class PulseGroup(
    val pulseCount: Int,
)

data class PulseTimingProfile(
    val leadInPulseDurationMs: Long,
    val leadInPauseDurationMs: Long,
    val pulseDurationMs: Long,
    val pulseGapDurationMs: Long,
    val groupPauseDurationMs: Long,
) {
    init {
        require(leadInPulseDurationMs > 0) { "Lead-in pulse duration must be positive." }
        require(leadInPauseDurationMs >= 0) { "Lead-in pause duration cannot be negative." }
        require(pulseDurationMs > 0) { "Pulse duration must be positive." }
        require(pulseGapDurationMs >= 0) { "Pulse gap duration cannot be negative." }
        require(groupPauseDurationMs >= 0) { "Group pause duration cannot be negative." }
    }

    companion object {
        val Balanced = PulseTimingProfile(
            leadInPulseDurationMs = 320L,
            leadInPauseDurationMs = 320L,
            pulseDurationMs = 140L,
            pulseGapDurationMs = 110L,
            groupPauseDurationMs = 440L,
        )
    }
}

data class PulsePlan(
    val groups: List<PulseGroup>,
    val timingProfile: PulseTimingProfile = PulseTimingProfile.Balanced,
) {
    init {
        require(groups.isNotEmpty()) { "Pulse plan must contain at least one group." }
        require(groups.all { it.pulseCount > 0 }) { "Each pulse group must contain at least one pulse." }
    }

    fun toWaveformTimings(): LongArray {
        val timings = mutableListOf(
            0L,
            timingProfile.leadInPulseDurationMs,
            timingProfile.leadInPauseDurationMs,
        )

        groups.forEachIndexed { groupIndex, group ->
            repeat(group.pulseCount) { pulseIndex ->
                timings += timingProfile.pulseDurationMs
                val isLastPulseInGroup = pulseIndex == group.pulseCount - 1
                val isLastGroup = groupIndex == groups.lastIndex

                if (!isLastPulseInGroup) {
                    timings += timingProfile.pulseGapDurationMs
                } else if (!isLastGroup) {
                    timings += timingProfile.groupPauseDurationMs
                }
            }
        }

        return timings.toLongArray()
    }
}
