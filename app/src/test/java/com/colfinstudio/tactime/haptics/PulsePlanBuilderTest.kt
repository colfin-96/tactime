package com.colfinstudio.tactime.haptics

import com.colfinstudio.tactime.time.InterpretedTime
import java.time.LocalTime
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PulsePlanBuilderTest {
    private val builder = PulsePlanBuilder()

    @Test
    fun `builds a single group for top of the hour`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 0))

        assertEquals(listOf(3), plan.groups.map { it.pulseCount })
    }

    @Test
    fun `builds separate groups for hour and quarter`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 1))

        assertEquals(listOf(3, 1), plan.groups.map { it.pulseCount })
    }

    @Test
    fun `supports three quarter pulses for forty five minutes`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 3))

        assertEquals(listOf(3, 3), plan.groups.map { it.pulseCount })
    }

    @Test
    fun `preserves twelve pulses for midnight or noon`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 12, quarterPulseCount = 0))

        assertEquals(12, plan.groups.single().pulseCount)
    }

    @Test
    fun `uses the balanced timing profile by default`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 0))

        assertEquals(PulseTimingProfile.Balanced, plan.timingProfile)
    }

    @Test
    fun `starts with a distinct lead in pulse before the countable pulses`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 0))

        assertArrayEquals(
            longArrayOf(
                0L,
                320L,
                320L,
                140L,
                110L,
                140L,
                110L,
                140L,
            ),
            plan.toWaveformTimings(),
        )
    }

    @Test
    fun `creates a waveform with the tuned separator between groups after the lead in`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 2))

        assertArrayEquals(
            longArrayOf(
                0L,
                320L,
                320L,
                140L,
                110L,
                140L,
                110L,
                140L,
                440L,
                140L,
                110L,
                140L,
            ),
            plan.toWaveformTimings(),
        )
    }

    @Test
    fun `keeps the separator longer than the gap within a group`() {
        val timingProfile = builder.build(
            interpretedTime(hourPulseCount = 3, quarterPulseCount = 1),
        ).timingProfile

        assertTrue(timingProfile.groupPauseDurationMs > timingProfile.pulseGapDurationMs)
        assertTrue(timingProfile.leadInPulseDurationMs > timingProfile.pulseDurationMs)
        assertTrue(timingProfile.leadInPauseDurationMs > timingProfile.pulseGapDurationMs)
    }

    private fun interpretedTime(
        hourPulseCount: Int,
        quarterPulseCount: Int,
    ) = InterpretedTime(
        roundedTime = LocalTime.of(3, quarterPulseCount * 15),
        hourPulseCount = hourPulseCount,
        quarterPulseCount = quarterPulseCount,
        formattedTime = "3:00 PM",
    )
}
