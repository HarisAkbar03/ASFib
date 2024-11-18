package edu.farmingdale.threadsexample.countdowntimer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.NumberPicker
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.farmingdale.threadsexample.R
import java.text.DecimalFormat
import java.util.Locale
import kotlin.text.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel = viewModel()
) {
    // Get the current context
    val context = LocalContext.current

    // Play sound when the timer reaches 0
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.bell) }

    // Calculate progress as a fraction of the total time
    val progress = if (timerViewModel.totalMillis > 0) {
        1f - (timerViewModel.remainingMillis / timerViewModel.totalMillis.toFloat())
    } else {
        0f
    }

    // Make text red and bold during the last 10 seconds
    val textColor = if (timerViewModel.remainingMillis <= 10000) Color.Red else Color.Black
    val textStyle = if (timerViewModel.remainingMillis <= 10000) FontWeight.Bold else FontWeight.Normal

    // Reset the timer
    fun resetTimer() {
        timerViewModel.cancelTimer()
        timerViewModel.selectTime(0, 0, 0)
    }

    // Handle the case when the timer reaches zero
    LaunchedEffect(timerViewModel.remainingMillis) {
        if (timerViewModel.remainingMillis <= 0 && !timerViewModel.isRunning) {
            // Play sound when the timer reaches zero
            mediaPlayer.start()
        }
    }

    // UI Layout
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .padding(20.dp)
                .size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular progress indicator while the timer is running
            if (timerViewModel.isRunning) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(150.dp),
                    strokeWidth = 8.dp
                )
            }

            // Display the remaining time as text
            Text(
                text = timerText(timerViewModel.remainingMillis),
                fontSize = 50.sp,
                color = textColor,
                fontWeight = textStyle
            )
        }

        // Time picker to select hours, minutes, and seconds
        TimePicker(
            hour = timerViewModel.selectedHour,
            min = timerViewModel.selectedMinute,
            sec = timerViewModel.selectedSecond,
            onTimePick = timerViewModel::selectTime
        )

        // Start/Cancel Button
        if (timerViewModel.isRunning) {
            Button(
                onClick = timerViewModel::cancelTimer,
                modifier = modifier.padding(50.dp)
            ) {
                Text("Cancel")
            }
        } else {
            Button(
                enabled = timerViewModel.selectedHour +
                        timerViewModel.selectedMinute +
                        timerViewModel.selectedSecond > 0,
                onClick = {
                    timerViewModel.startTimer(context)
                },
                modifier = modifier.padding(top = 50.dp)
            ) {
                Text("Start")
            }
        }

        // Reset Button
        Button(
            onClick = { resetTimer() },
            modifier = modifier.padding(top = 20.dp)
        ) {
            Text("Reset")
        }
    }
}

fun timerText(timeInMillis: Long): String {
    val duration = timeInMillis.milliseconds
    return String.format(
        "%02d:%02d:%02d",
        duration.inWholeHours, duration.inWholeMinutes % 60, duration.inWholeSeconds % 60
    )
}
// Function to handle time picker UI
@Composable
fun TimePicker(
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0,
    onTimePick: (Int, Int, Int) -> Unit = { _: Int, _: Int, _: Int -> }
) {
    var hourVal by remember { mutableIntStateOf(hour) }
    var minVal by remember { mutableIntStateOf(min) }
    var secVal by remember { mutableIntStateOf(sec) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Hours")
            NumberPickerWrapper(
                initVal = hourVal,
                maxVal = 99,
                onNumPick = {
                    hourVal = it
                    onTimePick(hourVal, minVal, secVal)
                }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            Text("Minutes")
            NumberPickerWrapper(
                initVal = minVal,
                onNumPick = {
                    minVal = it
                    onTimePick(hourVal, minVal, secVal)
                }
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Seconds")
            NumberPickerWrapper(
                initVal = secVal,
                onNumPick = {
                    secVal = it
                    onTimePick(hourVal, minVal, secVal)
                }
            )
        }
    }
}

// Function to handle the Android number picker UI
@Composable
fun NumberPickerWrapper(
    initVal: Int = 0,
    minVal: Int = 0,
    maxVal: Int = 59,
    onNumPick: (Int) -> Unit = {}
) {
    val numFormat = NumberPicker.Formatter { i: Int ->
        DecimalFormat("00").format(i)
    }

    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                setOnValueChangedListener { numberPicker, oldVal, newVal -> onNumPick(newVal) }
                minValue = minVal
                maxValue = maxVal
                value = initVal
                setFormatter(numFormat)
            }
        }
    )
}