package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
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
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import java.io.File

@Composable
fun StatusBarFontPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var spValueClockFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CLOCK)) }
    var spValueClockNotifFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CLOCK_NOTIFICATION)) }
    var spValueFocusNotifFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.FOCUS_NOTIFICATION)) }
    var spValueNetSpeedNumFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER)) }
    var spValueNetSpeedUnitFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT)) }
    var spValueMobileTypeFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.MOBILE_TYPE)) }
    var spValueBatPctInFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN)) }
    var spValueBatPctOutFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT)) }
    var spValueBatPctMarkFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK)) }
    var spValueCarrierFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.CARRIER)) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_font),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_font_general),
                first = true
            ) {
                EditTextPreference(
                    title = stringResource(R.string.font_general_path),
                    key = Pref.Key.SystemUI.FontWeight.FONT_PATH,
                    defValue = "/system/fonts/MiSansVF.ttf",
                    dataType = EditTextDataType.STRING,
                    dialogMessage = stringResource(R.string.font_general_path_tips),
                    isValueValid = { path ->
                        (path as? String)?.let {
                            val file = File(it)
                            file.exists() && file.isFile
                        } ?: false
                    },
                    valuePosition = ValuePosition.SUMMARY_VIEW
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_font_sb)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.font_sb_clock),
                    key = Pref.Key.SystemUI.FontWeight.CLOCK
                ) {
                    spValueClockFont = it
                }
                AnimatedVisibility(
                    spValueClockFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_sb_clock_weight),
                        key = Pref.Key.SystemUI.FontWeight.CLOCK_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.font_sb_focus),
                    key = Pref.Key.SystemUI.FontWeight.FOCUS_NOTIFICATION
                ) {
                    spValueFocusNotifFont = it
                }
                AnimatedVisibility(
                    spValueFocusNotifFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_sb_focus_weight),
                        key = Pref.Key.SystemUI.FontWeight.FOCUS_NOTIFICATION_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.font_sb_bat_pct_out),
                    key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT
                ) {
                    spValueBatPctOutFont = it
                }
                AnimatedVisibility(
                    spValueBatPctOutFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_sb_bat_pct_out_weight),
                        key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_OUT_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.font_sb_bat_pct_mark),
                    key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK
                ) {
                    spValueBatPctMarkFont = it
                }
                AnimatedVisibility(
                    spValueBatPctMarkFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_sb_bat_pct_mark_weight),
                        key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_MARK_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_font_icon)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.font_icon_bat_pct_in),
                    key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN
                ) {
                    spValueBatPctInFont = it
                }
                AnimatedVisibility(
                    spValueBatPctInFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_icon_bat_pct_in_weight),
                        key = Pref.Key.SystemUI.FontWeight.BATTERY_PERCENTAGE_IN_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.font_icon_net_speed_num),
                    key = Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER
                ) {
                    spValueNetSpeedNumFont = it
                }
                AnimatedVisibility(
                    spValueNetSpeedNumFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_icon_net_speed_num_weight),
                        key = Pref.Key.SystemUI.FontWeight.NET_SPEED_NUMBER_WEIGHT,
                        defValue = 700,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.font_icon_net_speed_unit),
                    key = Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT
                ) {
                    spValueNetSpeedUnitFont = it
                }
                AnimatedVisibility(
                    spValueNetSpeedUnitFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_icon_net_speed_unit_weight),
                        key = Pref.Key.SystemUI.FontWeight.NET_SPEED_UNIT_WEIGHT,
                        defValue = 700,
                        min = 1,
                        max = 1000
                    )
                }
                SwitchPreference(
                    title = stringResource(R.string.font_icon_mobile_type),
                    key = Pref.Key.SystemUI.FontWeight.MOBILE_TYPE
                ) {
                    spValueMobileTypeFont = it
                }
                AnimatedVisibility(
                    spValueMobileTypeFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_icon_mobile_type_weight),
                        key = Pref.Key.SystemUI.FontWeight.MOBILE_TYPE_WEIGHT,
                        defValue = 620,
                        min = 1,
                        max = 1000
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_font_notification)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.font_notif_clock),
                    key = Pref.Key.SystemUI.FontWeight.CLOCK_NOTIFICATION
                ) {
                    spValueClockNotifFont = it
                }
                AnimatedVisibility(
                    spValueClockNotifFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_notif_clock_weight),
                        key = Pref.Key.SystemUI.FontWeight.CLOCK_NOTIFICATION_WEIGHT,
                        defValue = 305,
                        min = 1,
                        max = 1000
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_font_lockscreen),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.font_lockscreen_carrier),
                    key = Pref.Key.SystemUI.FontWeight.CARRIER
                ) {
                    spValueCarrierFont = it
                }
                AnimatedVisibility(
                    spValueCarrierFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_lockscreen_carrier_weight),
                        key = Pref.Key.SystemUI.FontWeight.CARRIER_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
            }
        }
    }
}