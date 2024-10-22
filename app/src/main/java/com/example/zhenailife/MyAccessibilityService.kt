package com.example.zhenailife

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.WindowManager
import android.widget.FrameLayout

class MyAccessibilityService : AccessibilityService() {

    private var filterView: FrameLayout? = null
    private var filterEnabled = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        // 初始化滤镜或其他逻辑
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // 处理无障碍事件（不需要修改）
    }

    override fun onInterrupt() {
        // 当服务中断时调用
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 处理从 MainActivity 传递过来的意图
        filterEnabled = intent?.getBooleanExtra("FILTER_ENABLED", false) ?: false
        if (filterEnabled) {
            enableFilter()
        } else {
            disableFilter()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun enableFilter() {
        if (filterView == null) {
            filterView = FrameLayout(this)
            filterView!!.setBackgroundColor(Color.argb(100, 125, 102, 8))  // 土黄色滤镜
            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                5000, // 原本是 WindowManager.LayoutParams.MATCH_PARENT，這邊直接寫死超長覆蓋
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, // 這兩個 Flag 很重要
                PixelFormat.TRANSLUCENT
            )
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.addView(filterView, layoutParams)
        }
    }

    private fun disableFilter() {
        if (filterView != null) {
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.removeView(filterView)
            filterView = null
        }
    }
}
