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
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device

@Composable
fun StatusBarClockPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var spValueLayoutCustom by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_HORIZON)) }
    var spValueGeekMode by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.StatusBar.CLOCK_GEEK)) }
    var clockFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CLOCK, false)
    ) }
    var padClockFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.PAD_CLOCK, false)
    ) }
    var bigTimeFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BIG_TIME, false)
    ) }
    var dateTimeFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.DATE_TIME, false)
    ) }
    var controlCenterDateFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CC_DATE, false)
    ) }
    var horizontalTimeFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.HORIZONTAL_TIME, false)
    ) }
    val defClockFWVal = if (Device.isPad) 460 else 500

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
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_HORIZON
                ) {
                    spValueLayoutCustom = it
                }
                AnimatedVisibility(
                    spValueLayoutCustom
                ) {
                    Column {
                        EditTextPreference(
                            title = stringResource(R.string.clock_general_padding_left),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_START_VAL,
                            defValue = 0.0f,
                            dataType = EditTextDataType.FLOAT,
                            dialogMessage = stringResource(R.string.clock_general_custom_layout)
                        )
                        EditTextPreference(
                            title = stringResource(R.string.clock_general_padding_right),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_PADDING_END_VAL,
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
                    title = stringResource(R.string.clock_easy_fixed_width),
                    summary = stringResource(R.string.clock_easy_fixed_width_tips),
                    key = Pref.Key.SystemUI.StatusBar.CLOCK_FIXED_WIDTH
                )
            }
        }
        item {
            PreferenceGroup(
                title = if (spValueGeekMode) {
                    stringResource(R.string.ui_title_clock_geek_mode)
                } else {
                    stringResource(R.string.ui_title_clock_easy)
                }
            ) {
                AnimatedVisibility(
                    spValueGeekMode
                ) {
                    Column {
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern_clock),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_CLOCK,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_CLOCK,
                            dataType = EditTextDataType.STRING
                        )
                        if (Device.isPad) {
                            EditTextPreference(
                                title = stringResource(R.string.clock_geek_time_format_pattern_pad_clock),
                                key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_PAD_CLOCK,
                                defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_PAD_CLOCK,
                                dataType = EditTextDataType.STRING
                            )
                        }
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern_big_time),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_BIG_TIME,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_BIG_TIME,
                            dataType = EditTextDataType.STRING
                        )
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern_date_time),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_DATE_TIME,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_DATE_TIME,
                            dataType = EditTextDataType.STRING
                        )
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern_cc_date),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_CC_DATE,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_CC_DATE,
                            dataType = EditTextDataType.STRING
                        )
                        EditTextPreference(
                            title = stringResource(R.string.clock_geek_time_format_pattern_horizon),
                            key = Pref.Key.SystemUI.StatusBar.CLOCK_GEEK_FORMAT_HORIZON_TIME,
                            defValue = Pref.DefValue.SystemUI.CLOCK_GEEK_FORMAT_HORIZON_TIME,
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
                        )
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_clock_font_weight),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.clock_fw_clock),
                    key = Pref.Key.SystemUI.FontWeight.CLOCK
                ) { clockFW = it }
                AnimatedVisibility(clockFW) {
                    SeekBarPreference(
                        title = stringResource(R.string.clock_fw_clock_weight),
                        key = Pref.Key.SystemUI.FontWeight.CLOCK_WEIGHT,
                        defValue = defClockFWVal,
                        min = 1,
                        max = 1000
                    )
                }
                if (Device.isPad) {
                    SwitchPreference(
                        title = stringResource(R.string.clock_fw_pad_clock),
                        key = Pref.Key.SystemUI.FontWeight.PAD_CLOCK
                    ) { padClockFW = it }
                    AnimatedVisibility(padClockFW) {
                        SeekBarPreference(
                            title = stringResource(R.string.clock_fw_pad_clock_weight),
                            key = Pref.Key.SystemUI.FontWeight.PAD_CLOCK_WEIGHT,
                            defValue = defClockFWVal,
                            min = 1,
                            max = 1000
                        )
                    }
                }
                SwitchPreference(
                    title = stringResource(R.string.clock_fw_big_time),
                    key = Pref.Key.SystemUI.FontWeight.BIG_TIME
                ) { bigTimeFW = it }
                AnimatedVisibility(bigTimeFW) {
                    SeekBarPreference(
                        title = stringResource(R.string.clock_fw_big_time_weight),
                        key = Pref.Key.SystemUI.FontWeight.BIG_TIME_WEIGHT,
                        defValue = 305,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.clock_fw_date_time),
                    key = Pref.Key.SystemUI.FontWeight.DATE_TIME
                ) { dateTimeFW = it }
                AnimatedVisibility(dateTimeFW) {
                    SeekBarPreference(
                        title = stringResource(R.string.clock_fw_date_time_weight),
                        key = Pref.Key.SystemUI.FontWeight.DATE_TIME_WEIGHT,
                        defValue = 400,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.clock_fw_cc_date),
                    key = Pref.Key.SystemUI.FontWeight.CC_DATE
                ) { controlCenterDateFW = it }
                AnimatedVisibility(controlCenterDateFW) {
                    SeekBarPreference(
                        title = stringResource(R.string.clock_fw_cc_date_weight),
                        key = Pref.Key.SystemUI.FontWeight.CC_DATE_WEIGHT,
                        defValue = 400,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.clock_fw_horizontal_time),
                    key = Pref.Key.SystemUI.FontWeight.HORIZONTAL_TIME
                ) { horizontalTimeFW = it }
                AnimatedVisibility(horizontalTimeFW) {
                    SeekBarPreference(
                        title = stringResource(R.string.clock_fw_horizontal_time_weight),
                        key = Pref.Key.SystemUI.FontWeight.HORIZONTAL_TIME_WEIGHT,
                        defValue = defClockFWVal,
                        min = 1,
                        max = 1000
                    )
                }
            }
        }
    }
}