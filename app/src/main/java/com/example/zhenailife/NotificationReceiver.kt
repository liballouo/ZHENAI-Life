package com.example.zhenailife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "com.example.zhenailife.ACTION_SNOOZE" -> {
                // 在這裡處理 "再五分鐘" 的邏輯
                Toast.makeText(context, "Snooze for 5 more minutes", Toast.LENGTH_SHORT).show()
                // 您可以在這裡重新啟動倒數計時
                (context as? MainActivity)?.startCountdown(5 * 60 * 1000)
            }
        }
    }
} 