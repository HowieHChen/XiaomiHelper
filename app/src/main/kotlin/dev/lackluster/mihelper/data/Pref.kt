package dev.lackluster.mihelper.data

object Pref {
    const val VERSION = 5
    object Key {
        object App {
            const val SPLIT_VIEW = "app_disable_split"
            const val HAZE_BLUR = "app_haze_blur"
            const val HAZE_TINT_ALPHA_LIGHT = "app_haze_tint_alpha_light"
            const val HAZE_TINT_ALPHA_DARK = "app_haze_tint_alpha_dark"
            const val SKIP_ROOT_CHECK = "app_ignore_root"
        }
        object Module {
            const val ENABLED = "enable_module"
            const val DEX_KIT_CACHE = "dexkit_cache"
            const val HIDE_ICON = "hide_icon"
            const val SHOW_IN_SETTINGS = "entry_in_settings"
            const val SETTINGS_ICON_STYLE = "entry_icon_style"
            const val SETTINGS_ICON_COLOR = "entry_icon_color"
            const val SETTINGS_NAME = "entry_name"
            const val SETTINGS_NAME_CUSTOM = "entry_name_custom"
            const val SP_VERSION = "sp_version"
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
            const val FONT_SCALE = "android_font_scale"
            const val FONT_SCALE_VAL = "android_font_scale_val"
        }
        object Browser {
            const val DEBUG_MODE = "browser_debug_mode"
            const val SWITCH_ENV = "browser_switch_env"
            const val BLOCK_UPDATE = "browser_no_update"
            const val SKIP_SPLASH = "browser_skip_splash"
            const val REMOVE_APP_REC = "browser_remove_app_rec"
            const val HIDE_HOMEPAGE_TOP_BAR = "browser_hide_home_top_bar"
            const val HIDE_AI_SEARCH_ENTRY = "browser_hide_ai_search"
        }
        object Download {
            const val FUCK_XL = "download_remove_xl"
        }
        object DownloadUI {
            const val HIDE_XL = "downloadui_remove_xl"
        }
        object GuardProvider {
            const val BLOCK_UPLOAD_APP = "guard_forbid_upload_app"
            const val BLOCK_ENV_CHECK = "guard_block_env_check"
        }
        object InCallUI {
            const val HIDE_CRBT = "incallui_hide_crbt"
        }
        object LBE {
            const val BLOCK_REMOVE_AUTO_STARTUP = "lbe_block_rec_auto_startup"
            const val CLIPBOARD_TOAST = "lbe_clipboard_toast"
        }
        object Market {
            const val AD_BLOCKER = "market_ad_block"
            const val SKIP_SPLASH = "market_skip_splash"
            const val TAB_BLUR = "market_tab_blur"
            const val FILTER_TAB = "market_filter_tab"
            const val FILTER_TAB_IGNORE_RESTRICT = "market_tab_ignore_restrict"
            const val HIDE_TAB_HOME = "market_hide_tab_home"
            const val HIDE_TAB_GAME = "market_hide_tab_game"
            const val HIDE_TAB_RANK = "market_hide_tab_rank"
            const val HIDE_TAB_AGENT = "market_hide_tab_agent"
            const val HIDE_TAB_APP_ASSEMBLE = "market_hide_tab_assemble"
            const val HIDE_TAB_MINI_GAME = "market_hide_tab_mini_game"
            const val HIDE_TAB_MINE = "market_hide_tab_mine"
            const val HIDE_TAB_OTHERS = "market_hide_tab_others"
            const val BLOCK_UPDATE_DIALOG = "market_block_up_dialog"
            const val HIDE_APP_SECURITY = "market_hide_app_security"
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
            const val ENHANCE_CONTINUE_TASKS = "mismarthub_enhance_continue"
        }
        object MiTrust {
            const val DISABLE_RISK_CHECK = "mitrust_skip_risk_check"
        }
        object MiuiHome {
            const val REMOVE_REPORT = "home_remove_report"
            const val DOUBLE_TAP_TO_SLEEP = "home_double_tap_sleep"
            const val BACK_HAPTIC = "home_back_haptic"
            const val QUICK_SWITCH = "home_quick_back"
            const val QUICK_SWITCH_LEFT = "home_quick_back_left"
            const val QUICK_SWITCH_RIGHT = "home_quick_back_right"
            const val LINE_GESTURE_DOUBLE_TAP = "home_line_double_tap"
            const val LINE_GESTURE_LONG_PRESS = "home_line_long_press"
            const val ANIM_ICON_ZOOM = "home_anim_icon_zoom"
            const val ANIM_ICON_DARKEN = " home_anim_icon_darken"
            const val ANIM_FOLDER_ZOOM = "home_anim_folder_zoom"
            const val ANIM_FOLDER_ICON_DARKEN = "home_anim_folder_icon_darken"
            const val FOLDER_ADAPT_SIZE ="home_folder_aapt_size"
            const val PAD_RECENT_SHOW_MEMORY = "home_recent_pad_memory"
            const val PAD_RECENT_HIDE_WORLD = "home_recent_pad_world"
            const val RECENT_SHOW_REAL_MEMORY = "home_recent_real_memory"
            const val RECENT_CARD_ANIM = "home_recent_anim"
            const val RECENT_HIDE_CLEAR_BUTTON = "home_recent_hide_clear_button"
            const val RECENT_MEM_INFO_CLEAR = "home_recent_mem_info_clear"
            const val RECENT_DISABLE_FAKE_NAVBAR = "home_disable_fake_navbar"
            const val MINUS_RESTORE_SETTING = "home_minus_restore"
            const val FORCE_COLOR_STATUS_BAR = "home_force_color_status_bar"
            const val FORCE_COLOR_TEXT_ICON = "home_force_color_text_icon"
            const val FORCE_COLOR_MINUS = "home_force_color_minus"
            const val DOCK_REMOVE_NUM_LIMIT = "home_dock_remove_num_limit"
        }
        object MMS {
            const val AD_BLOCKER = "mms_ad_block"
        }
        object Music {
            const val AD_BLOCKER = "music_ad_block"
            const val SKIP_SPLASH = "music_skip_splash"
            const val HIDE_KARAOKE = "music_hide_karaoke"
            const val HIDE_LONG_AUDIO = "music_hide_long_audio"
            const val HIDE_DISCOVER = "music_hide_discover"
            const val MY_HIDE_BANNER = "music_my_hide_banner"
            const val MY_HIDE_REC_PLAYLIST = "music_my_hide_rec"
            const val HIDE_FAV_NUM = "music_hide_fav_num"
        }
        object PackageInstaller {
            const val BLOCK_UPLOAD_INFO = "package_block_upload"
            const val REMOVE_ELEMENT = "package_ad_block"
            const val DISABLE_COUNT_CHECK = "package_count_check"
            const val DISABLE_RISK_CHECK = "package_skip_risk_check"
            const val INSTALL_SOURCE = "package_install_source"
            const val SOURCE_PKG_NAME = "package_source_pkg"
            const val DISGUISE_NO_NETWORK = "package_no_network"
        }
        object PowerKeeper {
            const val DO_NOT_KILL_APP = "power_donot_kill_app"
            const val BLOCK_BATTERY_WHITELIST = "power_battery_whitelist"
            const val GMS_BG_RUNNING = "power_gms_bg_running"
            const val UNLOCK_CUSTOM_REFRESH = "power_custom_refresh"
        }
        object RemoteController {
            const val AD_BLOCKER = "remote_ad_block"
        }
        object Search {
            const val MORE_SEARCH_ENGINE = "search_more_engine"
            const val CUSTOM_SEARCH_ENGINE = "search_custom_engine"
            const val CUSTOM_SEARCH_ENGINE_ENTITY = "search_custom_engine_entity"
        }
        object SecurityCenter {
            const val SKIP_SPLASH = "security_skip_splash"
            const val LOCK_SCORE = "security_lock_score"
            const val HIDE_RED_DOT = "security_hide_red_dot"
            const val HIDE_HOME_REC = "security_hide_home_rec"
            const val HIDE_HOME_COMMON = "security_hide_home_common"
            const val HIDE_HOME_POPULAR = "security_hide_home_popular"
            const val DISABLE_RISK_APP_NOTIF = "security_no_risk_notification"
            const val REMOVE_REPORT = "security_remove_report"
            const val SKIP_WARNING = "security_skip_warn"
            const val LINK_START = "security_link_start"
            const val SHOW_SCREEN_BATTERY = "security_screen_battery"
            const val SHOW_SYSTEM_BATTERY = "security_system_battery"
            const val DISABLE_BUBBLE_RESTRICT = "security_bubble_restriction"
            const val CTRL_SYSTEM_APP_WIFI = "security_system_app_wifi"
            const val CLICK_ICON_TO_OPEN = "security_click_icon_open"
        }
        object Settings {
            const val SHOE_GOOGLE = "settings_show_google"
            const val UNLOCK_TAPLUS_FOR_PAD = "taplus_unlock_pad"
            const val QUICK_PER_OVERLAY = "settings_quick_per_overlay"
            const val QUICK_PER_INSTALL_SOURCE = "settings_quick_per_install"
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
                const val CLOCK_FIXED_WIDTH = "statusbar_clock_fixed_width"
                const val CLOCK_TNUM = "statusbar_clock_tnum"
                const val NOTIFICATION_COUNT = "statusbar_notif_max"
                const val NOTIFICATION_COUNT_ICON = "statusbar_notif_icon_max"
                const val DOUBLE_TAP_TO_SLEEP = "statusbar_double_tap_sleep"
                const val DISABLE_SMART_DARK = "systemui_disable_smart_dark"
            }
            object FontWeight {
                const val FONT_PATH = "sb_font_path"
                const val CLOCK = "sb_font_clock"
                const val CLOCK_WEIGHT = "sb_font_clock_weight"
                const val CLOCK_NOTIFICATION = "sb_font_clock_notif"
                const val CLOCK_NOTIFICATION_WEIGHT = "sb_font_clock_notif_weight"
                const val FOCUS_NOTIFICATION = "sb_font_focus"
                const val FOCUS_NOTIFICATION_WEIGHT = "sb_font_focus_weight"
                const val CARRIER = "sb_font_carrier"
                const val CARRIER_WEIGHT = "sb_font_carrier_weight"
                const val NET_SPEED_NUMBER = "sb_font_speed_num"
                const val NET_SPEED_NUMBER_WEIGHT = "sb_font_speed_num_weight"
                const val NET_SPEED_UNIT = "sb_font_speed_unit"
                const val NET_SPEED_UNIT_WEIGHT = "sb_font_speed_unit_weight"
                const val MOBILE_TYPE = "sb_font_mobile_type"
                const val MOBILE_TYPE_WEIGHT = "sb_font_mobile_type_weight"
                const val BATTERY_PERCENTAGE_IN = "sb_font_battery_pct_in"
                const val BATTERY_PERCENTAGE_IN_WEIGHT = "sb_font_bat_pct_in_weight"
                const val BATTERY_PERCENTAGE_OUT = "sb_font_bat_pct_out"
                const val BATTERY_PERCENTAGE_OUT_WEIGHT = "sb_font_bat_pct_out_weight"
                const val BATTERY_PERCENTAGE_MARK = "sb_font_bat_pct_mark"
                const val BATTERY_PERCENTAGE_MARK_WEIGHT = "sb_font_bat_pct_mark_weight"
            }
            object IconTurner {
                const val IGNORE_SYS_HIDE = "statusbar_ignore_sys_hide"
                const val MOBILE = "statusbar_hide_mobile"
                const val HIDE_SIM_ONE = "statusbar_hide_sim_one"
                const val HIDE_SIM_TWO = "statusbar_hide_sim_two"
                const val NO_SIM = "statusbar_hide_no_sim"
                const val HIDE_MOBILE_ACTIVITY = "statusbar_hide_mobile_activity"
                const val HIDE_MOBILE_TYPE = "statusbar_hide_mobile_type"
                const val HD_NEW = "statusbar_hide_hd_new"
                const val HIDE_HD_SMALL = "statusbar_hide_hd_small"
                @Deprecated("Use HD_NEW")
                const val HIDE_HD_LARGE = "statusbar_hide_hd_large"
                @Deprecated("")
                const val HIDE_HD_NO_SERVICE = "statusbar_hide_hd_no_service"
                const val HIDE_ROAM = "statusbar_hide_roam"
                const val HIDE_ROAM_SMALL = "statusbar_hide_roam_small"
                const val HIDE_VOLTE = "statusbar_hide_volte"
                const val HIDE_VOWIFI = "statusbar_hide_vowifi"
                const val WIFI = "statusbar_hide_wifi"
                const val HIDE_WIFI_ACTIVITY = "statusbar_hide_wifi_activity"
                const val HIDE_WIFI_STANDARD = "statusbar_hide_wifi_type"
                const val HOTSPOT = "statusbar_hide_hotspot"
                const val BATTERY_STYLE = "statusbar_battery_style"
                const val BATTERY_PERCENTAGE_SYMBOL_STYLE = "statusbar_battery_percent_symbol_style"
                const val HIDE_CHARGE = "statusbar_hide_charge"
                const val BATTERY_MODIFY_PERCENTAGE_TEXT_SIZE = "statusbar_change_battery_percent_size"
                const val BATTERY_PERCENTAGE_TEXT_SIZE = "statusbar_battery_percent_size"
                const val BATTERY_PERCENTAGE_TNUM = "statusbar_battery_percent_tnum"
                const val SWAP_BATTERY_PERCENT = "statusbar_swap_battery_percent"
                const val BATTERY_MODIFY_PADDING = "statusbar_battery_custom"
                const val BATTERY_PADDING_LEFT = "statusbar_battery_custom_padding_left"
                const val BATTERY_PADDING_RIGHT = "statusbar_battery_custom_padding_right"
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
                const val GLASSES = "statusbar_hide_glasses"
                const val CAMERA = "statusbar_hide_camera"
                const val SWAP_MOBILE_WIFI = "statusbar_swap_mobile_wifi"
                const val ALARM = "statusbar_hide_alarm"
                const val HEADSET = "statusbar_hide_headset"
                const val VOLUME = "statusbar_hide_volume"
                const val ZEN = "statusbar_hide_zen"
                const val HIDE_PRIVACY = "statusbar_hide_privacy"
            }
            object LockScreen {
                const val DOUBLE_TAP_TO_SLEEP = "systemui_double_tap_sleep"
                const val HIDE_DISTURB = "systemui_lockscreen_hide_disturb"
                const val CARRIER_TEXT = "systemui_lockscreen_carrier_text"
                const val KEEP_NOTIFICATION = "systemui_lockscreen_keep_notif"
            }
            object NotifCenter {
                const val NOTIF_NO_WHITELIST = "systemui_notif_no_whitelist"
                const val NOTIF_FREEFORM = "systemui_notif_freeform"
                const val CLOCK_PAD_ANIM = "statusbar_clock_pad_anim"
                const val MIUIX_EXPAND_BUTTON = "systemui_notif_miuix_expand"
                const val MONET_OVERLAY = "systemui_notif_monet_overlay"
                const val MONET_OVERLAY_COLOR = "systemui_notif_monet_color"
                const val EXPAND_NOTIFICATION = "systemui_notif_expand_notifs"
            }
            object MediaControl {
                const val UNLOCK_ACTION = "media_unlock_action"

