package com.colfinstudio.tactime.time

import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeInterpreterTest {
    private val interpreter = TimeInterpreter(
        clock = Clock.fixed(Instant.parse("2026-04-10T15:22:00Z"), ZoneId.of("UTC")),
    )

    @Test
    fun `interprets morning quarter hour`() {
        val interpreted = interpreter.interpret(LocalTime.of(3, 0))

        assertEquals(3, interpreted.hourPulseCount)
        assertEquals(0, interpreted.quarterPulseCount)
        assertEquals("3:00 AM", interpreted.formattedTime)
    }

    @Test
    fun `interprets afternoon quarter hour in 12 hour form`() {
        val interpreted = interpreter.interpret(LocalTime.of(15, 30))

        assertEquals(3, interpreted.hourPulseCount)
        assertEquals(2, interpreted.quarterPulseCount)
        assertEquals("3:30 PM", interpreted.formattedTime)
    }

    @Test
    fun `midnight uses 12 instead of zero`() {
        val interpreted = interpreter.interpret(LocalTime.MIDNIGHT)

        assertEquals(12, interpreted.hourPulseCount)
        assertEquals("12:00 AM", interpreted.formattedTime)
    }

    @Test
    fun `noon uses 12 instead of zero`() {
        val interpreted = interpreter.interpret(LocalTime.NOON)

        assertEquals(12, interpreted.hourPulseCount)
        assertEquals("12:00 PM", interpreted.formattedTime)
    }

    @Test
    fun `rounds around quarter hour boundaries`() {
        assertEquals("3:00 AM", interpreter.interpret(LocalTime.of(3, 7)).formattedTime)
        assertEquals("3:15 AM", interpreter.interpret(LocalTime.of(3, 8)).formattedTime)
        assertEquals("3:15 AM", interpreter.interpret(LocalTime.of(3, 22)).formattedTime)
        assertEquals("3:30 AM", interpreter.interpret(LocalTime.of(3, 23)).formattedTime)
        assertEquals("3:30 AM", interpreter.interpret(LocalTime.of(3, 37)).formattedTime)
        assertEquals("3:45 AM", interpreter.interpret(LocalTime.of(3, 38)).formattedTime)
        assertEquals("3:45 AM", interpreter.interpret(LocalTime.of(3, 52)).formattedTime)
        assertEquals("4:00 AM", interpreter.interpret(LocalTime.of(3, 53)).formattedTime)
    }

    @Test
    fun `rounding can roll over to the next day`() {
        val interpreted = interpreter.interpret(LocalTime.of(23, 53))

        assertEquals(12, interpreted.hourPulseCount)
        assertEquals(0, interpreted.quarterPulseCount)
        assertEquals("12:00 AM", interpreted.formattedTime)
    }

    @Test
    fun `interpret now uses the injected clock`() {
        val interpreted = interpreter.interpretNow()

        assertEquals("3:15 PM", interpreted.formattedTime)
        assertEquals(3, interpreted.hourPulseCount)
        assertEquals(1, interpreted.quarterPulseCount)
    }
}
