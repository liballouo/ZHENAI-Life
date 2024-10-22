package com.example.zhenailife

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout

class MyAccessibilityService : AccessibilityService() {

    private lateinit var filterView: FrameLayout
    private lateinit var windowManager: WindowManager

    override fun onServiceConnected() {
        super.onServiceConnected()

        // 初始化 WindowManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // 创建过滤器视图
        filterView = FrameLayout(this).apply {
            setBackgroundColor(Color.argb(150, 125, 102, 8))  // 土黄色滤镜，半透明
        }

        // 设置布局参数
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        // 将视图添加到 WindowManager
        windowManager.addView(filterView, layoutParams)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 处理你感兴趣的事件
    }

    override fun onInterrupt() {
        // 当服务被中断时处理
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除滤镜视图
        if (::filterView.isInitialized) {
            windowManager.removeView(filterView)
        }
    }
}