                const val BACKGROUND_STYLE = "media_ctrl_background"
                const val BLUR_RADIUS = "media_ctrl_radius"
                const val ALLOW_REVERSE = "media_allow_reverse"

                const val LYT_ALBUM = "media_lyt_album"
                const val LYT_LEFT_ACTIONS = "media_lyt_left_actions"
                const val LYT_ACTIONS_ORDER = "media_lyt_actions_order"
                const val LYT_HIDE_TIME = "media_lyt_hide_time"
                const val LYT_HIDE_SEAMLESS = "media_lyt_hide_seamless"
                const val LYT_HEADER_MARGIN = "media_lyt_header_margin"
                const val LYT_HEADER_PADDING = "media_lyt_header_padding"

                const val ELM_TEXT_SIZE = "media_elm_text_size"
                const val ELM_TITLE_SIZE = "media_elm_title_size"
                const val ELM_ARTIST_SIZE = "media_elm_artist_size"
                const val ELM_TIME_SIZE = "media_elm_time_size"
                const val ELM_ACTIONS_RESIZE = "media_elm_actions_resize"
                const val ELM_THUMB_STYLE = "media_elm_thumb_style"
                const val ELM_PROGRESS_STYLE = "media_elm_prog_style"
                const val ELM_PROGRESS_WIDTH = "media_elm_prog_width"

