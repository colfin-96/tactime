package com.colfinstudio.tactime.time

import java.time.LocalTime

data class InterpretedTime(
    val roundedTime: LocalTime,
    val hourPulseCount: Int,
    val quarterPulseCount: Int,
    val formattedTime: String,
)
