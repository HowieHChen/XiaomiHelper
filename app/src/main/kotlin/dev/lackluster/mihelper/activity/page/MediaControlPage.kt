package dev.lackluster.mihelper.activity.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.activity.component.MediaControlCard
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
        DropDownEntry(stringResource(R.string.media_lyt_album_default)),
        DropDownEntry(stringResource(R.string.media_lyt_album_hide_app)),
        DropDownEntry(stringResource(R.string.media_lyt_album_gone))
    )
    val thumbStyleEntries = listOf(
        DropDownEntry(stringResource(R.string.media_elm_thumb_style_default)),
        DropDownEntry(stringResource(R.string.media_elm_thumb_style_gone)),
        DropDownEntry(stringResource(R.string.media_elm_thumb_style_vbar))
    )
    val progressStyleEntries = listOf(
        DropDownEntry(stringResource(R.string.media_elm_prog_style_default)),
        DropDownEntry(stringResource(R.string.media_elm_prog_style_custom)),
        DropDownEntry(stringResource(R.string.media_elm_prog_style_squiggly))
    )
    var backgroundStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
    ) }
    var blurRadius by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.BLUR_RADIUS, 10)
    ) }
    var allowReverse by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE, false)
    ) }
    var lytAlbum by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.LYT_ALBUM, 0)
    ) }
    var lytLeftActions by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_LEFT_ACTIONS, false)
    ) }
    var lytHideTime by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_HIDE_TIME, false)
    ) }
    var lytHideSeamless by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.LYT_HIDE_SEAMLESS, false)
    ) }
    var modifyTextSize by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE, false)
    ) }
    var titleSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE, 18.0f)
    ) }
    var artistSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE, 12.0f)
    ) }
    var timeSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE, 12.0f)
    ) }
    var thumbStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE, 0)
    ) }
    var progressStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE, 0)
    ) }
    var progressWidth by remember { mutableFloatStateOf(
        SafeSP.getFloat(Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH, 4.0f)
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
            MediaControlCard(
                backgroundStyle = backgroundStyle,
                allowReverse = allowReverse,
                blurRadius = blurRadius,
                lytAlbum = lytAlbum,
                lytLeftActions = lytLeftActions,
                lytHideTime = lytHideTime,
                lytHideSeamless = lytHideSeamless,
                modifyTextSize = modifyTextSize,
                titleSize = titleSize,
                artistSize = artistSize,
                timeSize = timeSize,
                thumbStyle = thumbStyle,
                progressStyle = progressStyle,
                progressWidth = progressWidth
            )
        }
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
                    backgroundStyle != 0
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.media_bg_color_anim),
                        summary = stringResource(R.string.media_bg_color_anim_tips),
                        key = Pref.Key.SystemUI.MediaControl.USE_ANIM,
                        defValue = true
                    )
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
                    ) {
                        blurRadius = it
                    }
                }
                AnimatedVisibility(
                    backgroundStyle == 4
                ) {
                    SwitchPreference(
                        title = stringResource(R.string.media_bg_auto_color_reverse),
                        key = Pref.Key.SystemUI.MediaControl.ALLOW_REVERSE
                    ) {
                        allowReverse = it
                    }
                }
            }
            PreferenceGroup(
                title = stringResource(R.string.ui_title_media_layout)
            ) {
                DropDownPreference(
                    title = stringResource(R.string.media_lyt_album),
                    entries = albumStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.LYT_ALBUM
                ) {
                    lytAlbum = it
                }
                SwitchPreference(
                    title = stringResource(R.string.media_lyt_left_actions),
                    key = Pref.Key.SystemUI.MediaControl.LYT_LEFT_ACTIONS
                ) {
                    lytLeftActions = it
                }
                SwitchPreference(
                    title = stringResource(R.string.media_lyt_hide_time),
                    summary = stringResource(R.string.media_lyt_hide_time_tips),
                    key = Pref.Key.SystemUI.MediaControl.LYT_HIDE_TIME
                ) {
                    lytHideTime = it
                }
                SwitchPreference(
                    title = stringResource(R.string.media_lyt_hide_seamless),
                    summary = stringResource(R.string.media_lyt_hide_seamless_tips),
                    key = Pref.Key.SystemUI.MediaControl.LYT_HIDE_SEAMLESS
                ) {
                    lytHideSeamless = it
                }
            }
            PreferenceGroup(
                title = stringResource(R.string.ui_title_media_elements)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.media_elm_text_size),
                    key = Pref.Key.SystemUI.MediaControl.ELM_TEXT_SIZE
                ) {
                    modifyTextSize = it
                }
                AnimatedVisibility(
                    modifyTextSize
                ) {
                    Column {
                        SeekBarPreference(
                            title = stringResource(R.string.media_elm_text_size_title),
                            key = Pref.Key.SystemUI.MediaControl.ELM_TITLE_SIZE,
                            defValue = 18.0f,
                            min = 0.5f,
                            max = 36.0f
                        ) {
                            titleSize = it
                        }
                        SeekBarPreference(
                            title = stringResource(R.string.media_elm_text_size_artist),
                            key = Pref.Key.SystemUI.MediaControl.ELM_ARTIST_SIZE,
                            defValue = 12.0f,
                            min = 0.5f,
                            max = 24.0f
                        ) {
                            artistSize = it
                        }
                        SeekBarPreference(
                            title = stringResource(R.string.media_elm_text_size_time),
                            key = Pref.Key.SystemUI.MediaControl.ELM_TIME_SIZE,
                            defValue = 12.0f,
                            min = 0.5f,
                            max = 24.0f
                        ) {
                            timeSize = it
                        }
                    }
                }
                DropDownPreference(
                    title = stringResource(R.string.media_elm_thumb_style),
                    entries = thumbStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.ELM_THUMB_STYLE
                ) {
                    thumbStyle = it
                }
                DropDownPreference(
                    title = stringResource(R.string.media_elm_prog_style),
                    entries = progressStyleEntries,
                    key = Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_STYLE
                ) {
                    progressStyle = it
                }
                AnimatedVisibility(
                    progressStyle == 1
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.media_elm_prog_width),
                        key = Pref.Key.SystemUI.MediaControl.ELM_PROGRESS_WIDTH,
                        defValue = 4.0f,
                        min = 0.5f,
                        max = 14.0f
                    ) {
                        progressWidth = it
                    }
                }
            }
        }
    }
}