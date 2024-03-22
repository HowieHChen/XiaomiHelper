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
}