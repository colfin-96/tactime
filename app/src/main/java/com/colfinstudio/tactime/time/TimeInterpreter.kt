package com.colfinstudio.tactime.time

import java.time.Clock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimeInterpreter(
    private val clock: Clock = Clock.systemDefaultZone(),
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.US),
) {
    fun interpretNow(): InterpretedTime = interpret(LocalTime.now(clock))

    fun interpret(time: LocalTime): InterpretedTime {
        val roundedTime = roundToNearestQuarterHour(time)
        val hourPulseCount = roundedTime.hour.to12HourValue()
        val quarterPulseCount = roundedTime.minute / MINUTES_PER_QUARTER

        return InterpretedTime(
            roundedTime = roundedTime,
            hourPulseCount = hourPulseCount,
            quarterPulseCount = quarterPulseCount,
            formattedTime = roundedTime.format(formatter),
        )
    }

    private fun roundToNearestQuarterHour(time: LocalTime): LocalTime {
        val totalMinutes = (time.hour * MINUTES_PER_HOUR) + time.minute
        val remainder = totalMinutes % MINUTES_PER_QUARTER
        val roundedMinutes =
            if (remainder < ROUND_UP_THRESHOLD_MINUTES) {
                totalMinutes - remainder
            } else {
                totalMinutes + (MINUTES_PER_QUARTER - remainder)
            } % MINUTES_PER_DAY

        return LocalTime.of(
            roundedMinutes / MINUTES_PER_HOUR,
            roundedMinutes % MINUTES_PER_HOUR,
        )
    }

    private fun Int.to12HourValue(): Int = when (val remainder = this % 12) {
        0 -> 12
        else -> remainder
    }

    private companion object {
        const val MINUTES_PER_HOUR = 60
        const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR
        const val MINUTES_PER_QUARTER = 15
        const val ROUND_UP_THRESHOLD_MINUTES = 8
    }
}
