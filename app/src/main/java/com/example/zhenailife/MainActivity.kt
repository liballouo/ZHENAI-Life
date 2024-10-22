package com.example.zhenailife

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.util.Log
import com.example.zhenailife.ui.theme.ZHENAILifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZHENAILifeTheme {
                MainScreen(
                    onRequestAccessibilityPermission = {
                        requestAccessibilityPermission()
                    },
                    onCheckServiceStatus = {
                        val isEnabled = isAccessibilityEnabled(this)
                        if (isEnabled) {
                            // 已启用
                            showToast("Accessibility Service is enabled")
                        } else {
                            // 未启用
                            showToast("Accessibility Service is not enabled")
                        }
                    }
                )
            }
        }
    }

    private fun requestAccessibilityPermission() {
        // 引导用户前往系统的无障碍设置界面
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    private fun isAccessibilityEnabled(context: Context): Boolean {
        var accessibilityEnabled = 0
        val ACCESSIBILITY_SERVICE_NAME = "com.example.zhenailife/com.example.zhenailife.MyAccessibilityService"

        try {
            // 检查无障碍功能是否启用
            accessibilityEnabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.d("AccessibilityCheck", "ACCESSIBILITY: $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.d("AccessibilityCheck", "Error finding setting: ${e.message}")
        }

        // 创建一个 ':' 分隔符的字符串拆分器
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        // 检查无障碍服务是否开启
        if (accessibilityEnabled == 1) {
            Log.d("AccessibilityCheck", "***ACCESSIBILITY IS ENABLED***")

            // 获取当前启用的无障碍服务列表
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            Log.d("AccessibilityCheck", "Setting: $settingValue")

            // 如果有启用的服务，检查是否包含我们的服务
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.d("AccessibilityCheck", "Service: $accessibilityService")

                    // 比较服务名是否匹配
                    if (accessibilityService.equals(ACCESSIBILITY_SERVICE_NAME, ignoreCase = true)) {
                        Log.d("AccessibilityCheck", "We've found the correct service!")
                        return true
                    }
                }
            }

            Log.d("AccessibilityCheck", "***END***")
        } else {
            Log.d("AccessibilityCheck", "***ACCESSIBILITY IS DISABLED***")
        }

        return false
    }

}

@Composable
fun MainScreen(
    onRequestAccessibilityPermission: () -> Unit,
    onCheckServiceStatus: () -> Unit
) {
    // 主界面内容
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 检查服务是否启用的按钮
        Button(onClick = { onCheckServiceStatus() }) {
            Text(text = "Check Accessibility Service Status")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 引导启用服务的按钮
        Button(onClick = { onRequestAccessibilityPermission() }) {
            Text(text = "Enable Accessibility Service")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZHENAILifeTheme {
        MainScreen({}, {})
    }
}
