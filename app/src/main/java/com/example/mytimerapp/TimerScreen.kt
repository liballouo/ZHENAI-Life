package com.example.mytimerapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel()
) {
    val timerState by viewModel.timerState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 顯示剩餘時間
        Text(
            text = formatTime(timerState.remainingSeconds),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 時間選擇器，使用 SlotMachinePicker 替代原先的 WheelNumberPicker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // 分鐘選擇器
            SlotMachinePicker(
                range = 0..59,
                initialValue = 0,
                onNumberSelected = { minutes ->
                    viewModel.setTime(
                        timerState.totalSeconds / 3600,
                        minutes,
                        timerState.totalSeconds % 60
                    )
                },
                modifier = Modifier.width(80.dp)
            )

            Text(":", style = MaterialTheme.typography.headlineLarge)

            // 秒數選擇器
            SlotMachinePicker(
                range = 0..59,
                onNumberSelected = { seconds ->
                    viewModel.setTime(
                        timerState.totalSeconds / 3600,
                        (timerState.totalSeconds % 3600) / 60,
                        seconds
                    )
                },
                modifier = Modifier.width(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (timerState.isRunning) viewModel.stopTimer()
                else viewModel.startTimer()
            },
            enabled = timerState.totalSeconds > 0 || timerState.isRunning,
            modifier = Modifier.width(200.dp)
        ) {
            Text(if (timerState.isRunning) "停止" else "開始")
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return when {
        hours > 0 -> String.format("%d時%02d分%02d秒", hours, minutes, seconds)
        minutes > 0 -> String.format("%d分%02d秒", minutes, seconds)
        else -> String.format("%d秒", seconds)
    }
} 