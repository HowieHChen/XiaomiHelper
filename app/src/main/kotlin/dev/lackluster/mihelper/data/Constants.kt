package dev.lackluster.mihelper.data

import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.model.StatusBarIconSlotWrap

object Constants {
    const val CMD_LSPOSED = "am broadcast -a android.telephony.action.SECRET_CODE -d android_secret_code://5776733 android"
    const val PER_MIUI_INTERNAL_API = "miui.permission.USE_INTERNAL_GENERAL_API"
    const val ACTION_PREFIX = "hyperhelper.action."
    const val ACTION_NOTIFICATIONS = ACTION_PREFIX + "SYSTEM_ACTION_NOTIFICATIONS"
    const val ACTION_QUICK_SETTINGS = ACTION_PREFIX + "SYSTEM_ACTION_QUICK_SETTINGS"
    const val ACTION_SCREENSHOT = "android.intent.action.CAPTURE_SCREENSHOT"
    const val ACTION_HOME = ACTION_PREFIX + "SYSTEM_ACTION_HOME"
    const val ACTION_RECENTS = ACTION_PREFIX + "SYSTEM_ACTION_RECENTS"
    const val ACTION_FLOATING_WINDOW = ACTION_PREFIX + "SYSTEM_ACTION_FLOATING_WINDOW"
    const val ACTION_SCROLL_TO_TOP = ACTION_PREFIX + "SYSTEM_ACTION_SCROLL_TOP"

    const val BACKUP_FILE_PREFIX = "hyper_helper_backup_"

    const val UI_MODE_TYPE_SCALE_EXTRA_SMALL = 10 // 0.9f
    const val UI_MODE_TYPE_SCALE_SMALL = 12 // 0.9f
    const val UI_MODE_TYPE_SCALE_MEDIUM = 13 // 1.0f
    const val UI_MODE_TYPE_SCALE_LARGE = 14 // 1.1f
    const val UI_MODE_TYPE_SCALE_HUGE = 15 // 1.25f
    const val UI_MODE_TYPE_SCALE_GODZILLA = 11 // 1.45f
    const val UI_MODE_TYPE_SCALE_170 = 8 // 1.7f
    const val UI_MODE_TYPE_SCALE_200 = 9 // 2.0f

    const val VARIABLE_FONT_DEFAULT_PATH = "/system/fonts/MiSansVF.ttf"
    const val ASSETS_VF_MI_SANS_CONDENSED = "fonts/MiSansCondensed-Subset.ttf"
    const val ASSETS_VF_SF_PRO = "fonts/SFPro-Subset.ttf"

    const val REMOTE_FILE_STATUS_BAR_FONT = "status_bar_font.ttf"
    const val REMOTE_FILE_STACKED_MOBILE_TYPE_FONT = "stacked_signal_mobile_type_font.ttf"
    const val REMOTE_FILE_STACKED_SIGNAL_SINGLE = "stat_sys_stacked_signal_single.svg"
    const val REMOTE_FILE_STACKED_SIGNAL_STACKED = "stat_sys_stacked_signal_stacked.svg"

    const val ASSETS_SVG_SIGNAL_HYPER_OS_SINGLE = "svg/Signal-HyperOS-Single.svg"
    const val ASSETS_SVG_SIGNAL_HYPER_OS_STACKED = "svg/Signal-HyperOS-Stacked.svg"
    const val ASSETS_SVG_SIGNAL_IOS_SINGLE = "svg/Signal-iOS-Single.svg"
    const val ASSETS_SVG_SIGNAL_IOS_STACKED = "svg/Signal-iOS-Stacked.svg"

    const val CELLULAR_TYPE_LIST = ",G,E,3G,H,H+,4G,4G+,,LTE,5G,5G,5G+,5GA,5G"

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