                const val FIX_THUMB_CROPPED = "media_thumb_crop"
                const val USE_ANIM = "media_ctrl_anim"
            }
            object ControlCenter {
                const val HIDE_CARRIER_ONE = "statusbar_hide_carrier_one"
                const val HIDE_CARRIER_TWO = "statusbar_hide_carrier_two"
                const val HIDE_CARRIER_HD = "statusbar_hide_carrier_hd"
                const val BATTERY_PERCENTAGE = "statusbar_cc_battery_percent"
                const val BATTERY_PERCENTAGE_ANIM = "statusbar_cc_battery_percent_anim"
            }
            object Plugin {
                const val AUTO_FLASH_ON = "sys_plugin_flash_on"
            }
            const val FUCK_GESTURES_DAT = "systemui_fuck_gesture_dat"
        }
        object Taplus {
            const val HIDE_SHOP = "taplus_hide_shop"
            const val SEARCH_USE_BROWSER = "taplus_use_browser"
            const val SEARCH_ENGINE = "taplus_search_engine"
            const val SEARCH_URL = "taplus_search_url"
        }
        object Themes {
            const val SKIP_SPLASH = "themes_skip_splash"
        }
        object Hints {
            const val MEDIA_ADVANCED_TEXTURES = "hint_media_adv_textures"
        }
        object Updater {
            const val DISABLE_VALIDATION = "updater_no_validation"
        }
        object Weather {
            const val CARD_COLOR = "weather_card_color"
        }
    }
    @Suppress("unused")
    object OldKey {
        object Android {
            const val ROTATE_SUGGEST = "android_rotate_suggest"
        }
        object Gallery {
            const val PATH_OPTIM = "gallery_path_optim"
            const val UNLIMITED_CROP = "screenshot_unlimited_crop"
        }
        object Joyose {
            const val BLOCK_CLOUD_CONTROL = "joyose_no_cloud_control"
        }
        object MiShare {
            const val ALWAYS_ON = "mishare_no_auto_off"
        }
        object MiuiHome {
            object Refactor {
                const val EXTRA_COMPATIBILITY = "home_refactor_extra_compatibility"
                const val SYNC_WALLPAPER_SCALE = "home_refactor_wallpaper_scale_sync"
                const val EXTRA_FIX = "home_refactor_extra_fix"
                const val FIX_SMALL_WINDOW_ANIM = "home_refactor_small_window"
                const val ALL_APPS_BLUR_BG = "home_refactor_allapps_blur_bg"
                const val MINUS_MODE = "home_refactor_minus_mode"
                const val SHOW_LAUNCH_IN_RECENTS = "home_refactor_launch_show"
                const val SHOW_LAUNCH_IN_RECENTS_SCALE = "home_refactor_launch_scale"
                const val SHOW_LAUNCH_IN_FOLDER = "home_refactor_launch_folder"
                const val SHOW_LAUNCH_IN_FOLDER_SCALE = "home_refactor_launch_folder_scale"
                const val SHOW_LAUNCH_IN_MINUS_SCALE = "home_refactor_minus_launch_scale"
                const val APPS_BLUR = "home_refactor_apps_blur"
                const val APPS_BLUR_RADIUS_STR = "home_refactor_apps_radius_str"
                const val APPS_DIM = "home_refactor_apps_dim"
                const val APPS_DIM_MAX = "home_refactor_apps_dim_max"
                const val APPS_NONLINEAR_TYPE = "home_refactor_apps_nonlinear_type"
                const val APPS_NONLINEAR_DECE_FACTOR = "home_refactor_apps_nonlinear_dece_factor"
                const val APPS_NONLINEAR_PATH_X1 = "home_refactor_apps_nonlinear_path_x1"
                const val APPS_NONLINEAR_PATH_Y1 = "home_refactor_apps_nonlinear_path_y1"
                const val APPS_NONLINEAR_PATH_X2 = "home_refactor_apps_nonlinear_path_x2"
                const val APPS_NONLINEAR_PATH_Y2 = "home_refactor_apps_nonlinear_path_y2"
                const val FOLDER_BLUR = "home_refactor_folder_blur"
                const val FOLDER_BLUR_RADIUS_STR = "home_refactor_folder_radius_str"
                const val FOLDER_DIM = "home_refactor_folder_dim"
                const val FOLDER_DIM_MAX = "home_refactor_folder_dim_max"
                const val FOLDER_NONLINEAR_TYPE = "home_refactor_folder_nonlinear_type"
                const val FOLDER_NONLINEAR_DECE_FACTOR = "home_refactor_folder_nonlinear_dece_factor"
                const val FOLDER_NONLINEAR_PATH_X1 = "home_refactor_folder_nonlinear_path_x1"
                const val FOLDER_NONLINEAR_PATH_Y1 = "home_refactor_folder_nonlinear_path_y1"
                const val FOLDER_NONLINEAR_PATH_X2 = "home_refactor_folder_nonlinear_path_x2"
                const val FOLDER_NONLINEAR_PATH_Y2 = "home_refactor_folder_nonlinear_path_y2"
                const val WALLPAPER_BLUR = "home_refactor_wall_blur"
                const val WALLPAPER_BLUR_RADIUS_STR = "home_refactor_wall_radius_dp"
                const val WALLPAPER_DIM = "home_refactor_wall_dim"
                const val WALLPAPER_DIM_MAX = "home_refactor_wall_dim_max"
                const val WALLPAPER_NONLINEAR_TYPE = "home_refactor_wall_nonlinear_type"
                const val WALLPAPER_NONLINEAR_DECE_FACTOR = "home_refactor_wall_nonlinear_dece_factor"
                const val WALLPAPER_NONLINEAR_PATH_X1 = "home_refactor_wall_nonlinear_path_x1"
                const val WALLPAPER_NONLINEAR_PATH_Y1 = "home_refactor_wall_nonlinear_path_y1"
                const val WALLPAPER_NONLINEAR_PATH_X2 = "home_refactor_wall_nonlinear_path_x2"
                const val WALLPAPER_NONLINEAR_PATH_Y2 = "home_refactor_wall_nonlinear_path_y2"
                const val MINUS_BLUR = "home_refactor_minus_blur"
                const val MINUS_BLUR_RADIUS_STR = "home_refactor_minus_radius_str"
                const val MINUS_DIM = "home_refactor_minus_dim"
                const val MINUS_DIM_MAX = "home_refactor_minus_dim_max"
                const val MINUS_OVERLAP = "home_refactor_minus_overlap"
                const val SHOW_LAUNCH_IN_MINUS = "home_refactor_minus_launch"
                const val APPS_BLUR_RADIUS = "home_refactor_apps_blur_radius"
                const val WALLPAPER_BLUR_RADIUS = "home_refactor_wall_radius"
                const val MINUS_BLUR_RADIUS = "home_refactor_minus_radius"
            }
            const val MINUS_BLUR = "person_assist_blur"
            const val REFACTOR = "home_refactor"
            const val ANIM_UNLOCK = "home_anim_unlock"
            const val ANIM_WALLPAPER_ZOOM_SYNC = "home_wallpaper_sync"
            const val FOLDER_NO_PADDING = "home_folder_no_padding"
            const val FOLDER_COLUMNS = "home_folder_columns"
            const val FOLDER_BLUR = "home_blur_enhance"
            const val ICON_UNBLOCK_GOOGLE = "home_icon_unblock_google"
            const val ICON_CORNER4LARGE = "home_icon_corner4large"
            const val ICON_PERFECT = "home_icon_perfect_icon"
            const val RECENT_WALLPAPER_DARKEN = "home_recent_wallpaper"
            const val PAD_DOCK_TIME_DURATION = "home_pad_dock_time_duration"
            const val PAD_DOCK_SAFE_AREA_HEIGHT = "home_pad_dock_safe_height"
            const val WIDGET_ANIM = "home_widget_launch_anim"
            const val WIDGET_RESIZABLE = "home_widget_resizable"
            const val SHORTCUT_FREEFORM = "home_shortcut_freeform"
            const val SHORTCUT_INSTANCE = "home_shortcut_instance"
            const val MINUS_FOLD_STYLE = "home_minus_fold"
            const val MINUS_BLUR_TYPE = "person_assist_blur_type"
            const val ALWAYS_SHOW_TIME = "home_always_show_time"
            const val FAKE_PREMIUM = "home_fake_premium"
        }
        object Module {
            const val LITE_MODE = "lite_mode"
        }
        object ScreenRecorder {
            const val SAVE_TO_MOVIES = "screen_recorder_to_movies"
        }
        object Screenshot {
            const val SAVE_AS_PNG = "screenshot_save_png"
            const val SAVE_TO_PICTURE = "screenshot_to_picture"
        }
        object Settings {
            const val UNLOCK_VOIP_ASSISTANT = "settings_unlock_voip_assistant"
            const val UNLOCK_CUSTOM_REFRESH = "power_custom_refresh"
            const val UNLOCK_NET_MODE_SETTINGS = "phone_net_mode_settings"
        }
        object SystemUI {
            object IconTurner {
                const val HIDE_BATTERY_PERCENT_SYMBOL = "statusbar_hide_battery_percent"
                const val CHANGE_BATTERY_PERCENT_SYMBOL = "statusbar_change_battery_percent_mark"
                const val BATTERY_PADDING_LEFT = "statusbar_battery_padding_left"
                const val BATTERY_PADDING_RIGHT = "statusbar_battery_padding_right"
            }
            object MediaControl {
                const val SQUIGGLY_PROGRESS = "media_ctrl_squiggly_progress"
                const val HIDE_APP_ICON = "media_ctrl_hide_app"
            }
            object NotifCenter {
                const val CLOCK_COLOR_FIX = "statusbar_clock_color_fix"
            }
        }
        object PackageInstaller {
            const val UPDATE_SYSTEM_APP = "package_update_system_app"
            const val MORE_INFO = "package_more_info"
        }
        object SecurityCenter {
            const val SKIP_WARNING = "security_skip_warn"
            const val SKIP_OPEN_APP = "security_skip_open_app"
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