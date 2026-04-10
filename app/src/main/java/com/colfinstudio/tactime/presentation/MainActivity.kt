package com.colfinstudio.tactime.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.colfinstudio.tactime.R
import com.colfinstudio.tactime.haptics.HapticsPlaybackResult
import com.colfinstudio.tactime.haptics.HapticsPlayer
import com.colfinstudio.tactime.haptics.PulsePlanBuilder
import com.colfinstudio.tactime.haptics.WearHapticsPlayer
import com.colfinstudio.tactime.presentation.theme.TacTimeTheme
import com.colfinstudio.tactime.time.TimeInterpreter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val timeInterpreter = remember { TimeInterpreter() }
            val context = LocalContext.current
            val hapticsPlayer = remember(context) { WearHapticsPlayer(context) }

            WearApp(
                timeInterpreter = timeInterpreter,
                hapticsPlayer = hapticsPlayer,
            )
        }
    }
}

@Composable
fun WearApp(
    timeInterpreter: TimeInterpreter,
    hapticsPlayer: HapticsPlayer,
) {
    TacTimeTheme {
        val context = LocalContext.current
        val pulsePlanBuilder = remember { PulsePlanBuilder() }
        val initialStatus = context.getString(R.string.status_ready)
        var statusText by remember { mutableStateOf(initialStatus) }

        ScreenScaffold { contentPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFF101418))
                        .padding(contentPadding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = context.getString(R.string.app_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFF5F7FA),
                )
                Text(
                    text = context.getString(R.string.tell_time_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFF5F7FA),
                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
                )
                Button(
                    onClick = {
                        val interpretedTime = timeInterpreter.interpretNow()
                        val pulsePlan = pulsePlanBuilder.build(interpretedTime)

                        statusText = when (hapticsPlayer.play(pulsePlan)) {
                            HapticsPlaybackResult.Played -> {
                                context.getString(
                                    R.string.status_about_time,
                                    interpretedTime.formattedTime,
                                )
                            }
                            is HapticsPlaybackResult.Unavailable -> {
                                context.getString(
                                    R.string.status_haptics_unavailable,
                                    interpretedTime.formattedTime,
                                )
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag(TELL_TIME_BUTTON_TAG),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF0F4F8),
                            contentColor = Color(0xFF101418),
                        ),
                ) {
                    Text(context.getString(R.string.tell_time_button))
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFF5F7FA),
                    modifier =
                        Modifier
                            .padding(top = 10.dp)
                            .testTag(STATUS_TEXT_TAG),
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp(
        timeInterpreter = remember { TimeInterpreter() },
        hapticsPlayer = HapticsPlayer { HapticsPlaybackResult.Played },
    )
}

const val TELL_TIME_BUTTON_TAG = "tell_time_button"
const val STATUS_TEXT_TAG = "status_text"
