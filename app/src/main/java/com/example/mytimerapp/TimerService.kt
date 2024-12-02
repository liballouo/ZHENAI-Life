package com.example.mytimerapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService : Service() {
    private val binder = LocalBinder()
    private var countDownTimer: CountDownTimer? = null
    private lateinit var notificationManager: NotificationManager
    private var timerCallback: ((Long) -> Unit)? = null
    
    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun setTimerCallback(callback: (Long) -> Unit) {
        timerCallback = callback
    }

    fun startTimer(totalSeconds: Int) {
        initialTotalSeconds = totalSeconds
        
        val notification = createNotification(
            "時間正在流逝...",
            "剩餘時間: ${formatTime(totalSeconds)}"
        )
        
        startForeground(NOTIFICATION_ID, notification)
        
        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                updateNotification(seconds)
                timerCallback?.invoke(seconds)
            }

            override fun onFinish() {
                showCompletionNotification()
                timerCallback?.invoke(0)
                stopSelf()
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "計時器通知",
                // NotificationManager.IMPORTANCE_HIGH
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "顯示計時器進度"
                enableLights(true)
                enableVibration(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setProgress(100, 100, false)
            .build()
    }

    private fun updateNotification(remainingSeconds: Long) {
        val hours = remainingSeconds / 3600
        val minutes = (remainingSeconds % 3600) / 60
        val seconds = remainingSeconds % 60
        
        val timeString = when {
//            hours > 0 -> String.format("%d時%02d分%02d秒", hours, minutes, seconds)
            minutes > 0 -> String.format("%d分%02d秒", minutes, seconds)
            else -> String.format("%d秒", seconds)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("時間正在流逝...")
            .setContentText("剩餘時間: $timeString")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(100, ((remainingSeconds.toFloat() / initialTotalSeconds) * 100).toInt(), false)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showCompletionNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("計時完成")
            .setContentText("時間到!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            // .setPriority(NotificationCompat.PRIORITY_HIGH)
            // .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "TimerChannel"
    }

    private var initialTotalSeconds: Int = 0

    private fun formatTime(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return when {
//            hours > 0 -> String.format("%d時%02d分%02d秒", hours, minutes, seconds)
            minutes > 0 -> String.format("%d分%02d秒", minutes, seconds)
            else -> String.format("%d秒", seconds)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
} 