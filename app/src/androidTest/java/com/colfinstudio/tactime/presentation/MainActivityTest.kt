package com.colfinstudio.tactime.presentation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.colfinstudio.tactime.haptics.HapticsPlaybackResult
import com.colfinstudio.tactime.haptics.HapticsPlayer
import com.colfinstudio.tactime.time.TimeInterpreter
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun tellTimeShowsTheInterpretedTimeAfterPlayback() {
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                WearApp(
                    timeInterpreter = fixedTimeInterpreter("2026-04-10T15:22:00Z"),
                    hapticsPlayer = HapticsPlayer { HapticsPlaybackResult.Played },
                )
            }
        }

        composeRule.onNodeWithTag(TELL_TIME_BUTTON_TAG).performClick()

        composeRule.onNodeWithTag(STATUS_TEXT_TAG).assertTextEquals("About 3:15 PM")
    }

    @Test
    fun tellTimeShowsFallbackCopyWhenHapticsAreUnavailable() {
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                WearApp(
                    timeInterpreter = fixedTimeInterpreter("2026-04-10T23:53:00Z"),
                    hapticsPlayer = HapticsPlayer {
                        HapticsPlaybackResult.Unavailable("No haptics")
                    },
                )
            }
        }

        composeRule.onNodeWithTag(TELL_TIME_BUTTON_TAG).performClick()

        composeRule.onNodeWithTag(STATUS_TEXT_TAG)
            .assertTextEquals("About 12:00 AM (no vibration)")
    }

    private fun fixedTimeInterpreter(instant: String) = TimeInterpreter(
        clock = Clock.fixed(Instant.parse(instant), ZoneId.of("UTC")),
    )
}
