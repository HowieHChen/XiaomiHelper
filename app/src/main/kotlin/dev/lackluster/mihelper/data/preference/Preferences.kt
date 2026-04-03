package dev.lackluster.mihelper.data.preference

import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.utils.Device

object Preferences {
    const val VERSION = 10

    object AiEngine {
        val OPEN_LINK_WITH_CUSTOM_BROWSER = PreferenceKey("aicr_link_browser", false)
    }

    object App {
        val ENABLE_SPLIT_SCREEN = PreferenceKey("app_disable_split", Device.isPad)
        val HAZE_BLUR = PreferenceKey("app_haze_blur", true)
        val HAZE_LIGHT_BLUR_ALPHA = PreferenceKey("app_haze_light_blur_alpha", 0.8f)
        val HAZE_DARK_BLUR_ALPHA = PreferenceKey("app_haze_dark_blur_alpha", 0.7f)
        val MODULE_ENABLED = PreferenceKey("enable_module", true)
        val SKIP_ROOT_CHECK = PreferenceKey("app_ignore_root", false)
        val HIDE_ICON = PreferenceKey("hide_icon", false)
    }

    object Browser {
        val AD_BLOCKER = PreferenceKey("browser_ad_block", false)
        val SKIP_SPLASH = PreferenceKey("browser_skip_splash", false)
        val SHOW_SUG_SWITCH_ENTRY = PreferenceKey("browser_show_sug_switch_view", false)
        val HIDE_HOMEPAGE_TOP_BAR = PreferenceKey("browser_hide_home_top_bar", false)
        val BLOCK_DIALOG = PreferenceKey("browser_block_dialog", false)
        val DEBUG_MODE = PreferenceKey("browser_debug_mode", false)
        val SWITCH_ENV = PreferenceKey("browser_switch_env", false)
        val BLOCK_UPDATE = PreferenceKey("browser_no_update", false)
    }

    object Download {
        val FUCK_XL = PreferenceKey("download_remove_xl", false)
    }

    object DownloadUI {
        val HIDE_XL = PreferenceKey("downloadui_remove_xl", false)
    }

    object HintState {
        val SYSTEM_FONT_SCALE = PreferenceKey("hint_system_font_scale", true)
        val STATUS_BAR_FONT_ROOT = PreferenceKey("hint_status_bar_font_root", true)
        val ICON_TUNER_IGNORE_SYS = PreferenceKey("hint_icon_tuner_general", true)
        val MEDIA_ADVANCED_TEXTURES = PreferenceKey("hint_media_adv_textures", true)
        val MEDIA_AMBIENT_LIGHTING = PreferenceKey("hint_media_ambient_light", true)
    }

    object GuardProvider {
        val BLOCK_UPLOAD_APP = PreferenceKey("guard_forbid_upload_app", false)
        val BLOCK_ENV_CHECK = PreferenceKey("guard_block_env_check", false)
    }

    object InCallUI {
        val HIDE_CRBT = PreferenceKey("incallui_hide_crbt", false)
    }

    object LBE {
        val BLOCK_REMOVE_AUTO_STARTUP = PreferenceKey("lbe_block_rec_auto_startup", false)
        val TOAST_CLIPBOARD_USAGE = PreferenceKey("lbe_clipboard_toast", false)
    }

    object Market {
        val AD_BLOCKER = PreferenceKey("market_ad_block", false)
        val SKIP_SPLASH = PreferenceKey("market_skip_splash", false)
        val BLOCK_UPDATE_DIALOG = PreferenceKey("market_block_up_dialog", false)
        val HIDE_APP_SECURITY = PreferenceKey("market_hide_app_security", false)
        val TAB_BLUR = PreferenceKey("market_tab_blur", false)
        val DISABLE_CUSTOMIZE_ICON = PreferenceKey("market_diable_customize_icon", false)
        val ENABLE_FILTER_TAB = PreferenceKey("market_filter_tab", false)
        val FILTER_TAB_IGNORE_RESTRICT = PreferenceKey("market_tab_ignore_restrict", false)
        val HIDE_TAB_HOME = PreferenceKey("market_hide_tab_home", false)
        val HIDE_TAB_GAME = PreferenceKey("market_hide_tab_game", false)
        val HIDE_TAB_RANK = PreferenceKey("market_hide_tab_rank", false)
        val HIDE_TAB_AGENT = PreferenceKey("market_hide_tab_agent", false)
        val HIDE_TAB_APP_ASSEMBLE = PreferenceKey("market_hide_tab_assemble", false)
        val HIDE_TAB_MINI_GAME = PreferenceKey("market_hide_tab_mini_game", false)
        val HIDE_TAB_MINE = PreferenceKey("market_hide_tab_mine", false)
        val HIDE_TAB_OTHERS = PreferenceKey("market_hide_tab_others", false)
    }

    object MiAi {
        val HIDE_WATERMARK = PreferenceKey("xiaoai_hide_watermark", false)
        val SEARCH_USE_BROWSER = PreferenceKey("xiaoai_use_browser", false)
        val SEARCH_ENGINE = PreferenceKey("xiaoai_search_engine", 0)
        val CUSTOM_SEARCH_URL = PreferenceKey("xiaoai_search_url", "")
    }

    object MiLink {
        val FUCK_HPPLAY = PreferenceKey("milink_fuck_hpplay", false)
    }

