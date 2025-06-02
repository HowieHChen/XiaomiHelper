package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@Composable
fun StatusBarClockPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var spValueLayoutCustom by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM)) }
    var spValueGeekMode by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK)) }
    var spValueShowSecond by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS)) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_clock),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_clock_general),
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.clock_general_custom_layout),
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_LAYOUT_CUSTOM
                ) {
                    spValueLayoutCustom = it
                }
                AnimatedVisibility(
                    spValueLayoutCustom
                ) {
                    Column {
                        EditTextPreference(
                            title = stringResource(R.string.clock_general_padding_left),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_LEFT,
                            defValue = 0.0f,
                            dataType = EditTextDataType.FLOAT,
                            dialogMessage = stringResource(R.string.clock_general_custom_layout)
                        )
                        EditTextPreference(
                            title = stringResource(R.string.clock_general_padding_right),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_RIGHT,
                            defValue = 0.0f,
                            dataType = EditTextDataType.FLOAT,
                            dialogMessage = stringResource(R.string.clock_general_custom_layout)
                        )
                    }
                }
                SwitchPreference(
                    title = stringResource(R.string.clock_general_geek),
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK
                ) {
                    spValueGeekMode = it
                }
                SwitchPreference(
                    title = stringResource(R.string.clock_font_tnum),
                    summary = stringResource(R.string.clock_font_tnum_tips),
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_TNUM
                )
            }
        }
        item {
            PreferenceGroup(
                title = if (spValueGeekMode) {
                    stringResource(R.string.ui_title_clock_geek_mode)
                } else {
                    stringResource(R.string.ui_title_clock_easy)
                },
                last = true
            ) {
                AnimatedVisibility(
                    spValueGeekMode
                ) {
                    Column {
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT,
                            dataType = EditTextDataType.STRING
                        )
                        if (Device.isPad) {
                            EditTextPreference(
                                title = stringResource(R.string.clock_geek_time_format_pattern_pad),
                                key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD,
                                defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD,
                                dataType = EditTextDataType.STRING
                            )
                        }
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern_horizon),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON,
                            dataType = EditTextDataType.STRING
                        )
                    }
                }
                AnimatedVisibility(
                    !spValueGeekMode
                ) {
                    Column {
                        SwitchPreference(
                            title = stringResource(R.string.clock_easy_show_ampm),
                            summary = stringResource(R.string.clock_easy_show_ampm_tips),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_AMPM
                        )
                        SwitchPreference(
                            title = stringResource(R.string.clock_easy_show_leading_zero),
                            summary = stringResource(R.string.clock_easy_show_leading_zero_tips),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_LEADING_ZERO
                        )
                        SwitchPreference(
                            title = stringResource(R.string.clock_easy_show_seconds),
                            summary = stringResource(R.string.clock_easy_show_seconds_tips),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_SHOW_SECONDS
                        ) {
                            spValueShowSecond = it
                        }
                        AnimatedVisibility(
                            spValueShowSecond
                        ) {
                            SwitchPreference(
                                title = stringResource(R.string.clock_easy_fixed_width),
                                summary = stringResource(R.string.clock_easy_fixed_width_tips),
                                key = Pref.Key.SystemUI.StatusBar.CLOCK_FIXED_WIDTH
                            )
                        }
                    }
                }
            }
        }
    }
}