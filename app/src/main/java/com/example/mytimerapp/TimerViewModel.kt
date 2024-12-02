package com.example.mytimerapp

import android.app.Application
import android.content.*
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimerViewModel(
    private val application: Application
) : AndroidViewModel(application) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerService: TimerService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerService = (service as TimerService.LocalBinder).getService().apply {
                // 設置回調來接收計時器更新
                setTimerCallback { remainingSeconds ->
                    _timerState.update { 
                        it.copy(remainingSeconds = remainingSeconds.toInt())
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
        }
    }

    init {
        bindTimerService()
    }

    private fun bindTimerService() {
        Intent(application, TimerService::class.java).also { intent ->
            application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun setTime(hours: Int, minutes: Int, seconds: Int) {
        _timerState.update { 
            it.copy(
                totalSeconds = hours * 3600 + minutes * 60 + seconds,
                remainingSeconds = hours * 3600 + minutes * 60 + seconds
            )
        }
    }

    fun startTimer() {
        // 啟動服務
        val serviceIntent = Intent(application, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.startForegroundService(serviceIntent)
        } else {
            application.startService(serviceIntent)
        }
        
        _timerState.update { it.copy(isRunning = true) }
        timerService?.startTimer(timerState.value.totalSeconds)
    }

    fun stopTimer() {
        _timerState.update { it.copy(isRunning = false) }
        timerService?.stopTimer()
        // 停止服務
        val serviceIntent = Intent(application, TimerService::class.java)
        application.stopService(serviceIntent)
    }

    override fun onCleared() {
        super.onCleared()
        application.unbindService(serviceConnection)
    }
}

data class TimerState(
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false
) 