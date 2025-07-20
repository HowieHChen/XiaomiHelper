package dev.lackluster.mihelper.ui.page

import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@Composable
fun OthersPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    var spMiAiBrowserSearch by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.MiAi.SEARCH_USE_BROWSER)) }
    var visibilityMiAiCustomEntryName by remember { mutableStateOf(SafeSP.getInt(Pref.Key.MiAi.SEARCH_ENGINE) == 5) }
    var spTaplusBrowserSearch by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Taplus.SEARCH_USE_BROWSER)) }
    var visibilityTaplusCustomEntryName by remember { mutableStateOf(SafeSP.getInt(Pref.Key.Taplus.SEARCH_ENGINE) == 5) }
    var spContinueAllTasks by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.MiMirror.CONTINUE_ALL_TASKS)) }
    var spSearchMoreEngines by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Search.MORE_SEARCH_ENGINE)) }

    val dropdownEntriesSearchEngine = listOf(
        DropDownEntry(stringResource(R.string.search_engine_default)),
        DropDownEntry(stringResource(R.string.search_engine_baidu)),
        DropDownEntry(stringResource(R.string.search_engine_sogou)),
        DropDownEntry(stringResource(R.string.search_engine_bing)),
        DropDownEntry(stringResource(R.string.search_engine_google)),
        DropDownEntry(stringResource(R.string.search_engine_custom))
    )
    val dropDownEntriesWeatherCard = listOf(
        DropDownEntry(stringResource(R.string.weather_card_color_default)),
        DropDownEntry(stringResource(R.string.weather_card_color_light)),
        DropDownEntry(stringResource(R.string.weather_card_color_dark)),
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_others),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_browser),
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_browser_debug_mode),
                    summary = stringResource(R.string.others_browser_debug_mode_tips),
                    key = Pref.Key.Browser.DEBUG_MODE
                )
                SwitchPreference(
                    title = stringResource(R.string.others_browser_switch_env),
                    summary = stringResource(R.string.others_browser_switch_env_tips),
                    key = Pref.Key.Browser.SWITCH_ENV
                )
                SwitchPreference(
                    title = stringResource(R.string.others_browser_disable_update),
                    key = Pref.Key.Browser.BLOCK_UPDATE
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_miai)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_miai_hide_watermark),
                    key = Pref.Key.MiAi.HIDE_WATERMARK
                )
                SwitchPreference(
                    title = stringResource(R.string.search_use_browser),
                    key = Pref.Key.MiAi.SEARCH_USE_BROWSER
                ) {
                    spMiAiBrowserSearch = it
                }
                AnimatedVisibility(
                    spMiAiBrowserSearch
                ) {
                    Column {
                        DropDownPreference(
                            title = stringResource(R.string.search_engine),
                            entries = dropdownEntriesSearchEngine,
                            key = Pref.Key.MiAi.SEARCH_ENGINE
                        ) {
                            visibilityMiAiCustomEntryName = (it == 5)
                        }
                        AnimatedVisibility(
                            visibilityMiAiCustomEntryName
                        ) {
                            EditTextPreference(
                                title = stringResource(R.string.search_engine_custom_url),
                                key = Pref.Key.MiAi.SEARCH_URL,
                                dataType = EditTextDataType.STRING,
                                dialogMessage = stringResource(R.string.search_engine_custom_url_toast) + "\nhttps://example.com/s?q=%s",
                                isValueValid = { value ->
                                    val string = value as String
                                    string.isBlank() || string.contains("%s")
                                },
                                valuePosition = ValuePosition.SUMMARY_VIEW
                            )
                        }
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_mimirror)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_mimirror_all_app),
                    key = Pref.Key.MiMirror.CONTINUE_ALL_TASKS
                ) {
                    spContinueAllTasks = it
                }
                AnimatedVisibility(
                    visible = spContinueAllTasks
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.others_mimirror_enhance_continue),
                        summary = stringResource(R.string.others_mimirror_enhance_continue_tips),
                        key = Pref.Key.MiMirror.ENHANCE_CONTINUE_TASKS
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_search)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_search_more_search_engines),
                    summary = stringResource(R.string.others_search_more_search_engines_tips),
                    key = Pref.Key.Search.MORE_SEARCH_ENGINE
                ) {
                    spSearchMoreEngines = it
                }
                AnimatedVisibility(
                    spSearchMoreEngines
                ) {
                    Column {
                        TextPreference(
                            title = stringResource(R.string.others_search_custom_search_engine),
                            value = stringResource(
                                if (SafeSP.getBoolean(Pref.Key.Search.CUSTOM_SEARCH_ENGINE)) {
                                    R.string.common_on
                                } else {
                                    R.string.common_off
                                }
                            )
                        ) {
                            navController.navigateTo(Pages.DIALOG_SEARCH_CUSTOM_ENGINE)
                        }
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_taplus)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.search_use_browser),
                    key = Pref.Key.Taplus.SEARCH_USE_BROWSER
                ) {
                    spTaplusBrowserSearch = it
                }
                AnimatedVisibility(
                    spTaplusBrowserSearch
                ) {
                    Column {
                        DropDownPreference(
                            title = stringResource(R.string.search_engine),
                            entries = dropdownEntriesSearchEngine,
                            key = Pref.Key.Taplus.SEARCH_ENGINE
                        ) {
                            visibilityTaplusCustomEntryName = (it == 5)
                        }
                        AnimatedVisibility(
                            visibilityTaplusCustomEntryName
                        ) {
                            EditTextPreference(
                                title = stringResource(R.string.search_engine_custom_url),
                                key = Pref.Key.Taplus.SEARCH_URL,
                                dataType = EditTextDataType.STRING,
                                dialogMessage = stringResource(R.string.search_engine_custom_url_toast) + "\nhttps://example.com/s?q=%s",
                                isValueValid = { value ->
                                    val string = value as String
                                    string.isBlank() || string.contains("%s")
                                },
                                valuePosition = ValuePosition.SUMMARY_VIEW
                            )
                        }
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_settings)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_settings_show_google),
                    key = Pref.Key.Settings.SHOE_GOOGLE
                )
                TextPreference(
                    title = stringResource(R.string.others_settings_cellular_debug)
                ) {
                    context.let {
                        val intent = Intent().apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            component = ComponentName("com.xiaomi.phone", "com.xiaomi.phone.settings.development.CellularNetworkActivity")
                        }
                        try {
                            it.startActivity(intent)
                        } catch (t: Throwable) {
                            Toast.makeText(it, t.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (Device.isPad) {
                    SwitchPreference(
                        title = stringResource(R.string.others_settings_unlock_taplus_for_pad),
                        key = Pref.Key.Settings.UNLOCK_TAPLUS_FOR_PAD
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.others_settings_quick_per_overlay),
                    summary = stringResource(R.string.others_settings_quick_per_tips),
                    key = Pref.Key.Settings.QUICK_PER_OVERLAY
                )
                SwitchPreference(
                    title = stringResource(R.string.others_settings_quick_per_install),
                    summary = stringResource(R.string.others_settings_quick_per_tips),
                    key = Pref.Key.Settings.QUICK_PER_INSTALL_SOURCE
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_updater)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_updater_disable_validation),
                    summary = stringResource(R.string.others_updater_disable_validation_tips),
                    key = Pref.Key.Updater.DISABLE_VALIDATION
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_others_weather),
                last = true
            ) {
                DropDownPreference(
                    title = stringResource(R.string.weather_card_color),
                    summary = stringResource(R.string.weather_card_color_tips),
                    entries = dropDownEntriesWeatherCard,
                    key = Pref.Key.Weather.CARD_COLOR
                )
            }
        }
    }
}