    object MiMirror {
        val CONTINUE_ALL_TASKS = PreferenceKey("mismarthub_all_app", false)
    }

    object MiTrust {
        val BLOCK_RISK_CHECK = PreferenceKey("mitrust_skip_risk_check", false)
    }

    object MiuiHome {
        val DISABLE_ICON_ZOOM_ANIM = PreferenceKey("home_anim_icon_zoom", false)
        val DISABLE_ICON_DARKEN_ANIM = PreferenceKey("home_anim_icon_darken", false)
        val DISABLE_FOLDER_ZOOM_ANIM = PreferenceKey("home_anim_folder_zoom", false)
        val DISABLE_FOLDER_DARKEN_ANIM = PreferenceKey("home_anim_folder_icon_darken", false)
        val FOLDER_ADAPT_SIZE = PreferenceKey("home_folder_aapt_size", false)
        val BACK_GESTURE_HAPTIC = PreferenceKey("home_back_gesture_haptic", 0)
        val FIX_PREDICTIVE_BACK_PROG = PreferenceKey("home_back_predictive_prog", false)
//        const val QUICK_SWITCH = "home_quick_back"
//        const val QUICK_SWITCH_LEFT = "home_quick_back_left"
//        const val QUICK_SWITCH_RIGHT = "home_quick_back_right"
        val LINE_GESTURE_DOUBLE_TAP = PreferenceKey("home_line_double_tap", 0)
        val LINE_GESTURE_LONG_PRESS = PreferenceKey("home_line_long_press", 0)
        val OPT_RECENT_CARD_ANIM = PreferenceKey("home_recent_anim", false)
        val HIDE_RECENT_CLEAR_BUTTON = PreferenceKey("home_recent_hide_clear_button", false)
        val RECENT_MEM_INFO_CLEAR = PreferenceKey("home_recent_mem_info_clear", false)
        val SHOW_RECENT_REAL_MEMORY = PreferenceKey("home_recent_real_memory", false)
        val REMOVE_REPORT = PreferenceKey("home_remove_report", false)
        val REMOVE_DOCK_NUM_LIMIT = PreferenceKey("home_dock_remove_num_limit", false)
        val RESTORE_MINUS_SETTING = PreferenceKey("home_minus_restore", false)
        val DISABLE_RECENT_FAKE_NAVBAR = PreferenceKey("home_disable_fake_navbar", false)
        val FORCE_COLOR_TEXT_ICON = PreferenceKey("home_force_color_text_icon", 0)
        val FORCE_COLOR_STATUS_BAR = PreferenceKey("home_force_color_status_bar", 0)
        val FORCE_COLOR_MINUS = PreferenceKey("home_force_color_minus", 0)
        //            const val DOUBLE_TAP_TO_SLEEP = "home_double_tap_sleep"
        //            const val PAD_RECENT_SHOW_MEMORY = "home_recent_pad_memory"
        //            const val PAD_RECENT_HIDE_WORLD = "home_recent_pad_world"
    }

    object MMS {
        val AD_BLOCKER = PreferenceKey("mms_ad_block", false)
    }

    object Module {
        val DEX_KIT_CACHE = PreferenceKey("dexkit_cache", true)
        val SHOW_IN_SETTINGS = PreferenceKey("entry_in_settings", false)
        val SETTINGS_ICON_STYLE = PreferenceKey("entry_icon_style", 0)
        val SETTINGS_ICON_COLOR = PreferenceKey("entry_icon_color", 0)
        val SETTINGS_NAME = PreferenceKey("entry_name", 0)
        val CUSTOM_SETTINGS_NAME = PreferenceKey("entry_name_custom", "Hyper Helper")
        val SP_VERSION = PreferenceKey("sp_version", VERSION)
    }

    object Music {
        val AD_BLOCKER = PreferenceKey("music_ad_block", false)
        val SKIP_SPLASH = PreferenceKey("music_skip_splash", false)
        val HIDE_KARAOKE = PreferenceKey("music_hide_karaoke", false)
        val HIDE_LONG_AUDIO = PreferenceKey("music_hide_long_audio", false)
        val HIDE_DISCOVER = PreferenceKey("music_hide_discover", false)
        val HIDE_MY_BANNER = PreferenceKey("music_my_hide_banner", false)
        val HIDE_MY_REC_PLAYLIST = PreferenceKey("music_my_hide_rec", false)
        val HIDE_FAV_NUM = PreferenceKey("music_hide_fav_num", false)
        val HIDE_LISTEN_COUNT = PreferenceKey("music_hide_listen_count", false)
    }

    object PackageInstaller {
        val REMOVE_ELEMENT = PreferenceKey("package_ad_block", false)
        val DISABLE_RISK_CHECK = PreferenceKey("package_skip_risk_check", false)
        val DISGUISE_NO_NETWORK = PreferenceKey("package_no_network", false)
        val DISABLE_COUNT_CHECK = PreferenceKey("package_count_check", false)
        val BLOCK_UPLOAD_INFO = PreferenceKey("package_block_upload", false)
        val CUSTOM_INSTALL_SOURCE = PreferenceKey("package_install_source", 0)
        val INSTALL_SOURCE_PKG = PreferenceKey("package_source_pkg", "")
    }

