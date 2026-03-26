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

    const val VARIABLE_FONT_REAL_FILE_PATH = "/data/system/fonts"
    const val VARIABLE_FONT_REAL_FILE_NAME = "hyper_helper_vf_real.ttf"
    const val VARIABLE_FONT_MOBILE_TYPE_REAL_FILE_NAME = "hyper_helper_vf_mobile_type_real.ttf"
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

    object IconSlots {
        const val LOCATION = "location"
        const val ALARM_CLOCK = "alarm_clock"
        const val ZEN = "zen"
        const val VOLUME = "volume"

        const val COMPOUND_ICON_STUB = "compound_stub"
        const val COMPOUND_ICON_REAL_LOCATION = "compound_location"
        const val COMPOUND_ICON_REAL_ALARM_CLOCK = "compound_alarm_clock"
        const val COMPOUND_ICON_REAL_ZEN = "compound_zen"
        const val COMPOUND_ICON_REAL_VIBRATE = "compound_volume_vibrate"
        const val COMPOUND_ICON_REAL_MUTE = "compound_volume_mute"

        const val STACKED_MOBILE_ICON = "stacked_mobile_icon"
        const val STACKED_MOBILE_TYPE = "stacked_mobile_type"
        const val SINGLE_MOBILE_SIM1 = "single_mobile_sim1"
        const val SINGLE_MOBILE_SIM2 = "single_mobile_sim2"
    }

    val STATUS_BAR_ICONS_DEFAULT = listOf(
        "handle", "network_speed", "mute", "micphone", "headset", "mikey", "privacy_mode", "nfc", "gps",
        "missed_call", "managed_profile", "second_space", "ime", "cast", IconSlots.LOCATION, "stealth", "tty",
        IconSlots.ALARM_CLOCK, "vpn", "ethernet", "handle_battery", "bluetooth", "bluetooth_handsfree_battery",
        "hotspot", "sound_box_group", "stereo", "sound_box_screen", "sound_box", "wireless_headset",
        "zen", "volume", "dist_compute", "camera", "glasses", "car", "tv", "pc", "pad", "phone",
        "hd", "airplane", "mobile", "demo_mobile", IconSlots.SINGLE_MOBILE_SIM1, IconSlots.SINGLE_MOBILE_SIM2,
        IconSlots.STACKED_MOBILE_ICON, IconSlots.STACKED_MOBILE_TYPE, "no_sim", "wifi", "demo_wifi",
    )

    val STATUS_BAR_ICONS_SWAP = listOf(
        "handle", "network_speed", "mute", "micphone", "headset", "mikey", "privacy_mode", "nfc", "gps",
        "missed_call", "managed_profile", "second_space", "ime", "cast", IconSlots.LOCATION, "stealth", "tty",
        IconSlots.ALARM_CLOCK, "vpn", "ethernet", "handle_battery", "bluetooth", "bluetooth_handsfree_battery",
        "hotspot", "sound_box_group", "stereo", "sound_box_screen", "sound_box", "wireless_headset",
        "zen", "volume", "dist_compute", "camera", "glasses", "car", "tv", "pc", "pad", "phone",
        "wifi", "demo_wifi", "hd", "airplane", "mobile", "demo_mobile", IconSlots.SINGLE_MOBILE_SIM1, IconSlots.SINGLE_MOBILE_SIM2,
        IconSlots.STACKED_MOBILE_ICON, IconSlots.STACKED_MOBILE_TYPE, "no_sim",
    )


    val STATUS_BAR_ICON_SLOT_MAP = mutableMapOf(
        "handle" to StatusBarIconSlotWrap("handle", R.drawable.ic_stat_sys_unknown, 0),
        "network_speed" to StatusBarIconSlotWrap("network_speed", R.drawable.ic_stat_sys_net_speed, R.string.icon_tuner_network_net_speed),
        "mute" to StatusBarIconSlotWrap("mute", R.drawable.ic_stat_sys_unknown, 0),
        "micphone" to StatusBarIconSlotWrap("micphone", R.drawable.ic_stat_sys_micphone, 0),
        "headset" to StatusBarIconSlotWrap("headset", R.drawable.ic_stat_sys_headset, R.string.icon_tuner_connect_headset),
        "mikey" to StatusBarIconSlotWrap("mikey", R.drawable.ic_stat_sys_unknown, 0),
        "privacy_mode" to StatusBarIconSlotWrap("privacy_mode", R.drawable.ic_stat_sys_unknown, 0),
        "nfc" to StatusBarIconSlotWrap("nfc", R.drawable.ic_stat_sys_nfc, R.string.icon_tuner_connect_nfc),
        "gps" to StatusBarIconSlotWrap("gps", R.drawable.ic_stat_sys_unknown, 0),
        "missed_call" to StatusBarIconSlotWrap("missed_call", R.drawable.ic_stat_sys_unknown, 0),
        "managed_profile" to StatusBarIconSlotWrap("managed_profile", R.drawable.ic_stat_sys_managed_profile, 0),
        "second_space" to StatusBarIconSlotWrap("second_space", R.drawable.ic_stat_sys_second_space, R.string.icon_tuner_other_second_space),
        "ime" to StatusBarIconSlotWrap("ime", R.drawable.ic_stat_sys_unknown, 0),
        "cast" to StatusBarIconSlotWrap("cast", R.drawable.ic_stat_sys_unknown, 0),
        IconSlots.LOCATION to StatusBarIconSlotWrap(IconSlots.LOCATION, R.drawable.ic_stat_sys_location, R.string.icon_tuner_connect_location),
        "stealth" to StatusBarIconSlotWrap("stealth", R.drawable.ic_stat_sys_stealth, 0),
        "tty" to StatusBarIconSlotWrap("tty", R.drawable.ic_stat_sys_tty, 0),
        IconSlots.ALARM_CLOCK to StatusBarIconSlotWrap(IconSlots.ALARM_CLOCK, R.drawable.ic_stat_sys_alarm_clock, R.string.icon_tuner_other_alarm),
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
        IconSlots.ZEN to StatusBarIconSlotWrap(IconSlots.ZEN, R.drawable.ic_stat_sys_zen, R.string.icon_tuner_other_zen),
        IconSlots.VOLUME to StatusBarIconSlotWrap(IconSlots.VOLUME, R.drawable.ic_stat_sys_volume, R.string.icon_tuner_other_volume),
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
        IconSlots.COMPOUND_ICON_STUB to StatusBarIconSlotWrap(IconSlots.COMPOUND_ICON_STUB, R.drawable.ic_stat_sys_compound, R.string.icon_tuner_compound_icon),
        IconSlots.STACKED_MOBILE_ICON to StatusBarIconSlotWrap(IconSlots.STACKED_MOBILE_ICON, R.drawable.ic_stat_sys_stacked_icon, R.string.icon_tuner_stacked_mobile_icon),
        IconSlots.STACKED_MOBILE_TYPE to StatusBarIconSlotWrap(IconSlots.STACKED_MOBILE_TYPE, R.drawable.ic_stat_sys_stacked_type, R.string.ui_title_icon_detail_stacked_type),
        IconSlots.SINGLE_MOBILE_SIM1 to StatusBarIconSlotWrap(IconSlots.SINGLE_MOBILE_SIM1, R.drawable.ic_stat_sys_single_sim1, R.string.icon_tuner_single_mobile_sim1),
        IconSlots.SINGLE_MOBILE_SIM2 to StatusBarIconSlotWrap(IconSlots.SINGLE_MOBILE_SIM2, R.drawable.ic_stat_sys_single_sim2, R.string.icon_tuner_single_mobile_sim2),
    )

    enum class MediaControlSpKey {
        BACKGROUND_STYLE,
        BLUR_RADIUS,
        ALLOW_REVERSE,
        AMBIENT_LIGHT_OPT,

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
//        ELM_ACTIONS_RESIZE,
        ELM_THUMB_STYLE,
        ELM_PROGRESS_STYLE,
        ELM_PROGRESS_WIDTH,
        ELM_PROGRESS_ROUND,
        ELM_PROGRESS_COMET,

        FIX_THUMB_CROPPED,
        USE_ANIM,
    }

    private val normalMediaControlSpKeyMap by lazy {
        mapOf(
            MediaControlSpKey.BACKGROUND_STYLE to Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE,
            MediaControlSpKey.BLUR_RADIUS to Pref.Key.SystemUI.MediaControl.BLUR_RADIUS,
            MediaControlSpKey.ALLOW_REVERSE to Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE,
            MediaControlSpKey.AMBIENT_LIGHT_OPT to Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT_OPT,

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
            MediaControlSpKey.ELM_PROGRESS_ROUND to Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_ROUND,
            MediaControlSpKey.ELM_PROGRESS_COMET to Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_COMET,

            MediaControlSpKey.FIX_THUMB_CROPPED to Pref.Key.SystemUI.MediaControl.FIX_THUMB_CROPPED,
            MediaControlSpKey.USE_ANIM to Pref.Key.SystemUI.MediaControl.USE_ANIM,
        )
    }

    private val islandMediaControlSpKeyMap by lazy {
        mapOf(
            MediaControlSpKey.BACKGROUND_STYLE to Pref.Key.DynamicIsland.MediaControl.BACKGROUND_STYLE,
            MediaControlSpKey.BLUR_RADIUS to Pref.Key.DynamicIsland.MediaControl.BLUR_RADIUS,
            MediaControlSpKey.ALLOW_REVERSE to Pref.Key.DynamicIsland.MediaControl.ALLOW_REVERSE,
            MediaControlSpKey.AMBIENT_LIGHT_OPT to Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT_OPT,

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
            MediaControlSpKey.ELM_PROGRESS_ROUND to Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_ROUND,
            MediaControlSpKey.ELM_PROGRESS_COMET to Pref.Key.DynamicIsland.MediaControl.ELM_PROGRESS_COMET,

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


    val COMPOUND_ICON_REAL_SLOTS = listOf(
        IconSlots.COMPOUND_ICON_REAL_LOCATION,
        IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK,
        IconSlots.COMPOUND_ICON_REAL_ZEN,
        IconSlots.COMPOUND_ICON_REAL_VIBRATE,
        IconSlots.COMPOUND_ICON_REAL_MUTE,
    )
    const val COMPOUND_ICON_PRIORITY_STR = "${IconSlots.LOCATION},${IconSlots.ALARM_CLOCK},${IconSlots.ZEN},${IconSlots.VOLUME}"

    const val STACKED_MOBILE_ICON_SINGLE_MIUI = """
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="stat_sys_signal_single_miui"><path id="signal_4" d="M14.918 5.02774C14.918 4.65864 14.918 4.47409 14.9898 4.33311C15.053 4.20911 15.1538 4.10828 15.2778 4.0451C15.4188 3.97327 15.6033 3.97327 15.9724 3.97327H16.3435C16.7126 3.97327 16.8971 3.97327 17.0381 4.0451C17.1621 4.10828 17.263 4.20911 17.3261 4.33311C17.398 4.47409 17.398 4.65864 17.398 5.02774V14.9721C17.398 15.3412 17.398 15.5258 17.3261 15.6668C17.263 15.7908 17.1621 15.8916 17.0381 15.9548C16.8971 16.0266 16.7126 16.0266 16.3435 16.0266H15.9724C15.6033 16.0266 15.4188 16.0266 15.2778 15.9548C15.1538 15.8916 15.053 15.7908 14.9898 15.6668C14.918 15.5258 14.918 15.3412 14.918 14.9721V5.02774Z" fill="white"/><path id="signal_3" d="M10.6665 7.7211C10.6665 7.352 10.6665 7.16745 10.7383 7.02647C10.8015 6.90246 10.9023 6.80164 11.0264 6.73846C11.1673 6.66663 11.3519 6.66663 11.721 6.66663H12.092C12.4611 6.66663 12.6457 6.66663 12.7867 6.73846C12.9107 6.80164 13.0115 6.90246 13.0747 7.02647C13.1465 7.16745 13.1465 7.352 13.1465 7.7211V14.9721C13.1465 15.3413 13.1465 15.5258 13.0747 15.6668C13.0115 15.7908 12.9107 15.8916 12.7867 15.9548C12.6457 16.0266 12.4611 16.0266 12.092 16.0266H11.721C11.3519 16.0266 11.1673 16.0266 11.0264 15.9548C10.9023 15.8916 10.8015 15.7908 10.7383 15.6668C10.6665 15.5258 10.6665 15.3413 10.6665 14.9721V7.7211Z" fill="white"/><path id="signal_2" d="M6.41162 10.8183C6.41162 10.4492 6.41162 10.2646 6.48345 10.1236C6.54664 9.99963 6.64746 9.89881 6.77147 9.83563C6.91245 9.76379 7.097 9.76379 7.4661 9.76379H7.83714C8.20625 9.76379 8.3908 9.76379 8.53177 9.83563C8.65578 9.89881 8.7566 9.99963 8.81979 10.1236C8.89162 10.2646 8.89162 10.4492 8.89162 10.8183V14.9722C8.89162 15.3413 8.89162 15.5258 8.81979 15.6668C8.7566 15.7908 8.65578 15.8916 8.53177 15.9548C8.3908 16.0267 8.20625 16.0267 7.83715 16.0267H7.4661C7.097 16.0267 6.91245 16.0267 6.77147 15.9548C6.64746 15.8916 6.54664 15.7908 6.48345 15.6668C6.41162 15.5258 6.41162 15.3413 6.41162 14.9722V10.8183Z" fill="white"/><path id="signal_1" d="M2.16016 13.085C2.16016 12.7159 2.16016 12.5313 2.23199 12.3904C2.29517 12.2664 2.39599 12.1655 2.52 12.1023C2.66098 12.0305 2.84553 12.0305 3.21463 12.0305H3.58568C3.95478 12.0305 4.13933 12.0305 4.28031 12.1023C4.40432 12.1655 4.50514 12.2664 4.56832 12.3904C4.64016 12.5313 4.64016 12.7159 4.64016 13.085V14.9684C4.64016 15.3375 4.64016 15.5221 4.56832 15.6631C4.50514 15.7871 4.40432 15.8879 4.28031 15.9511C4.13933 16.0229 3.95478 16.0229 3.58568 16.0229H3.21463C2.84553 16.0229 2.66098 16.0229 2.52 15.9511C2.39599 15.8879 2.29517 15.7871 2.23199 15.6631C2.16016 15.5221 2.16016 15.3375 2.16016 14.9684V13.085Z" fill="white"/><rect id="type_container" x="0.799805" y="3.80005" width="2" height="2" fill="white"/></g></svg>
    """
    const val STACKED_MOBILE_ICON_STACKED_MIUI = """
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="stat_sys_signal_stacked_miui"><path id="signal_2_4" d="M14.918 14.3764C14.918 14.0073 14.918 13.8227 14.9898 13.6817C15.053 13.5577 15.1538 13.4569 15.2778 13.3937C15.4188 13.3219 15.6033 13.3219 15.9724 13.3219H16.3435C16.7126 13.3219 16.8971 13.3219 17.0381 13.3937C17.1621 13.4569 17.263 13.5577 17.3261 13.6817C17.398 13.8227 17.398 14.0073 17.398 14.3764V14.9722C17.398 15.3413 17.398 15.5258 17.3261 15.6668C17.263 15.7908 17.1621 15.8916 17.0381 15.9548C16.8971 16.0267 16.7126 16.0267 16.3435 16.0267H15.9724C15.6033 16.0267 15.4188 16.0267 15.2778 15.9548C15.1538 15.8916 15.053 15.7908 14.9898 15.6668C14.918 15.5258 14.918 15.3413 14.918 14.9722V14.3764Z" fill="white"/><path id="signal_2_3" d="M10.6665 14.3764C10.6665 14.0073 10.6665 13.8227 10.7383 13.6817C10.8015 13.5577 10.9023 13.4569 11.0264 13.3937C11.1673 13.3219 11.3519 13.3219 11.721 13.3219H12.092C12.4611 13.3219 12.6457 13.3219 12.7867 13.3937C12.9107 13.4569 13.0115 13.5577 13.0747 13.6817C13.1465 13.8227 13.1465 14.0073 13.1465 14.3764V14.9722C13.1465 15.3413 13.1465 15.5258 13.0747 15.6668C13.0115 15.7908 12.9107 15.8916 12.7867 15.9548C12.6457 16.0267 12.4611 16.0267 12.092 16.0267H11.721C11.3519 16.0267 11.1673 16.0267 11.0264 15.9548C10.9023 15.8916 10.8015 15.7908 10.7383 15.6668C10.6665 15.5258 10.6665 15.3413 10.6665 14.9722V14.3764Z" fill="white"/><path id="signal_2_2" d="M6.41162 14.3764C6.41162 14.0073 6.41162 13.8227 6.48345 13.6817C6.54664 13.5577 6.64746 13.4569 6.77147 13.3937C6.91245 13.3219 7.097 13.3219 7.4661 13.3219H7.83714C8.20625 13.3219 8.3908 13.3219 8.53177 13.3937C8.65578 13.4569 8.7566 13.5577 8.81979 13.6817C8.89162 13.8227 8.89162 14.0073 8.89162 14.3764V14.9722C8.89162 15.3413 8.89162 15.5258 8.81979 15.6668C8.7566 15.7908 8.65578 15.8916 8.53177 15.9548C8.3908 16.0267 8.20625 16.0267 7.83715 16.0267H7.4661C7.097 16.0267 6.91245 16.0267 6.77147 15.9548C6.64746 15.8916 6.54664 15.7908 6.48345 15.6668C6.41162 15.5258 6.41162 15.3413 6.41162 14.9722V14.3764Z" fill="white"/><path id="signal_2_1" d="M2.16016 14.3764C2.16016 14.0073 2.16016 13.8227 2.23199 13.6817C2.29517 13.5577 2.39599 13.4569 2.52 13.3937C2.66098 13.3219 2.84553 13.3219 3.21463 13.3219H3.58568C3.95478 13.3219 4.13933 13.3219 4.28031 13.3937C4.40432 13.4569 4.50514 13.5577 4.56832 13.6817C4.64016 13.8227 4.64016 14.0073 4.64016 14.3764V14.9722C4.64016 15.3413 4.64016 15.5258 4.56832 15.6668C4.50514 15.7908 4.40432 15.8916 4.28031 15.9548C4.13933 16.0267 3.95478 16.0267 3.58568 16.0267H3.21463C2.84553 16.0267 2.66098 16.0267 2.52 15.9548C2.39599 15.8916 2.29517 15.7908 2.23199 15.6668C2.16016 15.5258 2.16016 15.3413 2.16016 14.9722V14.3764Z" fill="white"/><path id="signal_1_4" d="M14.918 5.02774C14.918 4.65864 14.918 4.47409 14.9898 4.33311C15.053 4.20911 15.1538 4.10828 15.2778 4.0451C15.4188 3.97327 15.6033 3.97327 15.9724 3.97327H16.3435C16.7126 3.97327 16.8971 3.97327 17.0381 4.0451C17.1621 4.10828 17.263 4.20911 17.3261 4.33311C17.398 4.47409 17.398 4.65864 17.398 5.02774V10.915C17.398 11.2841 17.398 11.4686 17.3261 11.6096C17.263 11.7336 17.1621 11.8344 17.0381 11.8976C16.8971 11.9694 16.7126 11.9694 16.3435 11.9694H15.9724C15.6033 11.9694 15.4188 11.9694 15.2778 11.8976C15.1538 11.8344 15.053 11.7336 14.9898 11.6096C14.918 11.4686 14.918 11.2841 14.918 10.915V5.02774Z" fill="white"/><path id="signal_1_3" d="M10.6665 6.81461C10.6665 6.44551 10.6665 6.26096 10.7383 6.11998C10.8015 5.99597 10.9023 5.89515 11.0264 5.83196C11.1673 5.76013 11.3519 5.76013 11.721 5.76013H12.092C12.4611 5.76013 12.6457 5.76013 12.7867 5.83196C12.9107 5.89515 13.0115 5.99597 13.0747 6.11998C13.1465 6.26096 13.1465 6.44551 13.1465 6.81461V10.9151C13.1465 11.2842 13.1465 11.4687 13.0747 11.6097C13.0115 11.7337 12.9107 11.8345 12.7867 11.8977C12.6457 11.9695 12.4611 11.9695 12.092 11.9695H11.721C11.3519 11.9695 11.1673 11.9695 11.0264 11.8977C10.9023 11.8345 10.8015 11.7337 10.7383 11.6097C10.6665 11.4687 10.6665 11.2842 10.6665 10.9151V6.81461Z" fill="white"/><path id="signal_1_2" d="M6.41162 8.86917C6.41162 8.50007 6.41162 8.31552 6.48345 8.17454C6.54664 8.05054 6.64746 7.94971 6.77147 7.88653C6.91245 7.8147 7.097 7.8147 7.4661 7.8147H7.83714C8.20625 7.8147 8.3908 7.8147 8.53177 7.88653C8.65578 7.94971 8.7566 8.05054 8.81979 8.17454C8.89162 8.31552 8.89162 8.50007 8.89162 8.86917V10.915C8.89162 11.2841 8.89162 11.4686 8.81979 11.6096C8.7566 11.7336 8.65578 11.8345 8.53177 11.8976C8.3908 11.9695 8.20625 11.9695 7.83715 11.9695H7.4661C7.097 11.9695 6.91245 11.9695 6.77147 11.8976C6.64746 11.8345 6.54664 11.7336 6.48345 11.6096C6.41162 11.4686 6.41162 11.2841 6.41162 10.915V8.86917Z" fill="white"/><path id="signal_1_1" d="M2.16016 10.345C2.16016 9.96871 2.16016 9.78057 2.23199 9.63686C2.29517 9.51044 2.39599 9.40766 2.52 9.34325C2.66098 9.27002 2.84553 9.27002 3.21463 9.27002H3.58568C3.95478 9.27002 4.13933 9.27002 4.28031 9.34325C4.40432 9.40766 4.50514 9.51044 4.56832 9.63686C4.64016 9.78057 4.64016 9.96871 4.64016 10.345V10.8951C4.64016 11.2713 4.64016 11.4595 4.56832 11.6032C4.50514 11.7296 4.40432 11.8324 4.28031 11.8968C4.13933 11.97 3.95478 11.97 3.58568 11.97H3.21463C2.84553 11.97 2.66098 11.97 2.52 11.8968C2.39599 11.8324 2.29517 11.7296 2.23199 11.6032C2.16016 11.4595 2.16016 11.2713 2.16016 10.8951V10.345Z" fill="white"/><rect id="type_container" x="0.799805" y="3.80005" width="2" height="2" fill="white"/></g></svg>
    """
    const val STACKED_MOBILE_ICON_SINGLE_IOS = """
        <svg width="24" height="20" viewBox="0 0 24 20" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="stat_sys_signal_single_ios"><path id="signal_4" fill-rule="evenodd" clip-rule="evenodd" d="M21.5999 5.03307C21.5999 4.40002 21.1223 3.88684 20.5332 3.88684H19.4666C18.8775 3.88684 18.3999 4.40002 18.3999 5.03307V14.967C18.3999 15.6001 18.8775 16.1133 19.4666 16.1133H20.5332C21.1223 16.1133 21.5999 15.6001 21.5999 14.967V5.03307Z" fill="white"/><path id="signal_3" fill-rule="evenodd" clip-rule="evenodd" d="M14.1658 6.33215H15.2325C15.8216 6.33215 16.2991 6.85765 16.2991 7.50589V14.9395C16.2991 15.5878 15.8216 16.1133 15.2325 16.1133H14.1658C13.5767 16.1133 13.0991 15.5878 13.0991 14.9395V7.50589C13.0991 6.85765 13.5767 6.33215 14.1658 6.33215Z" fill="white"/><path id="signal_2" fill-rule="evenodd" clip-rule="evenodd" d="M9.83402 8.9812H8.76735C8.17825 8.9812 7.70068 9.51339 7.70068 10.1699V14.9246C7.70068 15.5811 8.17825 16.1133 8.76735 16.1133H9.83402C10.4231 16.1133 10.9007 15.5811 10.9007 14.9246V10.1699C10.9007 9.51339 10.4231 8.9812 9.83402 8.9812Z" fill="white"/><path id="signal_1" fill-rule="evenodd" clip-rule="evenodd" d="M4.53324 11.4265H3.46657C2.87747 11.4265 2.3999 11.9511 2.3999 12.5982V14.9416C2.3999 15.5887 2.87747 16.1133 3.46657 16.1133H4.53324C5.12234 16.1133 5.5999 15.5887 5.5999 14.9416V12.5982C5.5999 11.9511 5.12234 11.4265 4.53324 11.4265Z" fill="white"/></g></svg>
    """
    const val STACKED_MOBILE_ICON_STACKED_IOS = """
        <svg width="24" height="20" viewBox="0 0 24 20" fill="none" xmlns="http://www.w3.org/2000/svg"><g id="stat_sys_signal_stacked_ios"><path id="signal_2_4" d="M18.4131 14.7727C18.4131 14.2878 18.4131 14.0453 18.4869 13.8522C18.6006 13.5547 18.8357 13.3196 19.1332 13.2059C19.3263 13.1321 19.5688 13.1321 20.0537 13.1321C20.5386 13.1321 20.781 13.1321 20.9742 13.2059C21.2717 13.3196 21.5068 13.5547 21.6205 13.8522C21.6943 14.0453 21.6943 14.2878 21.6943 14.7727V15.071C21.6943 15.5559 21.6943 15.7983 21.6205 15.9915C21.5068 16.289 21.2717 16.5241 20.9742 16.6378C20.781 16.7116 20.5386 16.7116 20.0537 16.7116C19.5688 16.7116 19.3263 16.7116 19.1332 16.6378C18.8357 16.5241 18.6006 16.289 18.4869 15.9915C18.4131 15.7983 18.4131 15.5559 18.4131 15.071V14.7727Z" fill="white"/><path id="signal_2_3" d="M13.0439 14.7727C13.0439 14.2878 13.0439 14.0453 13.1178 13.8522C13.2315 13.5547 13.4665 13.3196 13.7641 13.2059C13.9572 13.1321 14.1997 13.1321 14.6846 13.1321C15.1695 13.1321 15.4119 13.1321 15.605 13.2059C15.9026 13.3196 16.1376 13.5547 16.2514 13.8522C16.3252 14.0453 16.3252 14.2878 16.3252 14.7727V15.071C16.3252 15.5559 16.3252 15.7983 16.2514 15.9915C16.1376 16.289 15.9026 16.5241 15.605 16.6378C15.4119 16.7116 15.1695 16.7116 14.6846 16.7116C14.1997 16.7116 13.9572 16.7116 13.7641 16.6378C13.4665 16.5241 13.2315 16.289 13.1178 15.9915C13.0439 15.7983 13.0439 15.5559 13.0439 15.071V14.7727Z" fill="white"/><path id="signal_2_2" d="M7.6748 14.7727C7.6748 14.2878 7.6748 14.0453 7.74862 13.8522C7.86234 13.5547 8.09739 13.3196 8.39494 13.2059C8.58806 13.1321 8.83051 13.1321 9.31541 13.1321C9.80032 13.1321 10.0428 13.1321 10.2359 13.2059C10.5334 13.3196 10.7685 13.5547 10.8822 13.8522C10.956 14.0453 10.956 14.2878 10.956 14.7727V15.071C10.956 15.5559 10.956 15.7983 10.8822 15.9915C10.7685 16.289 10.5334 16.5241 10.2359 16.6378C10.0428 16.7116 9.80032 16.7116 9.31541 16.7116C8.83051 16.7116 8.58806 16.7116 8.39494 16.6378C8.09739 16.5241 7.86234 16.289 7.74862 15.9915C7.6748 15.7983 7.6748 15.5559 7.6748 15.071V14.7727Z" fill="white"/><path id="signal_2_1" d="M2.30566 14.7727C2.30566 14.2878 2.30566 14.0453 2.37948 13.8522C2.4932 13.5547 2.72825 13.3196 3.0258 13.2059C3.21892 13.1321 3.46137 13.1321 3.94627 13.1321C4.43118 13.1321 4.67363 13.1321 4.86675 13.2059C5.1643 13.3196 5.39934 13.5547 5.51307 13.8522C5.58688 14.0453 5.58688 14.2878 5.58688 14.7727V15.071C5.58688 15.5559 5.58688 15.7983 5.51307 15.9915C5.39934 16.289 5.1643 16.5241 4.86675 16.6378C4.67363 16.7116 4.43118 16.7116 3.94627 16.7116C3.46137 16.7116 3.21892 16.7116 3.0258 16.6378C2.72825 16.5241 2.4932 16.289 2.37948 15.9915C2.30566 15.7983 2.30566 15.5559 2.30566 15.071V14.7727Z" fill="white"/><path id="signal_1_4" d="M18.4131 4.92906C18.4131 4.44416 18.4131 4.20171 18.4869 4.00859C18.6006 3.71104 18.8357 3.47599 19.1332 3.36226C19.3263 3.28845 19.5688 3.28845 20.0537 3.28845C20.5386 3.28845 20.781 3.28845 20.9742 3.36226C21.2717 3.47599 21.5068 3.71104 21.6205 4.00859C21.6943 4.20171 21.6943 4.44416 21.6943 4.92906V9.70175C21.6943 10.1866 21.6943 10.4291 21.6205 10.6222C21.5068 10.9198 21.2717 11.1548 20.9742 11.2685C20.781 11.3424 20.5386 11.3424 20.0537 11.3424C19.5688 11.3424 19.3263 11.3424 19.1332 11.2685C18.8357 11.1548 18.6006 10.9198 18.4869 10.6222C18.4131 10.4291 18.4131 10.1866 18.4131 9.70175V4.92906Z" fill="white"/><path id="signal_1_3" d="M13.0439 6.42052C13.0439 5.93561 13.0439 5.69316 13.1178 5.50004C13.2315 5.20249 13.4665 4.96745 13.7641 4.85372C13.9572 4.77991 14.1997 4.77991 14.6846 4.77991C15.1695 4.77991 15.4119 4.77991 15.605 4.85372C15.9026 4.96745 16.1376 5.20249 16.2514 5.50004C16.3252 5.69316 16.3252 5.93561 16.3252 6.42052V9.70174C16.3252 10.1866 16.3252 10.4291 16.2514 10.6222C16.1376 10.9198 15.9026 11.1548 15.605 11.2685C15.4119 11.3423 15.1695 11.3423 14.6846 11.3423C14.1997 11.3423 13.9572 11.3423 13.7641 11.2685C13.4665 11.1548 13.2315 10.9198 13.1178 10.6222C13.0439 10.4291 13.0439 10.1866 13.0439 9.70174V6.42052Z" fill="white"/><path id="signal_1_2" d="M7.6748 7.91197C7.6748 7.42707 7.6748 7.18462 7.74862 6.9915C7.86234 6.69395 8.09739 6.4589 8.39494 6.34517C8.58806 6.27136 8.83051 6.27136 9.31541 6.27136C9.80032 6.27136 10.0428 6.27136 10.2359 6.34517C10.5334 6.4589 10.7685 6.69395 10.8822 6.9915C10.956 7.18462 10.956 7.42707 10.956 7.91197V9.70173C10.956 10.1866 10.956 10.4291 10.8822 10.6222C10.7685 10.9198 10.5334 11.1548 10.2359 11.2685C10.0428 11.3423 9.80032 11.3423 9.31541 11.3423C8.83051 11.3423 8.58806 11.3423 8.39494 11.2685C8.09739 11.1548 7.86234 10.9198 7.74862 10.6222C7.6748 10.4291 7.6748 10.1866 7.6748 9.70173V7.91197Z" fill="white"/><path id="signal_1_1" d="M2.30566 9.10509C2.30566 8.62018 2.30566 8.37773 2.37948 8.18461C2.4932 7.88706 2.72825 7.65202 3.0258 7.53829C3.21892 7.46448 3.46137 7.46448 3.94627 7.46448C4.43118 7.46448 4.67363 7.46448 4.86675 7.53829C5.1643 7.65202 5.39934 7.88706 5.51307 8.18461C5.58688 8.37773 5.58688 8.62018 5.58688 9.10509V9.70167C5.58688 10.1866 5.58688 10.429 5.51307 10.6221C5.39934 10.9197 5.1643 11.1547 4.86675 11.2685C4.67363 11.3423 4.43118 11.3423 3.94627 11.3423C3.46137 11.3423 3.21892 11.3423 3.0258 11.2685C2.72825 11.1547 2.4932 10.9197 2.37948 10.6221C2.30566 10.429 2.30566 10.1866 2.30566 9.70167V9.10509Z" fill="white"/></g></svg>
    """
}
