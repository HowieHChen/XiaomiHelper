package dev.lackluster.mihelper.data

import dev.lackluster.mihelper.R

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

    const val VARIABLE_FONT_REAL_FILE_NAME = "hyper_helper_vf_real.ttf"
    const val VARIABLE_FONT_DEFAULT_PATH = "/system/fonts/MiSansVF.ttf"

    object BatteryIndicator {
        const val STYLE_DEFAULT = 0
        const val STYLE_ICON_ONLY = 1
        const val STYLE_TEXT_IN = 2
        const val STYLE_LINE = 3
        const val STYLE_TEXT_OUT = 4
        const val STYLE_TEXT_ONLY = 5
        const val STYLE_HIDDEN = 6

        const val PERCENT_MARK_STYLE_DEFAULT = 0
        const val PERCENT_MARK_STYLE_DIGITAL = 1
        const val PERCENT_MARK_STYLE_HIDDEN = 2

        const val REAL_STYLE_ICON_ONLY = 0
        const val REAL_STYLE_TEXT_IN = 1
        const val REAL_STYLE_LINE = 2
        const val REAL_STYLE_TEXT_OUT = 3

        const val TAG_POSITION_PHONE = 0
        const val TAG_POSITION_KEYGUARD = 1
        const val TAG_POSITION_CONTROL_CENTER_FAKE = 5
        const val TAG_POSITION_CONTROL_CENTER = 6
    }

    val STATUS_BAR_ICONS_DEFAULT = listOf(
        "handle", "network_speed", "mute", "micphone", "headset", "mikey", "privacy_mode", "nfc", "gps",
        "missed_call", "managed_profile", "second_space", "ime", "cast", "location", "stealth", "tty",
        "alarm_clock", "vpn", "ethernet", "handle_battery", "bluetooth", "bluetooth_handsfree_battery",
        "hotspot", "sound_box_group", "stereo", "sound_box_screen", "sound_box", "wireless_headset",
        "zen", "volume", "dist_compute", "camera", "glasses", "car", "tv", "pc", "pad", "phone",
        "hd", "airplane", "mobile", "demo_mobile", "no_sim", "wifi", "demo_wifi",
    )

    val STATUS_BAR_ICONS_SWAP = listOf(
        "handle", "network_speed", "mute", "micphone", "headset", "mikey", "privacy_mode", "nfc", "gps",
        "missed_call", "managed_profile", "second_space", "ime", "cast", "location", "stealth", "tty",
        "alarm_clock", "vpn", "ethernet", "handle_battery", "bluetooth", "bluetooth_handsfree_battery",
        "hotspot", "sound_box_group", "stereo", "sound_box_screen", "sound_box", "wireless_headset",
        "zen", "volume", "dist_compute", "camera", "glasses", "car", "tv", "pc", "pad", "phone",
        "wifi", "demo_wifi", "hd", "airplane", "mobile", "demo_mobile", "no_sim",
    )


    val STATUS_BAR_ICON_SLOT_MAP = mutableMapOf(
        "handle" to StatusBarIconSlotWrap("handle", R.drawable.ic_stat_sys_unknown, 0),
        "network_speed" to StatusBarIconSlotWrap("network_speed", R.drawable.ic_stat_sys_net_speed, R.string.icon_tuner_network_net_speed),
        "mute" to StatusBarIconSlotWrap("mute", R.drawable.ic_stat_sys_unknown, 0),
        "micphone" to StatusBarIconSlotWrap("micphone", R.drawable.ic_stat_sys_micphone, 0),
        "headset" to StatusBarIconSlotWrap("headset", R.drawable.ic_stat_sys_headset, R.string.icon_tuner_other_headset),
        "mikey" to StatusBarIconSlotWrap("mikey", R.drawable.ic_stat_sys_unknown, 0),
        "privacy_mode" to StatusBarIconSlotWrap("privacy_mode", R.drawable.ic_stat_sys_unknown, 0),
        "nfc" to StatusBarIconSlotWrap("nfc", R.drawable.ic_stat_sys_nfc, R.string.icon_tuner_connect_nfc),
        "gps" to StatusBarIconSlotWrap("gps", R.drawable.ic_stat_sys_unknown, 0),
        "missed_call" to StatusBarIconSlotWrap("missed_call", R.drawable.ic_stat_sys_unknown, 0),
        "managed_profile" to StatusBarIconSlotWrap("managed_profile", R.drawable.ic_stat_sys_managed_profile, 0),
        "second_space" to StatusBarIconSlotWrap("second_space", R.drawable.ic_stat_sys_second_space, R.string.icon_tuner_other_second_space),
        "ime" to StatusBarIconSlotWrap("ime", R.drawable.ic_stat_sys_unknown, 0),
        "cast" to StatusBarIconSlotWrap("cast", R.drawable.ic_stat_sys_unknown, 0),
        "location" to StatusBarIconSlotWrap("location", R.drawable.ic_stat_sys_location, R.string.icon_tuner_connect_location),
        "stealth" to StatusBarIconSlotWrap("stealth", R.drawable.ic_stat_sys_stealth, 0),
        "tty" to StatusBarIconSlotWrap("tty", R.drawable.ic_stat_sys_tty, 0),
        "alarm_clock" to StatusBarIconSlotWrap("alarm_clock", R.drawable.ic_stat_sys_alarm_clock, R.string.icon_tuner_other_alarm),
        "vpn" to StatusBarIconSlotWrap("vpn", R.drawable.ic_stat_sys_vpn, R.string.icon_tuner_network_vpn),
        "ethernet" to StatusBarIconSlotWrap("ethernet", R.drawable.ic_stat_sys_unknown, 0),
        "handle_battery" to StatusBarIconSlotWrap("handle_battery", R.drawable.ic_stat_sys_handle_battery, R.string.icon_tuner_connect_handle_battery),
        "bluetooth" to StatusBarIconSlotWrap("bluetooth", R.drawable.ic_stat_sys_bluetooth, R.string.icon_tuner_connect_bluetooth),
        "bluetooth_handsfree_battery" to StatusBarIconSlotWrap("bluetooth_handsfree_battery", R.drawable.ic_stat_sys_bluetooth_handsfree_battery, R.string.icon_tuner_connect_bluetooth_battery),
        "hotspot" to StatusBarIconSlotWrap("hotspot", R.drawable.ic_stat_sys_hotspot, R.string.icon_tuner_network_hotspot),
        "sound_box_group" to StatusBarIconSlotWrap("sound_box_group", R.drawable.ic_stat_sys_sound_box_group, R.string.icon_tuner_device_sound_box_group),
        "stereo" to StatusBarIconSlotWrap("stereo", R.drawable.ic_stat_sys_stereo, R.string.icon_tuner_device_stereo),
        "sound_box_screen" to StatusBarIconSlotWrap("sound_box_screen", R.drawable.ic_stat_sys_sound_box_screen, R.string.icon_tuner_device_sound_box_screen),
        "sound_box" to StatusBarIconSlotWrap("sound_box", R.drawable.ic_stat_sys_sound_box, R.string.icon_tuner_device_sound_box),
        "wireless_headset" to StatusBarIconSlotWrap("wireless_headset", R.drawable.ic_stat_sys_wireless_headset, R.string.icon_tuner_device_wireless_headset),
        "zen" to StatusBarIconSlotWrap("zen", R.drawable.ic_stat_sys_zen, R.string.icon_tuner_other_zen),
        "volume" to StatusBarIconSlotWrap("volume", R.drawable.ic_stat_sys_volume, R.string.icon_tuner_other_volume),
        "dist_compute" to StatusBarIconSlotWrap("dist_compute", R.drawable.ic_stat_sys_dist_compute, R.string.icon_tuner_device_dist_compute),
        "camera" to StatusBarIconSlotWrap("camera", R.drawable.ic_stat_sys_camera, R.string.icon_tuner_device_camera),
        "glasses" to StatusBarIconSlotWrap("glasses", R.drawable.ic_stat_sys_glasses, R.string.icon_tuner_device_glasses),
        "car" to StatusBarIconSlotWrap("car", R.drawable.ic_stat_sys_car, R.string.icon_tuner_device_car),
        "tv" to StatusBarIconSlotWrap("tv", R.drawable.ic_stat_sys_tv, R.string.icon_tuner_device_tv),
        "pc" to StatusBarIconSlotWrap("pc", R.drawable.ic_stat_sys_pc, R.string.icon_tuner_device_pc),
        "pad" to StatusBarIconSlotWrap("pad", R.drawable.ic_stat_sys_pad, R.string.icon_tuner_device_pad),
        "phone" to StatusBarIconSlotWrap("phone", R.drawable.ic_stat_sys_phone, R.string.icon_tuner_device_phone),
        "hd" to StatusBarIconSlotWrap("hd", R.drawable.ic_stat_sys_unknown, 0),
        "airplane" to StatusBarIconSlotWrap("airplane", R.drawable.ic_stat_sys_airplane, R.string.icon_tuner_network_airplane),
        "mobile" to StatusBarIconSlotWrap("mobile", R.drawable.ic_stat_sys_mobile, R.string.icon_tuner_network_mobile),
        "demo_mobile" to StatusBarIconSlotWrap("demo_mobile", R.drawable.ic_stat_sys_mobile, R.string.icon_tuner_position_demo_mobile),
        "no_sim" to StatusBarIconSlotWrap("no_sim", R.drawable.ic_stat_sys_no_sim, R.string.icon_tuner_network_no_sim),
        "wifi" to StatusBarIconSlotWrap("wifi", R.drawable.ic_stat_sys_wifi, R.string.icon_tuner_network_wifi),
        "demo_wifi" to StatusBarIconSlotWrap("demo_wifi", R.drawable.ic_stat_sys_wifi, R.string.icon_tuner_position_demo_wifi),
    )

    enum class MediaControlSpKey {
        UNLOCK_ACTION,

        BACKGROUND_STYLE,
        BLUR_RADIUS,
        ALLOW_REVERSE,
        AMBIENT_LIGHT,

        LYT_ALBUM,
        LYT_LEFT_ACTIONS,
        LYT_ACTIONS_ORDER,
        LYT_HIDE_TIME,
        LYT_HIDE_SEAMLESS,
        LYT_HEADER_MARGIN,
        LYT_HEADER_PADDING,

        ELM_TEXT_SIZE,
        ELM_TITLE_SIZE,
        ELM_ARTIST_SIZE,
        ELM_TIME_SIZE,
        ELM_ACTIONS_RESIZE,
        ELM_THUMB_STYLE,
        ELM_PROGRESS_STYLE,
        ELM_PROGRESS_WIDTH,

        FIX_THUMB_CROPPED,
        USE_ANIM,
    }

    private val normalMediaControlSpKeyMap by lazy {
        mapOf(
            MediaControlSpKey.UNLOCK_ACTION to Pref.Key.SystemUI.MediaControl.UNLOCK_ACTION,

            MediaControlSpKey.BACKGROUND_STYLE to Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE,
            MediaControlSpKey.BLUR_RADIUS to Pref.Key.SystemUI.MediaControl.BLUR_RADIUS,
            MediaControlSpKey.ALLOW_REVERSE to Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE,
            MediaControlSpKey.AMBIENT_LIGHT to Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT,

            MediaControlSpKey.LYT_ALBUM to Pref.Key.SystemUI.MediaControl.LYT_ALBUM,
            MediaControlSpKey.LYT_LEFT_ACTIONS to Pref.Key.SystemUI.MediaControl.LYT_LEFT_ACTIONS,
            MediaControlSpKey.LYT_ACTIONS_ORDER to Pref.Key.SystemUI.MediaControl.LYT_ACTIONS_ORDER,
            MediaControlSpKey.LYT_HIDE_TIME to Pref.Key.SystemUI.MediaControl.LYT_HIDE_TIME,
            MediaControlSpKey.LYT_HIDE_SEAMLESS to Pref.Key.SystemUI.MediaControl.LYT_HIDE_SEAMLESS,
            MediaControlSpKey.LYT_HEADER_MARGIN to Pref.Key.SystemUI.MediaControl.LYT_HEADER_MARGIN,
            MediaControlSpKey.LYT_HEADER_PADDING to Pref.Key.SystemUI.MediaControl.LYT_HEADER_PADDING,

            MediaControlSpKey.ELM_TEXT_SIZE to Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE,
            MediaControlSpKey.ELM_TITLE_SIZE to Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE,
            MediaControlSpKey.ELM_ARTIST_SIZE to Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE,
            MediaControlSpKey.ELM_TIME_SIZE to Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE,
//            MediaControlSpKey.ELM_ACTIONS_RESIZE to Pref.Key.SystemUI.MediaControl.ELM_ACTIONS_RESIZE,
            MediaControlSpKey.ELM_THUMB_STYLE to Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE,
            MediaControlSpKey.ELM_PROGRESS_STYLE to Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE,
            MediaControlSpKey.ELM_PROGRESS_WIDTH to Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH,

            MediaControlSpKey.FIX_THUMB_CROPPED to Pref.Key.SystemUI.MediaControl.FIX_THUMB_CROPPED,
            MediaControlSpKey.USE_ANIM to Pref.Key.SystemUI.MediaControl.USE_ANIM,
        )
    }

    private val islandMediaControlSpKeyMap by lazy {
        mapOf(
            MediaControlSpKey.UNLOCK_ACTION to Pref.Key.DynamicIsland.MediaControl.UNLOCK_ACTION,

            MediaControlSpKey.BACKGROUND_STYLE to Pref.Key.DynamicIsland.MediaControl.BACKGROUND_STYLE,
            MediaControlSpKey.BLUR_RADIUS to Pref.Key.DynamicIsland.MediaControl.BLUR_RADIUS,
            MediaControlSpKey.ALLOW_REVERSE to Pref.Key.DynamicIsland.MediaControl.ALLOW_REVERSE,
            MediaControlSpKey.AMBIENT_LIGHT to Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT,

            MediaControlSpKey.LYT_ALBUM to Pref.Key.DynamicIsland.MediaControl.LYT_ALBUM,
            MediaControlSpKey.LYT_LEFT_ACTIONS to Pref.Key.DynamicIsland.MediaControl.LYT_LEFT_ACTIONS,
            MediaControlSpKey.LYT_ACTIONS_ORDER to Pref.Key.DynamicIsland.MediaControl.LYT_ACTIONS_ORDER,
            MediaControlSpKey.LYT_HIDE_TIME to Pref.Key.DynamicIsland.MediaControl.LYT_HIDE_TIME,
            MediaControlSpKey.LYT_HIDE_SEAMLESS to Pref.Key.DynamicIsland.MediaControl.LYT_HIDE_SEAMLESS,
            MediaControlSpKey.LYT_HEADER_MARGIN to Pref.Key.DynamicIsland.MediaControl.LYT_HEADER_MARGIN,
            MediaControlSpKey.LYT_HEADER_PADDING to Pref.Key.DynamicIsland.MediaControl.LYT_HEADER_PADDING,

            MediaControlSpKey.ELM_TEXT_SIZE to Pref.Key.DynamicIsland.MediaControl.ELM_TEXT_SIZE,
            MediaControlSpKey.ELM_TITLE_SIZE to Pref.Key.DynamicIsland.MediaControl.ELM_TITLE_SIZE,
            MediaControlSpKey.ELM_ARTIST_SIZE to Pref.Key.DynamicIsland.MediaControl.ELM_ARTIST_SIZE,
            MediaControlSpKey.ELM_TIME_SIZE to Pref.Key.DynamicIsland.MediaControl.ELM_TIME_SIZE,
//            MediaControlSpKey.ELM_ACTIONS_RESIZE to Pref.Key.DynamicIsland.MediaControl.ELM_ACTIONS_RESIZE,
            MediaControlSpKey.ELM_THUMB_STYLE to Pref.Key.DynamicIsland.MediaControl.ELM_THUMB_STYLE,
            MediaControlSpKey.ELM_PROGRESS_STYLE to Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_STYLE,
            MediaControlSpKey.ELM_PROGRESS_WIDTH to Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_WIDTH,

            MediaControlSpKey.FIX_THUMB_CROPPED to Pref.Key.DynamicIsland.MediaControl.FIX_THUMB_CROPPED,
            MediaControlSpKey.USE_ANIM to Pref.Key.DynamicIsland.MediaControl.USE_ANIM,
        )
    }

    fun MediaControlSpKey.getKey(isDynamicIsland: Boolean): String {
        return if (isDynamicIsland) {
            islandMediaControlSpKeyMap.getOrDefault(this, "")
        } else {
            normalMediaControlSpKeyMap.getOrDefault(this, "")
        }
    }
}