    object PowerKeeper {
        val UNLOCK_CUSTOM_REFRESH = PreferenceKey("power_custom_refresh", false)
        val BLOCK_BATTERY_WHITELIST = PreferenceKey("power_battery_whitelist", false)
        val GMS_BG_RUNNING = PreferenceKey("power_gms_bg_running", false)
    }

    object RemoteController {
        val AD_BLOCKER = PreferenceKey("remote_ad_block", false)
    }

    object Search {
        val MORE_SEARCH_ENGINE = PreferenceKey("search_more_engine", false)
        val ENABLE_CUSTOM_SEARCH_ENGINE = PreferenceKey("search_custom_engine", false)
        val CUSTOM_SEARCH_ENGINE_ENTITY = PreferenceKey("search_custom_engine_entity", "")
    }

    object SecurityCenter {
        val SKIP_WARNING_DIALOG = PreferenceKey("security_skip_warn", false)
        val LINK_START = PreferenceKey("security_link_start", 0)
        val BATTERY_SHOW_SCREEN = PreferenceKey("security_screen_battery", false)
        val BATTERY_SHOW_SYSTEM = PreferenceKey("security_system_battery", false)
        val CLICK_ICON_TO_OPEN = PreferenceKey("security_click_icon_open", false)
        val CTRL_SYSTEM_APP_WIFI = PreferenceKey("security_system_app_wifi", false)
        val DISABLE_BUBBLE_RESTRICT = PreferenceKey("security_bubble_restriction", false)
        val DISABLE_RISK_APP_NOTIF = PreferenceKey("security_no_risk_notification", false)
        val SKIP_SPLASH = PreferenceKey("security_skip_splash", false)
        val LOCK_SCORE = PreferenceKey("security_lock_score", false)
        val HIDE_HOME_RED_DOT = PreferenceKey("security_hide_red_dot", false)
        val HIDE_HOME_REC = PreferenceKey("security_hide_home_rec", false)
        val HIDE_HOME_COMMON = PreferenceKey("security_hide_home_common", false)
        val HIDE_HOME_POPULAR = PreferenceKey("security_hide_home_popular", false)
        val REMOVE_REPORT = PreferenceKey("security_remove_report", false)
    }

    object Settings {
        val SHOW_GOOGLE_ENTRY = PreferenceKey("settings_show_google", false)
        val UNLOCK_TAPLUS_FOR_PAD = PreferenceKey("taplus_unlock_pad", false)
        val QUICK_PER_OVERLAY = PreferenceKey("settings_quick_per_overlay", false)
        val QUICK_PER_INSTALL_SOURCE = PreferenceKey("settings_quick_per_install", false)
    }

    object System {
        val ENABLE_FONT_SCALE = PreferenceKey("android_font_scale", false)
        val FONT_SCALE_SMALL = PreferenceKey("android_font_scale_small", 0.9f)
        val FONT_SCALE_MEDIUM = PreferenceKey("android_font_scale_medium", 1.0f)
        val FONT_SCALE_LARGE = PreferenceKey("android_font_scale_large", 1.1f)
        val FONT_SCALE_HUGE = PreferenceKey("android_font_scale_huge", 1.25f)
        val FONT_SCALE_GODZILLA = PreferenceKey("android_font_scale_godzilla", 1.45f)
        val FONT_SCALE_170 = PreferenceKey("android_font_scale_170", 1.7f)
        val FONT_SCALE_200 = PreferenceKey("android_font_scale_200", 2.0f)
        val DISABLE_FREEFORM_RESTRICT = PreferenceKey("android_freeform_restriction", false)
        val ALLOW_MORE_FREEFORM = PreferenceKey("android_freeform_allow_more", false)
        val DISABLE_FORCE_DARK_WHITELIST = PreferenceKey("android_dark_mode_all", false)
    }

    object SystemUI {
        object ControlCenter {
            val HIDE_CARRIER_ONE = PreferenceKey("statusbar_hide_carrier_one", false)
            val HIDE_CARRIER_TWO = PreferenceKey("statusbar_hide_carrier_two", false)
            val HIDE_CARRIER_HD = PreferenceKey("statusbar_hide_carrier_hd", false)
        }

        object LockScreen {
            val HIDE_DISTURB_NOTIF = PreferenceKey("systemui_lockscreen_hide_disturb", false)
            val KEEP_NOTIFICATION = PreferenceKey("systemui_lockscreen_keep_notif", false)
            val DOUBLE_TAP_TO_SLEEP = PreferenceKey("systemui_double_tap_sleep", false)
//            val CARRIER_TEXT = PreferenceKey("systemui_lockscreen_carrier_text", false)
            val KEEP_START_CONTAINER = PreferenceKey("systemui_ls_keep_clock", false)
            val HIDE_NEXT_ALARM = PreferenceKey("systemui_ls_hide_next_alarm", false)
            val HIDE_CARRIER_ONE = PreferenceKey("systemui_ls_hide_carrier_one", false)
            val HIDE_CARRIER_TWO = PreferenceKey("systemui_ls_hide_carrier_two", false)
            val FORCE_COLOR_STATUS_BAR = PreferenceKey("systemui_ls_force_color_status_bar", 0)
        }

