package edu.farmingdale.threadsexample.countdowntimer

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private var timerJob: Job? = null

    // Values selected in time picker
    var selectedHour by mutableIntStateOf(0)
        private set
    var selectedMinute by mutableIntStateOf(0)
        private set
    var selectedSecond by mutableIntStateOf(0)
        private set

    // Total milliseconds when timer starts
    var totalMillis by mutableLongStateOf(0L)
        private set

    // Time that remains
    var remainingMillis by mutableLongStateOf(0L)
        private set

    // Timer's running status
    var isRunning by mutableStateOf(false)
        private set

    // Time in hh:mm:ss format
    val formattedTime: String
        get() = formatTime(remainingMillis)

    fun selectTime(hour: Int, min: Int, sec: Int) {
        selectedHour = hour
        selectedMinute = min
        selectedSecond = sec
    }

    fun startTimer(context: Context) {
        // Convert hours, minutes, and seconds to milliseconds
        totalMillis = (selectedHour * 60 * 60 + selectedMinute * 60 + selectedSecond) * 1000L

        // Start coroutine that makes the timer count down
        if (totalMillis > 0) {
            isRunning = true
            remainingMillis = totalMillis

            // Trigger the TimerWorker to post notifications in the background
            startTimerWorker(context)

            timerJob = viewModelScope.launch {
                while (remainingMillis > 0) {
                    delay(1000)
                    remainingMillis -= 1000
                }

                isRunning = false
            }
        }
    }

    fun cancelTimer() {
        if (isRunning) {
            timerJob?.cancel()
            isRunning = false
            remainingMillis = 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    // Helper function to format remaining time into hh:mm:ss
    private fun formatTime(millis: Long): String {
        val hours = (millis / (1000 * 60 * 60)) % 24
        val minutes = (millis / (1000 * 60)) % 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Function to trigger TimerWorker
    private fun startTimerWorker(context: Context) {
        val timerWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(
                workDataOf(KEY_MILLIS_REMAINING.toString() to remainingMillis)
            )
            .build()

        // Enqueue the work request to post notifications
        WorkManager.getInstance(context).enqueue(timerWorkRequest)
    }
}