package dev.lackluster.mihelper.data

object Pref {
    object Key {
        object Module {
            const val ENABLED = "enable_module"
            const val HIDE_ICON = "hide_icon"
            const val SHOW_IN_SETTINGS = "entry_in_settings"
            const val SETTINGS_ICON_STYLE = "entry_icon_style"
            const val SETTINGS_ICON_COLOR = "entry_icon_color"
            const val SETTINGS_NAME = "entry_name"
        }
        object Android {
            const val DISABLE_FREEFORM_RESTRICT = "android_freeform_restriction"
            const val ALLOW_MORE_FREEFORM = "android_freeform_allow_more"
            const val MULTI_TASK = "android_freeform_multi_task"
            const val HIDE_WINDOW_TOP_BAR = "android_freeform_hide_top_bar"
            const val BLOCK_FIXED_ORIENTATION = "android_no_fixed_orientation"
            const val BLOCK_FIXED_ORIENTATION_LIST = "android_no_fixed_orientation_list"
            const val WALLPAPER_SCALE_RATIO = "android_wallpaper_scale"
            const val BLOCK_FORCE_DARK_WHITELIST = "android_dark_mode_all"
        }
        object Browser {
            const val DEBUG_MODE = "browser_debug_mode"
            const val BLOCK_UPDATE = "browser_no_update"
            const val SWITCH_ENV = "browser_switch_env"
        }
        object Download {
            const val FUCK_XL = "download_remove_xl"
        }
        object Gallery {
            const val PATH_OPTIM = "gallery_path_optim"
            const val UNLIMITED_CROP = "screenshot_unlimited_crop"
        }
        object GuardProvider {
            const val BLOCK_UPLOAD_APP = "guard_forbid_upload_app"
        }
        object InCallUI {
            const val HIDE_CRBT = "incallui_hide_crbt"
        }
        object Joyose {
            const val BLOCK_CLOUD_CONTROL = "joyose_no_cloud_control"
        }
        object Market {
            const val AD_BLOCKER = "market_ad_block"
        }
        object MiAi {
            const val SEARCH_USE_BROWSER = "xiaoai_use_browser"
            const val SEARCH_ENGINE = "xiaoai_search_engine"
            const val SEARCH_URL = "xiaoai_search_url"
            const val HIDE_WATERMARK = "xiaoai_hide_watermark"
        }
        object MiLink {
            const val FUCK_HPPLAY = "milink_fuck_hpplay"
        }
        object MiMirror {
            const val CONTINUE_ALL_TASKS = "mismarthub_all_app"
        }
        object MiShare {
            const val ALWAYS_ON = "mishare_no_auto_off"
        }
        object MiTrust {
            const val DISABLE_RISK_CHECK = "mitrust_skip_risk_check"
        }
        object MiuiHome {
            const val REMOVE_REPORT = "home_remove_report"
            const val REFACTOR = "home_refactor"

            const val ICON_UNBLOCK_GOOGLE = "home_icon_unblock_google"
            const val ICON_CORNER4LARGE = "home_icon_corner4large"
            const val ICON_PERFECT = "home_icon_perfect_icon"
            const val PAD_RECENT_SHOW_MEMORY = "home_recent_pad_memory"
            const val RECENT_SHOW_REAL_MEMORY = "home_recent_real_memory"
            const val RECENT_WALLPAPER_DARKEN = "home_recent_wallpaper"
            const val RECENT_CARD_ANIM = "home_recent_anim"
            const val RECENT_DISABLE_FAKE_NAVBAR = "home_disable_fake_navbar"
            const val PAD_DOCK_TIME_DURATION = "home_pad_dock_time_duration"
            const val PAD_DOCK_SAFE_AREA_HEIGHT = "home_pad_dock_safe_height"
            const val WIDGET_ANIM = "home_widget_launch_anim"
            const val WIDGET_RESIZABLE = "home_widget_resizable"