        object NotifCenter {
            val ALWAYS_ALLOW_FREEFORM = PreferenceKey("systemui_notif_freeform", false)
            val DISABLE_NOTIF_WHITELIST = PreferenceKey("systemui_notif_no_whitelist", false)
            val SUPPRESS_FOLD_NOTIF = PreferenceKey("systemui_notif_suppress_fold", false)
//            val CLOCK_PAD_ANIM = "statusbar_clock_pad_anim"
            val MIUIX_EXPAND_BUTTON = PreferenceKey("systemui_notif_miuix_expand", false)
            val AUTO_EXPAND_NOTIF = PreferenceKey("systemui_notif_expand_notifs", 0)
            val EXPAND_IGNORE_FOCUS = PreferenceKey("systemui_notif_exp_ignore_focus", false)
            val ENABLE_LAYOUT_RANK_OPT = PreferenceKey("systemui_notif_layout_rank_opt", false)
            val LR_OPT_HIDE_SECTION_HEADER = PreferenceKey("systemui_notif_lr_opt_hide_header", false)
            val LR_OPT_HIDE_SECTION_GAP = PreferenceKey("systemui_notif_lr_opt_hide_gap", false)
            val LR_OPT_RERANK = PreferenceKey("systemui_notif_lr_rerank", false)
            val ENABLE_MONET_OVERLAY = PreferenceKey("systemui_notif_monet_overlay", false)
            val MONET_OVERLAY_COLOR = PreferenceKey("systemui_notif_monet_color", "#3482FF")
        }

        object Plugin {
            val CONTROL_CENTER_HIDE_EDIT = PreferenceKey("sys_plugin_cc_hide_edit", false)
            val LOCKSCREEN_AUTO_FLASH_ON = PreferenceKey("sys_plugin_flash_on", false)
            val DISABLE_ISLAND_NOTIF_WHITELIST = PreferenceKey("sys_plugin_island_whitelist", false)
            val DISABLE_ISLAND_MEDIA_WHITELIST = PreferenceKey("sys_plugin_island_media_whitelist", false)
        }

        object StatusBar {
            val ENABLE_NOTIF_MAX_COUNT = PreferenceKey("statusbar_notif_max", false)
            val NOTIF_MAX_COUNT = PreferenceKey("statusbar_notif_icon_max", 3)
            val DOUBLE_TAP_TO_SLEEP = PreferenceKey("statusbar_double_tap_sleep", false)
            val REGION_SAMPLING = PreferenceKey("statusbar_region_sampling", 0)

            object Font {
                private val defaultClockFontWeight = if (Device.isPad) 460 else 500

                val FONT_PATH_INTERNAL = PreferenceKey("sb_font_path_app", Constants.VARIABLE_FONT_DEFAULT_PATH)
                val FONT_PATH_ORIGINAL = PreferenceKey("sb_font_path_real", Constants.VARIABLE_FONT_DEFAULT_PATH)

                val CUSTOM_LOCK_SCREEN_CARRIER = PreferenceKey("sb_font_ls_carrier", false)
                val LOCK_SCREEN_CARRIER_WEIGHT = PreferenceKey("sb_font_ls_carrier_weight", 430)

                val CUSTOM_CLOCK = PreferenceKey("sb_font_clock", false)
                val CLOCK_WEIGHT = PreferenceKey("sb_font_clock_weight", defaultClockFontWeight)
                val CUSTOM_PAD_CLOCK = PreferenceKey("sb_font_pad_clock", false)
                val PAD_CLOCK_WEIGHT = PreferenceKey("sb_font_pad_clock_weight", defaultClockFontWeight)
                val CUSTOM_BIG_TIME = PreferenceKey("sb_font_big_time", false)
                val BIG_TIME_WEIGHT = PreferenceKey("sb_font_big_time_weight", 305)
                val CUSTOM_DATE_TIME = PreferenceKey("sb_font_date_time", false)
                val DATE_TIME_WEIGHT = PreferenceKey("sb_font_date_time_weight", 400)
                val CUSTOM_CC_DATE = PreferenceKey("sb_font_cc_date", false)
                val CC_DATE_WEIGHT = PreferenceKey("sb_font_cc_date_weight", 400)
                val CUSTOM_HORIZONTAL_TIME = PreferenceKey("sb_font_horizontal_time", false)
                val HORIZONTAL_TIME_WEIGHT = PreferenceKey("sb_font_horizontal_time_weight", defaultClockFontWeight)

                val CUSTOM_CELLULAR_TYPE = PreferenceKey("sb_font_mobile_type", false)
                val CELLULAR_TYPE_WEIGHT = PreferenceKey("sb_font_mobile_type_weight", 660)
                val CUSTOM_CELLULAR_TYPE_SINGLE = PreferenceKey("sb_font_mobile_type_single", false)
                val CELLULAR_TYPE_SINGLE_WEIGHT = PreferenceKey("sb_font_mobile_type_single_weight", 400)

                val CUSTOM_BATTERY_PERCENTAGE_IN = PreferenceKey("sb_font_battery_pct_in", false)
                val BATTERY_PERCENTAGE_IN_WEIGHT = PreferenceKey("sb_font_bat_pct_in_weight", 620)
                val CUSTOM_BATTERY_PERCENTAGE_OUT = PreferenceKey("sb_font_bat_pct_out", false)
                val BATTERY_PERCENTAGE_OUT_WEIGHT = PreferenceKey("sb_font_bat_pct_out_weight", 500)
                val CUSTOM_BATTERY_PERCENTAGE_MARK = PreferenceKey("sb_font_bat_pct_mark", false)
                val BATTERY_PERCENTAGE_MARK_WEIGHT = PreferenceKey("sb_font_bat_pct_mark_weight", 600)

