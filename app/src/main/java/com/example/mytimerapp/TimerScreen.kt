package com.example.mytimerapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel()
) {
    val timerState by viewModel.timerState.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 显示剩余时间
        Text(
            text = formatTime(timerState.remainingSeconds),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 时间选择器
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 小时选择器
//            NumberPicker(
//                value = timerState.totalSeconds / 3600,
//                onValueChange = { hours ->
//                    viewModel.setTime(hours, timerState.totalSeconds % 3600 / 60,
//                        timerState.totalSeconds % 60)
//                },
//                range = 0..23
//            )
//
            // 分钟选择器
            NumberPicker(
                value = (timerState.totalSeconds % 3600) / 60,
                onValueChange = { minutes ->
                    viewModel.setTime(
                        timerState.totalSeconds / 3600,
                        minutes,
                        timerState.totalSeconds % 60
                    )
                },
                range = 0..59
            )
            
            // 秒数选择器
            NumberPicker(
                value = timerState.totalSeconds % 60,
                onValueChange = { seconds ->
                    viewModel.setTime(
                        timerState.totalSeconds / 3600,
                        (timerState.totalSeconds % 3600) / 60,
                        seconds
                    )
                },
                range = 0..59
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                if (timerState.isRunning) viewModel.stopTimer()
                else viewModel.startTimer()
            },
            enabled = timerState.totalSeconds > 0 || timerState.isRunning
        ) {
            Text(if (timerState.isRunning) "Stop" else "Start")
        }
    }
}

// 格式化时间的辅助函数
private fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
//    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { 
                if (value > range.first) onValueChange(value - 1)
            }
        ) {
            Icon(Icons.Default.Remove, "减少")
        }
        
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium
        )
        
        IconButton(
            onClick = { 
                if (value < range.last) onValueChange(value + 1)
            }
        ) {
            Icon(Icons.Default.Add, "增加")
        }
    }
} 