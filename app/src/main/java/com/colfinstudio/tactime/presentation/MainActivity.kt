package com.colfinstudio.tactime.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.colfinstudio.tactime.presentation.theme.TacTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    TacTimeTheme {
        var statusText by remember { mutableStateOf("Ready") }

        ScreenScaffold { contentPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "TacTime",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Tap to request the time",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
                )
                Button(
                    onClick = {
                        statusText = "Tell Time pressed"
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Tell Time")
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