                val CUSTOM_NET_SPEED_NUMBER = PreferenceKey("sb_font_speed_num", false)
                val NET_SPEED_NUMBER_WEIGHT = PreferenceKey("sb_font_speed_num_weight", 630)
                val CUSTOM_NET_SPEED_UNIT = PreferenceKey("sb_font_speed_unit", false)
                val NET_SPEED_UNIT_WEIGHT = PreferenceKey("sb_font_speed_unit_weight", 630)
                val CUSTOM_NET_SPEED_SEPARATE = PreferenceKey("sb_font_speed_separate", false)
                val NET_SPEED_SEPARATE_WEIGHT = PreferenceKey("sb_font_speed_separate_weight", 630)
            }

            object Clock {
                val CUSTOM_HORIZON_PADDING = PreferenceKey("statusbar_clock_padding_horizon", false)
                val PADDING_START_VAL = PreferenceKey("statusbar_clock_padding_start_val", 0.0f)
                val PADDING_END_VAL = PreferenceKey("statusbar_clock_padding_end_val", 0.0f)
                val ENABLE_GEEK_MODE = PreferenceKey("statusbar_clock_geek", false)
                val FIXED_WIDTH = PreferenceKey("statusbar_clock_fixed_width", false)
                val GEEK_FORMAT_CLOCK = PreferenceKey("statusbar_clock_pattern_clock", "H:mm")
                val GEEK_FORMAT_PAD_CLOCK = PreferenceKey("statusbar_clock_pattern_pad_clock", "M d E")
                val GEEK_FORMAT_BIG_TIME = PreferenceKey("statusbar_clock_pattern_big_time", "H:mm")
                val GEEK_FORMAT_DATE_TIME = PreferenceKey("statusbar_clock_pattern_date_time", "M d E")
                val GEEK_FORMAT_CC_DATE = PreferenceKey("statusbar_clock_pattern_cc_date", "M d E")
                val GEEK_FORMAT_HORIZON_TIME = PreferenceKey("statusbar_clock_pattern_horizon_time", "H:mm M d E")
                val EASY_SHOW_AMPM = PreferenceKey("statusbar_clock_show_ampm", false)
                val EASY_SHOW_SECONDS = PreferenceKey("statusbar_clock_show_seconds", false)
                val EASY_SHOW_LEADING_ZERO = PreferenceKey("statusbar_clock_show_zero", false)
            }

            object IconTuner {
                val IGNORE_SYS_SETTINGS = PreferenceKey("statusbar_ignore_sys_hide", false)
                val ICON_POSITION = PreferenceKey("icon_tuner_position", 0)
                val ICON_POSITION_VAL = PreferenceKey("icon_tuner_position_val", emptySet<String>())
                val ICON_POSITION_REORDER = PreferenceKey("icon_tuner_position_reorder", false)

                // Dropdowns (0: Default, 1: Show All, 2: Show StatusBar, 3: Show QS, 4: Hidden)
                val MOBILE = PreferenceKey("icon_tuner_slot_mobile", 0)
                val NO_SIM = PreferenceKey("icon_tuner_slot_no_sim", 0)
                val AIRPLANE = PreferenceKey("icon_tuner_slot_airplane", 0)
                val WIFI = PreferenceKey("icon_tuner_slot_wifi", 0)
                val HOTSPOT = PreferenceKey("icon_tuner_slot_hotspot", 0)
                val VPN = PreferenceKey("icon_tuner_slot_vpn", 0)
                val NET_SPEED = PreferenceKey("icon_tuner_slot_net_speed", 0)

                val BLUETOOTH = PreferenceKey("icon_tuner_slot_bluetooth", 0)
                val BLUETOOTH_BATTERY = PreferenceKey("icon_tuner_slot_bluetooth_battery", 0)
                val HANDLE_BATTERY = PreferenceKey("icon_tuner_slot_handle_battery", 0)
                val NFC = PreferenceKey("icon_tuner_slot_nfc", 0)
                val HEADSET = PreferenceKey("icon_tuner_slot_headset", 0)
                val LOCATION = PreferenceKey("icon_tuner_slot_location", 0)

                val WIRELESS_HEADSET = PreferenceKey("icon_tuner_slot_wireless_headset", 0)
                val PHONE = PreferenceKey("icon_tuner_slot_phone", 0)
                val PAD = PreferenceKey("icon_tuner_slot_pad", 0)
                val PC = PreferenceKey("icon_tuner_slot_pc", 0)
                val SOUND_BOX_GROUP = PreferenceKey("icon_tuner_slot_sound_box_group", 0)
                val STEREO = PreferenceKey("icon_tuner_slot_stereo", 0)
                val SOUND_BOX_SCREEN = PreferenceKey("icon_tuner_slot_sound_box_screen", 0)
                val SOUND_BOX = PreferenceKey("icon_tuner_slot_sound_box", 0)
                val TV = PreferenceKey("icon_tuner_slot_tv", 0)
                val GLASSES = PreferenceKey("icon_tuner_slot_glasses", 0)
                val CAR = PreferenceKey("icon_tuner_slot_car", 0)
                val CAMERA = PreferenceKey("icon_tuner_slot_camera", 0)
                val DIST_COMPUTE = PreferenceKey("icon_tuner_slot_dist_compute", 0)

