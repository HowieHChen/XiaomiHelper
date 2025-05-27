package dev.lackluster.mihelper.activity.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.activity.component.RebootMenuItem
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope

@Composable
fun MediaControlPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val backgroundStyleEntries = listOf(
        DropDownEntry(stringResource(R.string.media_bg_custom_default)),
        DropDownEntry(stringResource(R.string.media_bg_custom_art)),
        DropDownEntry(stringResource(R.string.media_bg_custom_blur)),
        DropDownEntry(stringResource(R.string.media_bg_custom_radial_gradient)),
        DropDownEntry(stringResource(R.string.media_bg_custom_linear_gradient))
    )
    val albumStyleEntries = listOf(
        DropDownEntry("VISIBLE"),
        DropDownEntry("HIDE APP ICON"),
        DropDownEntry("GONE")
    )
    val thumbStyleEntries = listOf(
        DropDownEntry("DEFAULT"),
        DropDownEntry("GONE"),
        DropDownEntry("VERTICAL BAR")
    )
    val progressStyleEntries = listOf(
        DropDownEntry("DEFAULT"),
        DropDownEntry("CUSTOM"),
        DropDownEntry("SQUIGGLY")
    )
    var backgroundStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    ) }
    var modifyTextSize by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE, false)
    ) }
    var progressStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE, 0)
    ) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_media_control_style),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        actions = {
            RebootMenuItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = Scope.SYSTEM_UI
            )
        }
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_media_bg)
            ) {
                DropDownPreference(
                    title = stringResource(R.string.media_bg_custom),
                    entries = backgroundStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE
                ) {
                    backgroundStyle = it
                }
                AnimatedVisibility(
                    backgroundStyle == 2
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.media_bg_blur_radius),
                        key = Pref.Key.SystemUI.MediaControl.BLUR_RADIUS,
                        defValue = 10,
                        min = 1,
                        max = 20
                    )
                }
                AnimatedVisibility(
                    backgroundStyle == 4
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.media_bg_auto_color_reverse),
                        key = Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE
                    )
                }
            }
            PreferenceGroup(
                title = "LAYOUT"
            ) {
                DropDownPreference(
                    title = "LYT_ALBUM",
                    entries = albumStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.LYT_ALBUM
                )
                SwitchPreference(
                    title = "LYT_LEFT_ACTIONS",
                    key = Pref.Key.SystemUI.MediaControl.LYT_LEFT_ACTIONS
                )
                SwitchPreference(
                    title = "LYT_HIDE_TIME",
                    key = Pref.Key.SystemUI.MediaControl.LYT_HIDE_TIME
                )
                SwitchPreference(
                    title = "LYT_HIDE_SEAMLESS",
                    key = Pref.Key.SystemUI.MediaControl.LYT_HIDE_SEAMLESS
                )
            }
            PreferenceGroup(
                title = "ELEMENT"
            ) {
                SwitchPreference(
                    title = "ELM_TEXT_SIZE",
                    key = Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE
                ) {
                    modifyTextSize = it
                }
                AnimatedVisibility(
                    modifyTextSize
                ) {
                    Column {
                        EditTextPreference(
                            title = "ELM_TITLE_SIZE",
                            key = Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE,
                            defValue = 18.0f,
                            dataType = EditTextDataType.FLOAT,
                            isValueValid = {
                                (it as? Float ?: -1.0f) > 0.0f
                            }
                        )
                        EditTextPreference(
                            title = "ELM_ARTIST_SIZE",
                            key = Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE,
                            defValue = 12.0f,
                            dataType = EditTextDataType.FLOAT,
                            isValueValid = {
                                (it as? Float ?: -1.0f) > 0.0f
                            }
                        )
                        EditTextPreference(
                            title = "ELM_TIME_SIZE",
                            key = Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE,
                            defValue = 12.0f,
                            dataType = EditTextDataType.FLOAT,
                            isValueValid = {
                                (it as? Float ?: -1.0f) > 0.0f
                            }
                        )
                    }
                }
                DropDownPreference(
                    title = "ELM_THUMB_STYLE",
                    entries = thumbStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE
                )
                DropDownPreference(
                    title = "ELM_PROGRESS_STYLE",
                    entries = progressStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE
                ) {
                    progressStyle = it
                }
                AnimatedVisibility(
                    progressStyle == 1
                ) {
                    EditTextPreference(
                        title = "ELM_PROGRESS_WIDTH",
                        key = Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH,
                        defValue = 4.0f,
                        dataType = EditTextDataType.FLOAT,
                        isValueValid = {
                            (it as? Float ?: -1.0f).let {
                                it > 0.0f && it <= 14.0f
                            }
                        }
                    )
                }
            }
            PreferenceGroup(
                title = stringResource(R.string.ui_title_media_others)
            ) {
                AnimatedVisibility(
                    backgroundStyle != 0
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.media_others_color_anim),
                        summary = stringResource(R.string.media_others_color_anim_tips),
                        key = Pref.Key.SystemUI.MediaControl.USE_ANIM,
                        defValue = true
                    )
                }
            }
        }
    }
}