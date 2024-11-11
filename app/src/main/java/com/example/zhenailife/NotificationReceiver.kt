package com.example.zhenailife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "com.example.zhenailife.ACTION_SNOOZE" -> {
                // 發送全局廣播以通知 MainActivity
                val snoozeIntent = Intent("com.example.zhenailife.ACTION_SNOOZE_LOCAL")
                context?.sendBroadcast(snoozeIntent)
            }
        }
    }
} 