                val ALARM_CLOCK = PreferenceKey("icon_tuner_slot_alarm_clock", 0)
                val ZEN = PreferenceKey("icon_tuner_slot_zen", 0)
                val VOLUME = PreferenceKey("icon_tuner_slot_volume", 0)
                val SECOND_SPACE = PreferenceKey("icon_tuner_slot_second_space", 0)
                val HIDE_PRIVACY = PreferenceKey("icon_tuner_hide_privacy", false)

                val COMPOUND_ICON = PreferenceKey("icon_tuner_slot_compound_icon", 0)
                val COMPOUND_ICON_ALARM = PreferenceKey("icon_tuner_compound_icon_alarm", false)
                val COMPOUND_ICON_ZEN = PreferenceKey("icon_tuner_compound_icon_zen", false)
                val COMPOUND_ICON_LOCATION = PreferenceKey("icon_tuner_compound_icon_location", false)
                val COMPOUND_ICON_VOLUME = PreferenceKey("icon_tuner_compound_icon_volume", false)
                val COMPOUND_PRIORITY = PreferenceKey("icon_tuner_compound_priority", Constants.COMPOUND_ICON_PRIORITY_STR)

                val LEFT_CONTAINER = PreferenceKey("icon_tuner_left_container", 0)
                val LEFT_EXT_BLOCK_LIST = PreferenceKey("icon_tuner_ext_blocked", "")
                val LEFT_COMPOUND_ICON = PreferenceKey("icon_tuner_left_compound_icon", false)
                val LEFT_LOCATION = PreferenceKey("icon_tuner_left_location", false)
                val LEFT_ALARM_CLOCK = PreferenceKey("icon_tuner_left_alarm_clock", false)
                val LEFT_ZEN = PreferenceKey("icon_tuner_left_zen", false)
                val LEFT_VOLUME = PreferenceKey("icon_tuner_left_volume", false)
            }

            object IconDetail {
                val HIDE_SIM_AUTO = PreferenceKey("icon_tuner_hide_sim_auto", false)
                val HIDE_SIM_ONE = PreferenceKey("icon_tuner_hide_sim_one", false)
                val HIDE_SIM_TWO = PreferenceKey("icon_tuner_hide_sim_two", false)
                val HIDE_CELLULAR_ACTIVITY = PreferenceKey("icon_tuner_hide_cellular_activity", false)
                val HIDE_CELLULAR_TYPE = PreferenceKey("icon_tuner_hide_cellular_type", false)
                val HIDE_CELLULAR_ROAM_GLOBAL = PreferenceKey("icon_tuner_cellular_hide_roam_global", false)
                val HIDE_CELLULAR_LARGE_ROAM = PreferenceKey("icon_tuner_cellular_hide_roam", false)
                val HIDE_CELLULAR_SMALL_ROAM = PreferenceKey("icon_tuner_cellular_hide_small_roam", false)
                val HIDE_CELLULAR_VO_WIFI = PreferenceKey("icon_tuner_cellular_hide_vo_wifi", false)
                val HIDE_CELLULAR_VOLTE = PreferenceKey("icon_tuner_cellular_hide_volte", false)
                val HIDE_CELLULAR_VOLTE_NO_SERVICE = PreferenceKey("icon_tuner_cellular_hide_volte_no_service", false)
                val HIDE_CELLULAR_SPEECH_HD = PreferenceKey("icon_tuner_cellular_hide_speech_hd", false)
                val USE_CELLULAR_TYPE_SINGLE = PreferenceKey("icon_tuner_cellular_type_single", false)
                val CELLULAR_TYPE_SINGLE_SWAP_INDEX = PreferenceKey("icon_tuner_cellular_type_single_swap", false)
                val CUSTOM_CELLULAR_TYPE_SINGLE_SIZE = PreferenceKey("icon_tuner_cellular_type_single_size", false)
                val CELLULAR_TYPE_SINGLE_SIZE_VAL = PreferenceKey("icon_tuner_cellular_type_single_size_val", 14.0f)
                val CUSTOM_CELLULAR_TYPE_LIST = PreferenceKey("icon_tuner_cellular_type_custom", false)
                val CELLULAR_TYPE_LIST_VAL = PreferenceKey("icon_tuner_cellular_type_custom_val", Constants.CELLULAR_TYPE_LIST)

                val HIDE_WIFI_STANDARD = PreferenceKey("icon_tuner_hide_wifi_type", false)
                val HIDE_WIFI_ACTIVITY = PreferenceKey("icon_tuner_hide_wifi_activity", false)
                val WIFI_ACTIVITY_RIGHT = PreferenceKey("icon_tuner_wifi_activity_right", false)
                val HIDE_WIFI_UNAVAILABLE = PreferenceKey("icon_tuner_hide_wifi_unavailable", false)

