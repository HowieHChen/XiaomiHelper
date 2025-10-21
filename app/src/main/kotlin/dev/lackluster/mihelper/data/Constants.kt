package dev.lackluster.mihelper.data

object Constants {
    const val CMD_LSPOSED = "am broadcast -a android.telephony.action.SECRET_CODE -d android_secret_code://5776733 android"
    const val PER_MIUI_INTERNAL_API = "miui.permission.USE_INTERNAL_GENERAL_API"
    const val ACTION_PREFIX = "hyperhelper.action."
    const val ACTION_NOTIFICATIONS = ACTION_PREFIX + "SYSTEM_ACTION_NOTIFICATIONS"
    const val ACTION_QUICK_SETTINGS = ACTION_PREFIX + "SYSTEM_ACTION_QUICK_SETTINGS"
    const val ACTION_SCREENSHOT = "android.intent.action.CAPTURE_SCREENSHOT"
    const val ACTION_HOME = ACTION_PREFIX + "SYSTEM_ACTION_HOME"
    const val ACTION_RECENTS = ACTION_PREFIX + "SYSTEM_ACTION_RECENTS"

    const val UI_MODE_TYPE_SCALE_EXTRAL_SMALL = 10 // 0.9f
    const val UI_MODE_TYPE_SCALE_SMALL = 12 // 0.9f
    const val UI_MODE_TYPE_SCALE_MEDIUM = 13 // 1.0f
    const val UI_MODE_TYPE_SCALE_LARGE = 14 // 1.1f
    const val UI_MODE_TYPE_SCALE_HUGE = 15 // 1.25f
    const val UI_MODE_TYPE_SCALE_GODZILLA = 11 // 1.45f
    const val UI_MODE_TYPE_SCALE_170 = 8 // 1.7f
    const val UI_MODE_TYPE_SCALE_200 = 9 // 2.0f
}
