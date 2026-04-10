package com.colfinstudio.tactime.haptics

import com.colfinstudio.tactime.time.InterpretedTime

class PulsePlanBuilder {
    fun build(interpretedTime: InterpretedTime): PulsePlan {
        val groups = buildList {
            add(PulseGroup(interpretedTime.hourPulseCount))

            if (interpretedTime.quarterPulseCount > 0) {
                add(PulseGroup(interpretedTime.quarterPulseCount))
            }
        }

        return PulsePlan(groups = groups)
    }
}