                val BATTERY_STYLE_BAR = PreferenceKey("icon_tuner_battery_style", 0)
                val BATTERY_STYLE_CC = PreferenceKey("icon_tuner_battery_style_cc", 0)
                val CUSTOM_BATTERY_PADDING_HORIZON = PreferenceKey("icon_tuner_battery_padding", false)
                val BATTERY_PADDING_START_VAL = PreferenceKey("icon_tuner_battery_padding_start_val", 0.0f)
                val BATTERY_PADDING_END_VAL = PreferenceKey("icon_tuner_battery_padding_end_val", 0.0f)
                val HIDE_BATTERY_CHARGE_OUT = PreferenceKey("icon_tuner_hide_charge_out", false)
                val BATTERY_PERCENT_MARK_STYLE = PreferenceKey("icon_tuner_battery_percent_mark_style", 0)
                val CUSTOM_BATTERY_PERCENT_OUT_SIZE = PreferenceKey("icon_tuner_change_battery_percent_out_size", false)
                val BATTERY_PERCENT_OUT_SIZE_VAL = PreferenceKey("icon_tuner_battery_percent_out_size_val", 12.5f)
                val CUSTOM_BATTERY_PERCENT_IN_SIZE = PreferenceKey("icon_tuner_battery_percent_in_size", false)
                val BATTERY_PERCENT_IN_SIZE_VAL = PreferenceKey("icon_tuner_battery_percent_in_size_val", 9.599976f)

                val NET_SPEED_MODE = PreferenceKey("icon_tuner_net_speed_mode", 0)
                val NET_SPEED_UNIT_MODE = PreferenceKey("icon_tuner_net_speed_unit_mode", 0)
                val NET_SPEED_REFRESH = PreferenceKey("icon_tuner_net_speed_refresh", false)
            }

            object StackedMobile {
                val ENABLED = PreferenceKey("icon_tuner_stacked_enabled", false)

                val STACKED_MOBILE_ICON = PreferenceKey("icon_tuner_slot_stacked_icon", 0)
                val STACKED_MOBILE_TYPE = PreferenceKey("icon_tuner_slot_stacked_type", 0)
                val SINGLE_MOBILE_SIM1 = PreferenceKey("icon_tuner_slot_single_sim1", 0)
                val SINGLE_MOBILE_SIM2 = PreferenceKey("icon_tuner_slot_single_sim2", 0)

                val SIGNAL_SVG_SINGLE = PreferenceKey("icon_tuner_stacked_icon_svg_single", 0)
                val SIGNAL_SVG_SINGLE_VAL = PreferenceKey("icon_tuner_stacked_icon_svg_single_val", Constants.STACKED_MOBILE_ICON_SINGLE_MIUI)
                val SIGNAL_SVG_SINGLE_NAME = PreferenceKey("icon_tuner_stacked_icon_svg_single_name", "")
                val SIGNAL_SVG_STACKED = PreferenceKey("icon_tuner_stacked_icon_svg_stacked", 0)
                val SIGNAL_SVG_STACKED_VAL = PreferenceKey("icon_tuner_stacked_icon_svg_stacked_val", Constants.STACKED_MOBILE_ICON_STACKED_MIUI)
                val SIGNAL_SVG_STACKED_NAME = PreferenceKey("icon_tuner_stacked_icon_svg_stacked_name", "")
                val SIGNAL_ALPHA_FG = PreferenceKey("icon_tuner_stacked_icon_alpha_fg", 1.0f)
                val SIGNAL_ALPHA_BG = PreferenceKey("icon_tuner_stacked_icon_alpha_bg", 0.4f)
                val SIGNAL_ALPHA_ERROR = PreferenceKey("icon_tuner_stacked_icon_alpha_error", 0.2f)

                val TYPE_FONT_MODE = PreferenceKey("sb_font_stacked_type_font", 0)
                val FONT_PATH_INTERNAL = PreferenceKey("sb_font_stacked_type_path_app", Constants.VARIABLE_FONT_DEFAULT_PATH)
                val FONT_PATH_ORIGINAL = PreferenceKey("sb_font_stacked_type_path_real", Constants.VARIABLE_FONT_DEFAULT_PATH)
                val TYPE_WIDTH_CONDENSED = PreferenceKey("sb_font_stacked_type_width_condensed", 80)

                val LARGE_TYPE_HIDE_WHEN_DISCONNECT = PreferenceKey("icon_tuner_stacked_type_hide_disconnect", false)
                val LARGE_TYPE_HIDE_WHEN_WIFI = PreferenceKey("icon_tuner_stacked_type_hide_wifi", false)
                val LARGE_TYPE_SIZE = PreferenceKey("icon_tuner_stacked_type_size_val", 14.0f)
                val LARGE_TYPE_FONT_WEIGHT = PreferenceKey("sb_font_stacked_type_single_weight", 400)
                val LARGE_TYPE_PADDING_START_VAL = PreferenceKey("icon_tuner_stacked_type_padding_left_val", 2.0f)
                val LARGE_TYPE_PADDING_END_VAL = PreferenceKey("icon_tuner_stacked_type_padding_right_val", 2.0f)
                val LARGE_TYPE_VERTICAL_OFFSET = PreferenceKey("icon_tuner_stacked_type_v_offset", 0.0f)

