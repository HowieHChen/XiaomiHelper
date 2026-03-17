package dev.lackluster.mihelper.ui.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup

@Composable
fun StackedMobileTunerPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    val dropdownEntriesIconStyle = listOf(
        DropDownEntry(stringResource(R.string.stacked_icon_style_miui)),
        DropDownEntry(stringResource(R.string.stacked_icon_style_ios)),
        DropDownEntry(stringResource(R.string.stacked_icon_style_custom)),
    )

    var spValueIconStyleSingle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_SVG_SINGLE, 0)
    ) }
    var spValueIconStyleStacked by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_SVG_STACKED, 0)
    ) }
    var spValueAlphaFg by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_ALPHA_FG, 1.0f)
    ) }
    var spValueAlphaBg by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_ALPHA_BG, 0.4f)
    ) }
    var spValueAlphaError by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_ALPHA_ERROR, 0.2f)
    ) }
    var spValueMobileTypeFW by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.STACKED_MOBILE_TYPE, false)
    ) }
    var spValueMobileTypeFWVal by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.FontWeight.STACKED_MOBILE_TYPE_VAL, 660)
    ) }
    var spValueTypeHideWhenDisconnect by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_HIDE_DISCONNECT, true)
    ) }
    var spValueTypeHideWhenWifi by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_HIDE_WIFI, true)
    ) }
    var spValueTypeSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_SIZE, 14.0f)
    ) }
    var spValueTypePaddingStart by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_PADDING_START_VAL, 2.0f)
    ) }
    var spValueTypePaddingEnd by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_PADDING_END_VAL, 2.0f)
    ) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_stacked_mobile),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        actions = {
            RebootMenuItem(
                stringResource(R.string.scope_systemui),
                Scope.SYSTEM_UI
            )
        }
    ) {
        itemPreferenceGroup(
            key = "STACKED_MOBILE_ICON",
            titleResId = R.string.ui_title_stacked_icon
        ) {
            DropDownPreference(
                title = stringResource(R.string.stacked_icon_style_single),
                entries = dropdownEntriesIconStyle,
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_SVG_SINGLE
            ) {
                spValueIconStyleSingle = it
            }
            AnimatedVisibility(spValueIconStyleSingle == 2) {
                EditTextPreference(
                    title = stringResource(R.string.stacked_icon_style_single_val),
                    key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_SVG_SINGLE_VAL,
                    defValue = "",
                    dataType = EditTextDataType.STRING,
                    dialogMessage = stringResource(R.string.stacked_icon_style_single_msg),
                    isValueValid = {
                        val newValue = it.toString()
                        if (newValue.isBlank()) {
                            return@EditTextPreference false
                        } else {
                            listOf(
                                "signal_1", "signal_2", "signal_3", "signal_4",
                            ).forEach{ id ->
                                val regex = Regex("""id\s*=\s*['"]$id['"]""")
                                if (!regex.containsMatchIn(newValue)) {
                                    Toast.makeText(
                                        context,
                                        "SVG Validation failed: Missing required ID -> $id",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@EditTextPreference false
                                }
                            }
                            return@EditTextPreference true
                        }
                    },
                    valuePosition = ValuePosition.HIDDEN,
                )
            }
            DropDownPreference(
                title = stringResource(R.string.stacked_icon_style_stacked),
                entries = dropdownEntriesIconStyle,
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_SVG_STACKED
            ) {
                spValueIconStyleStacked = it
            }
            AnimatedVisibility(spValueIconStyleStacked == 2) {
                EditTextPreference(
                    title = stringResource(R.string.stacked_icon_style_stacked_val),
                    key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_SVG_STACKED_VAL,
                    defValue = "",
                    dataType = EditTextDataType.STRING,
                    dialogMessage = stringResource(R.string.stacked_icon_style_stacked_msg),
                    isValueValid = {
                        val newValue = it.toString()
                        if (newValue.isBlank()) {
                            return@EditTextPreference false
                        } else {
                            listOf(
                                "signal_1_1", "signal_1_2", "signal_1_3", "signal_1_4",
                                "signal_2_1", "signal_2_2", "signal_2_3", "signal_2_4",
                            ).forEach{ id ->
                                val regex = Regex("""id\s*=\s*['"]$id['"]""")
                                if (!regex.containsMatchIn(newValue)) {
                                    Toast.makeText(
                                        context,
                                        "SVG Validation failed: Missing required ID -> $id",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@EditTextPreference false
                                }
                            }
                            return@EditTextPreference true
                        }
                    },
                    valuePosition = ValuePosition.HIDDEN,
                )
            }
            SeekBarPreference(
                title = stringResource(R.string.stacked_icon_alpha_fg),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_ALPHA_FG,
                defValue = 1.0f,
                min = 0.0f,
                max = 1.0f,
            ) {
                spValueAlphaFg = it
            }
            SeekBarPreference(
                title = stringResource(R.string.stacked_icon_alpha_bg),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_ALPHA_BG,
                defValue = 0.4f,
                min = 0.0f,
                max = 1.0f,
            ) {
                spValueAlphaBg = it
            }
            SeekBarPreference(
                title = stringResource(R.string.stacked_icon_alpha_error),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_ICON_ALPHA_ERROR,
                defValue = 0.2f,
                min = 0.0f,
                max = 1.0f,
            ) {
                spValueAlphaError = it
            }
        }
        itemPreferenceGroup(
            key = "STACKED_MOBILE_TYPE",
            titleResId = R.string.ui_title_stacked_type,
            last = true
        ) {
            SwitchPreference(
                title = stringResource(R.string.stacked_type_hide_disconnect),
                summary = stringResource(R.string.stacked_type_hide_disconnect_tips),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_HIDE_DISCONNECT,
                defValue = true
            ) { spValueTypeHideWhenDisconnect = it }
            SwitchPreference(
                title = stringResource(R.string.stacked_type_hide_wifi),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_HIDE_WIFI,
                defValue = true
            ) { spValueTypeHideWhenWifi = it }
            SwitchPreference(
                title = stringResource(R.string.icon_detail_cellular_fw_type),
                key = Pref.Key.SystemUI.FontWeight.STACKED_MOBILE_TYPE
            ) { spValueMobileTypeFW = it }
            AnimatedVisibility(spValueMobileTypeFW) {
                SeekBarPreference(
                    title = stringResource(R.string.icon_detail_cellular_fw_type_weight),
                    key = Pref.Key.SystemUI.FontWeight.STACKED_MOBILE_TYPE_VAL,
                    defValue = 400,
                    min = 1,
                    max = 1000
                ) { spValueMobileTypeFWVal = it }
            }
            EditTextPreference(
                title = stringResource(R.string.stacked_type_size),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_SIZE,
                defValue = 14.0f,
                dataType = EditTextDataType.FLOAT,
                isValueValid = {
                    (it as? Float ?: -1.0f) > 0.0f
                }
            ) { _, value -> spValueTypeSize = value as? Float ?: 0.0f}
            EditTextPreference(
                title = stringResource(R.string.icon_detail_battery_padding_start),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_PADDING_START_VAL,
                defValue = 2.0f,
                dataType = EditTextDataType.FLOAT
            ) { _, value -> spValueTypePaddingStart = value as Float }
            EditTextPreference(
                title = stringResource(R.string.icon_detail_battery_padding_end),
                key = Pref.Key.SystemUI.IconTuner.STACKED_MOBILE_TYPE_PADDING_END_VAL,
                defValue = 2.0f,
                dataType = EditTextDataType.FLOAT
            ) { _, value -> spValueTypePaddingEnd = value as Float }
        }
    }
}