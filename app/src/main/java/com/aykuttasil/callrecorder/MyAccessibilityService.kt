package com.aykuttasil.callrecorder

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.aykuttasil.callrecord.helper.LogUtils

class MyAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtils.d("onServiceConnected")
    }
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        LogUtils.d("onAccessibilityEvent")
    }

    override fun onInterrupt() {
        LogUtils.d("onInterrupt")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtils.d("onUnbind")
        return super.onUnbind(intent)
    }
}