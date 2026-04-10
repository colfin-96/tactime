package com.colfinstudio.tactime.haptics

import com.colfinstudio.tactime.time.InterpretedTime
import java.time.LocalTime
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
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
    fun `creates a waveform with a longer separator between groups`() {
        val plan = builder.build(interpretedTime(hourPulseCount = 3, quarterPulseCount = 2))

        assertArrayEquals(
            longArrayOf(
                0L,
                120L,
                120L,
                120L,
                120L,
                120L,
                360L,
                120L,
                120L,
                120L,
            ),
            plan.toWaveformTimings(),
        )
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
