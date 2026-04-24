package dev.lackluster.mihelper.hook.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dev.lackluster.mihelper.data.Constants.ACTION_FLOATING_WINDOW
import dev.lackluster.mihelper.data.Constants.ACTION_HOME
import dev.lackluster.mihelper.data.Constants.ACTION_NOTIFICATIONS
import dev.lackluster.mihelper.data.Constants.ACTION_QUICK_SETTINGS
import dev.lackluster.mihelper.data.Constants.ACTION_RECENTS
import dev.lackluster.mihelper.data.Constants.ACTION_SCREENSHOT
import dev.lackluster.mihelper.data.Constants.ACTION_SCROLL_TO_TOP
import dev.lackluster.mihelper.data.Constants.PER_MIUI_INTERNAL_API
import dev.lackluster.mihelper.utils.MLog

@SuppressLint("PrivateApi")
object CommonGesture {
    private const val TAG = "CommonGesture"

    /*
     * 0 -> DEFAULT;
     * 1 -> SYSTEM_ACTION_NOTIFICATIONS;
     * 2 -> SYSTEM_ACTION_QUICK_SETTINGS;
     * 3 -> SYSTEM_ACTION_LOCK_SCREEN;
     * 4 -> CAPTURE_SCREENSHOT;
     * 5 -> SYSTEM_ACTION_HOME;
     * 6 -> SYSTEM_ACTION_RECENTS;
     * 7 -> MI_AI_SCREEN;
     * 8 -> MI_AI_WAKE_UP;
     * 9 -> FLOATING_WINDOW;
     * 10 -> SCROLL_TO_TOP; // OS3.0.300+
     * 11 -> DISABLED;
     */
    fun doAction(appContext: Context, action: Int) {
        MLog.d(TAG) { "doAction action $action" }
        when (action) {
            3 -> {
                appContext.sendBroadcast(
                    Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE")
                        .putExtra("com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID", 10)
                )
            }
            4 -> {
                appContext.sendBroadcast(Intent(ACTION_SCREENSHOT))
            }
            7,8 -> {
                appContext.startForegroundService(
                    Intent("android.intent.action.ASSIST").apply {
                        setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.VoiceService")
                        putExtra("triggerFrom", "MiuiHome")
                        putExtra(
                            "voice_assist_function_key",
                            if (action == 7) "start_screen_recognition"
                            else "wake_up_voice_assist"
                        )
                        putExtra(
                            "triggerType",
                            if (action == 7) "NavLongPress"
                            else "NavDoubleClick"
                        )
                        putExtra(
                            "voice_assist_start_from_key",
                            if (action == 7) "long_press_fullscreen_gesture_line"
                            else "double_click_fullscreen_gesture_line"
                        )
                    }
                )
            }
            else -> {
                val intent = when(action) {
                    1 -> Intent(ACTION_NOTIFICATIONS)
                    2 -> Intent(ACTION_QUICK_SETTINGS)
                    5 -> Intent(ACTION_HOME)
                    6 -> Intent(ACTION_RECENTS)
                    9 -> Intent(ACTION_FLOATING_WINDOW)
                    10 -> Intent(ACTION_SCROLL_TO_TOP)
                    else -> return
                }
                appContext.sendBroadcast(intent, PER_MIUI_INTERNAL_API)
            }
        }
    }
}