        const val DEMO_MOBILE = "demo_mobile"

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
        "hd", "airplane", "mobile", IconSlots.DEMO_MOBILE, IconSlots.SINGLE_MOBILE_SIM1, IconSlots.SINGLE_MOBILE_SIM2,
        IconSlots.STACKED_MOBILE_ICON, IconSlots.STACKED_MOBILE_TYPE, "no_sim", "wifi", "demo_wifi",
    )

    val STATUS_BAR_ICONS_SWAP = listOf(
        "handle", "network_speed", "mute", "micphone", "headset", "mikey", "privacy_mode", "nfc", "gps",
        "missed_call", "managed_profile", "second_space", "ime", "cast", IconSlots.LOCATION, "stealth", "tty",
        IconSlots.ALARM_CLOCK, "vpn", "ethernet", "handle_battery", "bluetooth", "bluetooth_handsfree_battery",
        "hotspot", "sound_box_group", "stereo", "sound_box_screen", "sound_box", "wireless_headset",
        "zen", "volume", "dist_compute", "camera", "glasses", "car", "tv", "pc", "pad", "phone",
        "wifi", "demo_wifi", "hd", "airplane", "mobile", IconSlots.DEMO_MOBILE, IconSlots.SINGLE_MOBILE_SIM1, IconSlots.SINGLE_MOBILE_SIM2,
        IconSlots.STACKED_MOBILE_ICON, IconSlots.STACKED_MOBILE_TYPE, "no_sim",
    )


    val STATUS_BAR_ICON_SLOT_MAP = mutableMapOf(
        "handle" to StatusBarIconSlotWrap("handle", R.drawable.ic_stat_sys_unknown, 0),
        "network_speed" to StatusBarIconSlotWrap(
            "network_speed",
            R.drawable.ic_stat_sys_net_speed,
            R.string.icon_tuner_network_net_speed
        ),
        "mute" to StatusBarIconSlotWrap("mute", R.drawable.ic_stat_sys_unknown, 0),
        "micphone" to StatusBarIconSlotWrap("micphone", R.drawable.ic_stat_sys_micphone, 0),
        "headset" to StatusBarIconSlotWrap(
            "headset",
            R.drawable.ic_stat_sys_headset,
            R.string.icon_tuner_connect_headset
        ),
        "mikey" to StatusBarIconSlotWrap("mikey", R.drawable.ic_stat_sys_unknown, 0),
        "privacy_mode" to StatusBarIconSlotWrap("privacy_mode", R.drawable.ic_stat_sys_unknown, 0),
        "nfc" to StatusBarIconSlotWrap(
            "nfc",
            R.drawable.ic_stat_sys_nfc,
            R.string.icon_tuner_connect_nfc
        ),
        "gps" to StatusBarIconSlotWrap("gps", R.drawable.ic_stat_sys_unknown, 0),
        "missed_call" to StatusBarIconSlotWrap("missed_call", R.drawable.ic_stat_sys_unknown, 0),
        "managed_profile" to StatusBarIconSlotWrap(
            "managed_profile",
            R.drawable.ic_stat_sys_managed_profile,
            0
        ),
        "second_space" to StatusBarIconSlotWrap(
            "second_space",
            R.drawable.ic_stat_sys_second_space,
            R.string.icon_tuner_other_second_space
        ),
        "ime" to StatusBarIconSlotWrap("ime", R.drawable.ic_stat_sys_unknown, 0),
        "cast" to StatusBarIconSlotWrap("cast", R.drawable.ic_stat_sys_unknown, 0),
        IconSlots.LOCATION to StatusBarIconSlotWrap(
            IconSlots.LOCATION,
            R.drawable.ic_stat_sys_location,
            R.string.icon_tuner_connect_location
        ),
        "stealth" to StatusBarIconSlotWrap("stealth", R.drawable.ic_stat_sys_stealth, 0),
        "tty" to StatusBarIconSlotWrap("tty", R.drawable.ic_stat_sys_tty, 0),
        IconSlots.ALARM_CLOCK to StatusBarIconSlotWrap(
            IconSlots.ALARM_CLOCK,
            R.drawable.ic_stat_sys_alarm_clock,
            R.string.icon_tuner_other_alarm
        ),
        "vpn" to StatusBarIconSlotWrap(
            "vpn",
            R.drawable.ic_stat_sys_vpn,
            R.string.icon_tuner_network_vpn
        ),
        "ethernet" to StatusBarIconSlotWrap("ethernet", R.drawable.ic_stat_sys_unknown, 0),
        "handle_battery" to StatusBarIconSlotWrap(
            "handle_battery",
            R.drawable.ic_stat_sys_handle_battery,
            R.string.icon_tuner_connect_handle_battery
        ),
        "bluetooth" to StatusBarIconSlotWrap(
            "bluetooth",
            R.drawable.ic_stat_sys_bluetooth,
            R.string.icon_tuner_connect_bluetooth
        ),
        "bluetooth_handsfree_battery" to StatusBarIconSlotWrap(
            "bluetooth_handsfree_battery",
            R.drawable.ic_stat_sys_bluetooth_handsfree_battery,
            R.string.icon_tuner_connect_bluetooth_battery
        ),
        "hotspot" to StatusBarIconSlotWrap(
            "hotspot",
            R.drawable.ic_stat_sys_hotspot,
            R.string.icon_tuner_network_hotspot
        ),
        "sound_box_group" to StatusBarIconSlotWrap(
            "sound_box_group",
            R.drawable.ic_stat_sys_sound_box_group,
            R.string.icon_tuner_device_sound_box_group
        ),
        "stereo" to StatusBarIconSlotWrap(
            "stereo",
            R.drawable.ic_stat_sys_stereo,
            R.string.icon_tuner_device_stereo
        ),
        "sound_box_screen" to StatusBarIconSlotWrap(
            "sound_box_screen",
            R.drawable.ic_stat_sys_sound_box_screen,
            R.string.icon_tuner_device_sound_box_screen
        ),
        "sound_box" to StatusBarIconSlotWrap(
            "sound_box",
            R.drawable.ic_stat_sys_sound_box,
            R.string.icon_tuner_device_sound_box
        ),
        "wireless_headset" to StatusBarIconSlotWrap(
            "wireless_headset",
            R.drawable.ic_stat_sys_wireless_headset,
            R.string.icon_tuner_device_wireless_headset
        ),
        IconSlots.ZEN to StatusBarIconSlotWrap(
            IconSlots.ZEN,
            R.drawable.ic_stat_sys_zen,
            R.string.icon_tuner_other_zen
        ),
        IconSlots.VOLUME to StatusBarIconSlotWrap(
            IconSlots.VOLUME,
            R.drawable.ic_stat_sys_volume,
            R.string.icon_tuner_other_volume
        ),
        "dist_compute" to StatusBarIconSlotWrap(
            "dist_compute",
            R.drawable.ic_stat_sys_dist_compute,
            R.string.icon_tuner_device_dist_compute
        ),
        "camera" to StatusBarIconSlotWrap(
            "camera",
            R.drawable.ic_stat_sys_camera,
            R.string.icon_tuner_device_camera
        ),
        "glasses" to StatusBarIconSlotWrap(
            "glasses",
            R.drawable.ic_stat_sys_glasses,
            R.string.icon_tuner_device_glasses
        ),
        "car" to StatusBarIconSlotWrap(
            "car",
            R.drawable.ic_stat_sys_car,
            R.string.icon_tuner_device_car
        ),
        "tv" to StatusBarIconSlotWrap(
            "tv",
            R.drawable.ic_stat_sys_tv,
            R.string.icon_tuner_device_tv
        ),
        "pc" to StatusBarIconSlotWrap(
            "pc",
            R.drawable.ic_stat_sys_pc,
            R.string.icon_tuner_device_pc
        ),
        "pad" to StatusBarIconSlotWrap(
            "pad",
            R.drawable.ic_stat_sys_pad,
            R.string.icon_tuner_device_pad
        ),
        "phone" to StatusBarIconSlotWrap(
            "phone",
            R.drawable.ic_stat_sys_phone,
            R.string.icon_tuner_device_phone
        ),
        "hd" to StatusBarIconSlotWrap("hd", R.drawable.ic_stat_sys_unknown, 0),
        "airplane" to StatusBarIconSlotWrap(
            "airplane",
            R.drawable.ic_stat_sys_airplane,
            R.string.icon_tuner_network_airplane
        ),
        "mobile" to StatusBarIconSlotWrap(
            "mobile",
            R.drawable.ic_stat_sys_mobile,
            R.string.icon_tuner_network_mobile
        ),
        IconSlots.DEMO_MOBILE to StatusBarIconSlotWrap(
            IconSlots.DEMO_MOBILE,
            R.drawable.ic_stat_sys_mobile,
            R.string.icon_tuner_position_demo_mobile
        ),
        "no_sim" to StatusBarIconSlotWrap(
            "no_sim",
            R.drawable.ic_stat_sys_no_sim,
            R.string.icon_tuner_network_no_sim
        ),
        "wifi" to StatusBarIconSlotWrap(
            "wifi",
            R.drawable.ic_stat_sys_wifi,
            R.string.icon_tuner_network_wifi
        ),
        "demo_wifi" to StatusBarIconSlotWrap(
            "demo_wifi",
            R.drawable.ic_stat_sys_wifi,
            R.string.icon_tuner_position_demo_wifi
        ),
        IconSlots.COMPOUND_ICON_STUB to StatusBarIconSlotWrap(
            IconSlots.COMPOUND_ICON_STUB,
            R.drawable.ic_stat_sys_compound,
            R.string.icon_tuner_compound_icon
        ),
        IconSlots.STACKED_MOBILE_ICON to StatusBarIconSlotWrap(
            IconSlots.STACKED_MOBILE_ICON,
            R.drawable.ic_stat_sys_stacked_icon,
            R.string.icon_tuner_stacked_mobile_icon
        ),
        IconSlots.STACKED_MOBILE_TYPE to StatusBarIconSlotWrap(
            IconSlots.STACKED_MOBILE_TYPE,
            R.drawable.ic_stat_sys_stacked_type,
            R.string.ui_title_icon_detail_stacked_type
        ),
        IconSlots.SINGLE_MOBILE_SIM1 to StatusBarIconSlotWrap(
            IconSlots.SINGLE_MOBILE_SIM1,
            R.drawable.ic_stat_sys_single_sim1,
            R.string.icon_tuner_single_mobile_sim1
        ),
        IconSlots.SINGLE_MOBILE_SIM2 to StatusBarIconSlotWrap(
            IconSlots.SINGLE_MOBILE_SIM2,
            R.drawable.ic_stat_sys_single_sim2,
            R.string.icon_tuner_single_mobile_sim2
        ),
    )

    val COMPOUND_ICON_REAL_SLOTS = listOf(
        IconSlots.COMPOUND_ICON_REAL_LOCATION,
        IconSlots.COMPOUND_ICON_REAL_ALARM_CLOCK,
        IconSlots.COMPOUND_ICON_REAL_ZEN,
        IconSlots.COMPOUND_ICON_REAL_VIBRATE,
        IconSlots.COMPOUND_ICON_REAL_MUTE,
    )
    const val COMPOUND_ICON_PRIORITY_STR = "${IconSlots.LOCATION},${IconSlots.ALARM_CLOCK},${IconSlots.ZEN},${IconSlots.VOLUME}"
}