            const val ALWAYS_SHOW_TIME = "home_always_show_time"
        }
        object MMS {
            const val AD_BLOCKER = "mms_ad_block"
        }
        object Music {
            const val AD_BLOCKER = "music_ad_block"
        }
        object PackageInstaller {
            const val BLOCK_UPLOAD_INFO = "package_block_upload"
            const val REMOVE_ELEMENT = "package_ad_block"
            const val DISABLE_COUNT_CHECK = "package_count_check"
            const val DISABLE_RISK_CHECK = "package_skip_risk_check"
            const val UPDATE_SYSTEM_APP = "package_update_system_app"
            const val MORE_INFO = "package_more_info"
        }
        object PowerKeeper {
            const val DO_NOT_KILL_APP = "power_donot_kill_app"
            const val BLOCK_BATTERY_WHITELIST = "power_battery_whitelist"
        }
        object ScreenRecorder {
            const val SAVE_TO_MOVIES = "screen_recorder_to_movies"
        }
        object Screenshot {
            const val SAVE_AS_PNG = "screenshot_save_png"
            const val SAVE_TO_PICTURE = "screenshot_to_picture"
        }
        object SecurityCenter {
            const val LOCK_SCORE = "security_lock_score"
            const val DISABLE_RISK_APP_NOTIF = "security_no_risk_notification"
            const val REMOVE_REPORT = "security_remove_report"
            const val SKIP_WARNING = "security_skip_warn"
            const val SKIP_OPEN_APP = "security_skip_open_app"
            const val SHOW_SCREEN_BATTERY = "security_screen_battery"
            const val DISABLE_BUBBLE_RESTRICT = "security_bubble_restriction"
            const val CTRL_SYSTEM_APP_WIFI = "security_system_app_wifi"
            const val CLICK_ICON_TO_OPEN = "security_click_icon_open"
        }
        object Settings {
            const val SHOE_GOOGLE = "settings_show_google"
            const val UNLOCK_VOIP_ASSISTANT = "settings_unlock_voip_assistant"
            const val UNLOCK_CUSTOM_REFRESH = "power_custom_refresh"
            const val UNLOCK_NET_MODE_SETTINGS = "phone_net_mode_settings"
            const val UNLOCK_TAPLUS_FOR_PAD = "taplus_unlock_pad"
        }
        object SystemUI {
            object StatusBar {
                const val CLOCK_LAYOUT_CUSTOM = "statusbar_clock_custom"
                const val CLOCK_PADDING_LEFT = "statusbar_clock_padding_left"
                const val CLOCK_PADDING_RIGHT = "statusbar_clock_padding_right"
                const val CLOCK_GEEK = "statusbar_clock_geek"
                const val CLOCK_GEEK_FORMAT = "statusbar_clock_pattern"
                const val CLOCK_GEEK_FORMAT_HORIZON = "statusbar_clock_pattern_horizon"
                const val CLOCK_GEEK_FORMAT_PAD = "statusbar_clock_pattern_pad"
                const val CLOCK_SHOW_AMPM = "statusbar_clock_show_ampm"
                const val CLOCK_SHOW_SECONDS = "statusbar_clock_show_seconds"
                const val CLOCK_SHOW_LEADING_ZERO = "statusbar_clock_show_zero"
                const val NOTIFICATION_COUNT = "statusbar_notif_max"
                const val NOTIFICATION_COUNT_ICON = "statusbar_notif_icon_max"
                const val DOUBLE_TAP_TO_SLEEP = "statusbar_double_tap_sleep"
            }
            object IconTurner {
                const val HIDE_SIM_ONE = "statusbar_hide_sim_one"
                const val HIDE_SIM_TWO = "statusbar_hide_sim_two"
                const val NO_SIM = "statusbar_hide_no_sim"
                const val HIDE_MOBILE_ACTIVITY = "statusbar_hide_mobile_activity"
                const val HIDE_MOBILE_TYPE = "statusbar_hide_mobile_type"
                const val HIDE_HD_SMALL = "statusbar_hide_hd_small"
                const val HIDE_HD_LARGE = "statusbar_hide_hd_large"
                const val HD_NEW = "statusbar_hide_hd_new"
                const val HIDE_HD_NO_SERVICE = "statusbar_hide_hd_no_service"
                const val WIFI = "statusbar_hide_wifi"
                const val HIDE_WIFI_ACTIVITY = "statusbar_hide_wifi_activity"
                const val HIDE_WIFI_TYPE = "statusbar_hide_wifi_type"
                const val HOTSPOT = "statusbar_hide_hotspot"
                const val HIDE_BATTERY = "statusbar_hide_battery"
                const val HIDE_BATTERY_PERCENT = "statusbar_hide_battery_percent"
                const val HIDE_CHARGE = "statusbar_hide_charge"
                const val SWAP_BATTERY_PERCENT = "statusbar_swap_battery_percent"
                const val BATTERY_CUSTOM_LAYOUT = "statusbar_battery_custom"
                const val BATTERY_PADDING_LEFT = "statusbar_battery_padding_left"
                const val BATTERY_PADDING_RIGHT = "statusbar_battery_padding_right"
                const val CHANGE_BATTERY_PERCENT_MARK = "statusbar_change_battery_percent_mark"
                const val CHANGE_BATTERY_PERCENT_SIZE = "statusbar_change_battery_percent_size"
                const val BATTERY_PERCENT_SIZE = "statusbar_battery_percent_size"
                const val FLIGHT_MODE = "statusbar_hide_flight_mode"
                const val GPS = "statusbar_hide_gps"
                const val BLUETOOTH = "statusbar_hide_bluetooth"
                const val BLUETOOTH_BATTERY = "statusbar_hide_bluetooth_battery"
                const val NFC = "statusbar_hide_nfc"
                const val VPN = "statusbar_hide_vpn"
                const val NET_SPEED = "statusbar_hide_net_speed"
                const val CAR = "statusbar_hide_car"
                const val PAD = "statusbar_hide_pad"
                const val PC = "statusbar_hide_pc"
                const val PHONE = "statusbar_hide_phone"
                const val SOUND_BOX = "statusbar_hide_sound_box"
                const val SOUND_BOX_GROUP = "statusbar_hide_sound_box_group"
                const val SOUND_BOX_SCREEN = "statusbar_hide_sound_box_screen"
                const val STEREO = "statusbar_hide_stereo"
                const val TV = "statusbar_hide_tv"
                const val WIRELESS_HEADSET = "statusbar_hide_wireless_headset"
                const val SWAP_MOBILE_WIFI = "statusbar_swap_mobile_wifi"
                const val ALARM = "statusbar_hide_alarm"
                const val HEADSET = "statusbar_hide_headset"
                const val VOLUME = "statusbar_hide_volume"
                const val ZEN = "statusbar_hide_zen"
            }
            object LockScreen {
                const val DOUBLE_TAP_TO_SLEEP = "systemui_double_tap_sleep"
                const val HIDE_UNLOCK_TIP = "systemui_lockscreen_hide_tip"
                const val HIDE_DISTURB = "systemui_lockscreen_hide_disturb"
                const val BLOCK_EDITOR = "systemui_lockscreen_block_editor"
            }
            object NotifCenter {
                const val CLOCK_COLOR_FIX = "statusbar_clock_color_fix"
                const val NOTIF_CHANNEL_SETTINGS = "systemui_notif_channel_setting"
                const val NOTIF_CHANNEL_DIALOG = "systemui_notif_channel_dialog"
                const val NOTIF_NO_WHITELIST = "systemui_notif_no_whitelist"
                const val NOTIF_FREEFORM = "systemui_notif_freeform"
                const val ADVANCED_TEXTURE = "systemui_notif_adv_texture"
            }
            object ControlCenter {
                const val HIDE_CARRIER_ONE = "statusbar_hide_carrier_one"
                const val HIDE_CARRIER_TWO = "statusbar_hide_carrier_two"
            }
//            object Others {
//
//            }
        }
        object Taplus {
            const val HIDE_SHOP = "taplus_hide_shop"
            const val SEARCH_USE_BROWSER = "taplus_use_browser"
            const val SEARCH_ENGINE = "taplus_search_engine"
            const val SEARCH_URL = "taplus_search_url"
        }
        object Themes {
            const val AD_BLOCKER = "themes_ad_block"
        }
        object Updater {
            const val DISABLE_VALIDATION = "updater_no_validation"
        }
    }
    object DefValue {
        object SystemUI {
            const val CLOCK_GEEK_FORMAT = "HH:mm"
            const val CLOCK_GEEK_FORMAT_HORIZON = "M d E"
            const val CLOCK_GEEK_FORMAT_PAD = "M d E"
        }
    }
}