                val SMALL_TYPE_SHOW_ON_STACKED = PreferenceKey("stacked_s_type_show_stacked", false)
                val SMALL_TYPE_SHOW_ON_SINGLE = PreferenceKey("stacked_s_type_show_single", false)
                val SMALL_TYPE_SHOW_ROAMING = PreferenceKey("stacked_s_type_roaming", false)
                val SMALL_TYPE_SIZE = PreferenceKey("stacked_s_type_size", 7.159973f)
                val SMALL_TYPE_FONT_WEIGHT = PreferenceKey("stacked_s_type_weight_val", 630)
            }
        }

        object MediaControl {
            object Shared {
                val BG_STYLE = DualPreferenceKey(
                    notif = PreferenceKey("media_ctrl_background", 0),
                    island = PreferenceKey("di_media_ctrl_background", 0)
                )
                val BG_BLUR_RADIUS = DualPreferenceKey(
                    notif = PreferenceKey("media_ctrl_radius", 10),
                    island = PreferenceKey("di_media_ctrl_radius", 10)
                )
                val BG_ALLOW_REVERSE = DualPreferenceKey(
                    notif = PreferenceKey("media_allow_reverse", false),
                    island = PreferenceKey("di_media_allow_reverse", false)
                )
                val BG_AMBIENT_LIGHT_OPT = DualPreferenceKey(
                    notif = PreferenceKey("media_ambient_light_opt", false),
                    island = PreferenceKey("di_media_ambient_light_opt", false)
                )
                val BG_COLOR_ANIM = DualPreferenceKey(
                    notif = PreferenceKey("media_ctrl_anim", true),
                    island = PreferenceKey("di_media_ctrl_anim", true)
                )

                val LYT_ALBUM = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_album", 0),
                    island = PreferenceKey("di_media_lyt_album", 0)
                )
                val LYT_UNLOCK_ACTION = PreferenceKey("media_unlock_action", false)
                val LYT_LEFT_ACTIONS = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_left_actions", false),
                    island = PreferenceKey("di_media_lyt_left_actions", false)
                )
                val LYT_ACTIONS_ORDER = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_actions_order", 0),
                    island = PreferenceKey("di_media_lyt_actions_order", 0)
                )
                val LYT_HIDE_TIME = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_hide_time", false),
                    island = PreferenceKey("di_media_lyt_hide_time", false)
                )
                val LYT_HIDE_SEAMLESS = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_hide_seamless", false),
                    island = PreferenceKey("di_media_lyt_hide_seamless", false)
                )
                val LYT_HEADER_TOP_MARGIN = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_header_margin", 21.0f),
                    island = PreferenceKey("di_media_lyt_header_margin", 21.0f)
                )
                val LYT_HEADER_PADDING = DualPreferenceKey(
                    notif = PreferenceKey("media_lyt_header_padding", 4.0f),
                    island = PreferenceKey("di_media_lyt_header_padding", 4.0f)
                )

                val ELM_ALBUM_FLIP = PreferenceKey("media_elm_flip", true)
                val ELM_CUSTOM_TEXT_SIZE = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_text_size", false),
                    island = PreferenceKey("di_media_elm_text_size", false)
                )
                val ELM_TITLE_SIZE = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_title_size", 18.0f),
                    island = PreferenceKey("di_media_elm_title_size", 18.0f)
                )
                val ELM_ARTIST_SIZE = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_artist_size", 12.0f),
                    island = PreferenceKey("di_media_elm_artist_size", 12.0f)
                )
                val ELM_TIME_SIZE = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_time_size", 12.0f),
                    island = PreferenceKey("di_media_elm_time_size", 12.0f)
                )
                val ELM_PROGRESS_STYLE = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_prog_style", 0),
                    island = PreferenceKey("di_media_elm_prog_style", 0)
                )
                val ELM_PROGRESS_WIDTH = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_prog_width", 6.0f),
                    island = PreferenceKey("di_media_elm_prog_width", 6.0f)
                )
                val ELM_PROGRESS_COMET = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_prog_comet", false),
                    island = PreferenceKey("di_media_elm_prog_comet", false)
                )
                val ELM_THUMB_STYLE = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_thumb_style", 0),
                    island = PreferenceKey("di_media_elm_thumb_style", 0)
                )
                val ELM_PROGRESS_ROUND = DualPreferenceKey(
                    notif = PreferenceKey("media_elm_prog_round", false),
                    island = PreferenceKey("di_media_elm_prog_round", false)
                )
            }

            object NotifCenter {
                val BG_AMBIENT_LIGHT = PreferenceKey("media_ambient_light", false)
                val BG_ALWAYS_DARK = PreferenceKey("media_always_dark", false)

                val ELM_ALBUM_SHADOW = PreferenceKey("media_elm_shadow", true)
            }

            object DynamicIsland {
                val BG_AMBIENT_LIGHT_TYPE = PreferenceKey("di_media_ambient_light_type", 0)
            }
        }
    }

    object Taplus {
        val SEARCH_USE_BROWSER = PreferenceKey("taplus_use_browser", false)
        val SEARCH_ENGINE = PreferenceKey("taplus_search_engine", 0)
        val CUSTOM_SEARCH_URL = PreferenceKey("taplus_search_url", "")
    }

    object Themes {
        val SKIP_SPLASH = PreferenceKey("themes_skip_splash", false)
    }

    object Updater {
        val BLOCK_AUTO_UPDATE_DIALOG = PreferenceKey("updater_block_dialog", false)
        val DISABLE_VALIDATION = PreferenceKey("updater_no_validation", false)
    }
}