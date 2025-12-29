package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.TabRow
import dev.lackluster.hyperx.compose.component.Hint
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.MediaControlSpKey
import dev.lackluster.mihelper.data.Constants.getKey
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.MediaControlCard
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.ui.component.itemAnimated
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup

@Composable
fun MediaControlPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode, isDynamicIsland: Boolean) {
    val hapticFeedback = LocalHapticFeedback.current
    var hintAdvancedTextures by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.Hints.MEDIA_ADVANCED_TEXTURES, false)
    ) }
    val tabRowItems = listOf(
        stringResource(R.string.ui_title_media_bg),
        stringResource(R.string.ui_title_media_layout),
        stringResource(R.string.ui_title_media_elements),
    )
    val backgroundStyleEntries = listOf(
        DropDownEntry(stringResource(R.string.media_bg_custom_default)),
        DropDownEntry(stringResource(R.string.media_bg_custom_art)),
        DropDownEntry(stringResource(R.string.media_bg_custom_blur)),
        DropDownEntry(stringResource(R.string.media_bg_custom_radial_gradient)),
        DropDownEntry(stringResource(R.string.media_bg_custom_linear_gradient))
    )
    val backgroundAmbientLightEntries = listOf(
        DropDownEntry(stringResource(R.string.media_bg_ambient_light_default)),
        DropDownEntry(stringResource(R.string.media_bg_ambient_light_hidden)),
        DropDownEntry(stringResource(R.string.media_bg_ambient_light_custom)),
    )
    val albumStyleEntries = listOf(
        DropDownEntry(stringResource(R.string.media_lyt_album_default)),
        DropDownEntry(stringResource(R.string.media_lyt_album_hide_app)),
        DropDownEntry(stringResource(R.string.media_lyt_album_gone))
    )
    val actionsOrderEntries = listOf(
        DropDownEntry(stringResource(R.string.media_lyt_actions_order_default)),
        DropDownEntry(stringResource(R.string.media_lyt_actions_order_custom_right)),
        DropDownEntry(stringResource(R.string.media_lyt_actions_order_play_left))
    )
    val thumbStyleEntries = listOf(
        DropDownEntry(stringResource(R.string.media_elm_thumb_style_default)),
        DropDownEntry(stringResource(R.string.media_elm_thumb_style_gone)),
        DropDownEntry(stringResource(R.string.media_elm_thumb_style_vbar))
    )
    val progressStyleEntries = listOfNotNull(
        DropDownEntry(stringResource(R.string.media_elm_prog_style_default)),
        DropDownEntry(stringResource(R.string.media_elm_prog_style_custom)),
        if (isDynamicIsland) null else DropDownEntry(stringResource(R.string.media_elm_prog_style_squiggly))
    )
    var tabRowSelected by remember { mutableIntStateOf(0) }
    var backgroundStyle by remember { mutableIntStateOf(
        SafeSP.getInt(MediaControlSpKey.BACKGROUND_STYLE.getKey(isDynamicIsland), 0)
    ) }
    var blurRadius by remember { mutableIntStateOf(
        SafeSP.getInt(MediaControlSpKey.BLUR_RADIUS.getKey(isDynamicIsland), 10)
    ) }
    var allowReverse by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.ALLOW_REVERSE.getKey(isDynamicIsland), false)
    ) }
    var ambientLight by remember { mutableStateOf(
        if (isDynamicIsland) {
            SafeSP.getInt(Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT_TYPE, 0) != 1
        } else {
            SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT, false)
        }
    ) }
    var ambientLightType by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT_TYPE, 0)
    ) }
    var ambientLightOpt by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.AMBIENT_LIGHT_OPT.getKey(isDynamicIsland), false)
    ) }
    var lytAlbum by remember { mutableIntStateOf(
        SafeSP.getInt(MediaControlSpKey.LYT_ALBUM.getKey(isDynamicIsland), 0)
    ) }
    var lytLeftActions by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.LYT_LEFT_ACTIONS.getKey(isDynamicIsland), false)
    ) }
    var lytActionsOrder by remember { mutableIntStateOf(
        SafeSP.getInt(MediaControlSpKey.LYT_ACTIONS_ORDER.getKey(isDynamicIsland), 0)
    ) }
    var lytHideTime by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.LYT_HIDE_TIME.getKey(isDynamicIsland), false)
    ) }
    var lytHideSeamless by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.LYT_HIDE_SEAMLESS.getKey(isDynamicIsland), false)
    ) }
    var lytHeaderMargin by remember { mutableFloatStateOf(
        SafeSP.getFloat(MediaControlSpKey.LYT_HEADER_MARGIN.getKey(isDynamicIsland), 21.0f)
    ) }
    var lytHeaderPadding by remember { mutableFloatStateOf(
        SafeSP.getFloat(MediaControlSpKey.LYT_HEADER_PADDING.getKey(isDynamicIsland), 4.0f)
    ) }
    var albumShadow by remember { mutableStateOf(
        !isDynamicIsland && SafeSP.getBoolean(Pref.Key.SystemUI.MediaControl.ELM_ALBUM_SHADOW, true)
    ) }
    var modifyTextSize by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.ELM_TEXT_SIZE.getKey(isDynamicIsland), false)
    ) }
    var titleSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(MediaControlSpKey.ELM_TITLE_SIZE.getKey(isDynamicIsland), 18.0f)
    ) }
    var artistSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(MediaControlSpKey.ELM_ARTIST_SIZE.getKey(isDynamicIsland), 12.0f)
    ) }
    var timeSize by remember { mutableFloatStateOf(
        SafeSP.getFloat(MediaControlSpKey.ELM_TIME_SIZE.getKey(isDynamicIsland), 12.0f)
    ) }
    var thumbStyle by remember { mutableIntStateOf(
        SafeSP.getInt(MediaControlSpKey.ELM_THUMB_STYLE.getKey(isDynamicIsland), 0)
    ) }
    var progressStyle by remember { mutableIntStateOf(
        SafeSP.getInt(MediaControlSpKey.ELM_PROGRESS_STYLE.getKey(isDynamicIsland), 0)
    ) }
    var progressWidth by remember { mutableFloatStateOf(
        SafeSP.getFloat(MediaControlSpKey.ELM_PROGRESS_WIDTH.getKey(isDynamicIsland), 4.0f)
    ) }
    var progressRound by remember { mutableStateOf(
        SafeSP.getBoolean(MediaControlSpKey.ELM_PROGRESS_ROUND.getKey(isDynamicIsland), false)
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
                ambientLight = ambientLight,
                ambientLightOpt = ambientLightOpt,
                lytAlbum = lytAlbum,
                lytLeftActions = lytLeftActions,
                lytActionsOrder = lytActionsOrder,
                lytHideTime = lytHideTime,
                lytHideSeamless = lytHideSeamless,
                lytHeaderMargin = lytHeaderMargin,
                lytHeaderPadding = lytHeaderPadding,
                albumShadow = albumShadow,
                modifyTextSize = modifyTextSize,
                titleSize = titleSize,
                artistSize = artistSize,
                timeSize = timeSize,
                thumbStyle = thumbStyle,
                progressStyle = progressStyle,
                progressWidth = progressWidth,
                progressRound = progressRound
            )
        }
        item {
            TabRow(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 6.dp),
                tabs = tabRowItems,
                selectedTabIndex = tabRowSelected
            ) {
                tabRowSelected = it
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_STYLE",
            visible = (tabRowSelected == 0)
        ) {
            DropDownPreference(
                title = stringResource(R.string.media_bg_custom),
                entries = backgroundStyleEntries,
                key = MediaControlSpKey.BACKGROUND_STYLE.getKey(isDynamicIsland)
            ) {
                backgroundStyle = it
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_STYLE_0_EXT",
            last = true,
            visible = (tabRowSelected == 0 && backgroundStyle == 0)
        ) {
            if (isDynamicIsland) {
                DropDownPreference(
                    title = stringResource(R.string.media_bg_ambient_light),
                    entries = backgroundAmbientLightEntries,
                    key = Pref.Key.DynamicIsland.MediaControl.AMBIENT_LIGHT_TYPE
                ) {
                    ambientLightType = it
                    ambientLight = (it != 1)
                    if (it == 0) {
                        ambientLightOpt = false
                    } else if (it == 2) {
                        ambientLightOpt = SafeSP.getBoolean(MediaControlSpKey.AMBIENT_LIGHT_OPT.getKey(isDynamicIsland))
                    }
                }
            } else {
                SwitchPreference(
                    title = stringResource(R.string.media_bg_ambient_light),
                    summary = stringResource(R.string.media_bg_ambient_light_tips),
                    key = Pref.Key.SystemUI.MediaControl.AMBIENT_LIGHT,
                    defValue = false
                ) {
                    ambientLight = it
                }
                SwitchPreference(
                    title = stringResource(R.string.media_bg_always_dark),
                    summary = stringResource(R.string.media_bg_always_dark_tips),
                    key = Pref.Key.SystemUI.MediaControl.ALWAYS_DARK
                )
            }
            AnimatedVisibility ((isDynamicIsland && ambientLightType == 2) || (!isDynamicIsland && ambientLight)) {
                SwitchPreference(
                    title = stringResource(R.string.media_bg_ambient_light_opt),
                    summary = stringResource(R.string.media_bg_ambient_light_opt_tips),
                    key = MediaControlSpKey.AMBIENT_LIGHT_OPT.getKey(isDynamicIsland),
                ) {
                    ambientLightOpt = it
                }
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_STYLE_NOT_0_EXT",
            last = hintAdvancedTextures,
            visible = (tabRowSelected == 0 && backgroundStyle != 0)
        ) {
            SwitchPreference(
                title = stringResource(R.string.media_bg_color_anim),
                summary = stringResource(R.string.media_bg_color_anim_tips),
                key = MediaControlSpKey.USE_ANIM.getKey(isDynamicIsland),
                defValue = true
            )
            AnimatedVisibility(
                backgroundStyle == 2
            ) {
                SeekBarPreference(
                    title = stringResource(R.string.media_bg_blur_radius),
                    key = MediaControlSpKey.BLUR_RADIUS.getKey(isDynamicIsland),
                    defValue = 10,
                    min = 1,
                    max = 20,
                    format = "%d%%"
                ) {
                    blurRadius = it
                }
            }
            AnimatedVisibility(
                backgroundStyle == 4
            ) {
                SwitchPreference(
                    title = stringResource(R.string.media_bg_auto_color_reverse),
                    key = MediaControlSpKey.ALLOW_REVERSE.getKey(isDynamicIsland)
                ) {
                    allowReverse = it
                }
            }
        }
        itemAnimated(
            key = "BACKGROUND_STYLE_HINT",
            visible = (tabRowSelected == 0 && !hintAdvancedTextures)
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
                text = stringResource(R.string.media_hint_advanced_textures),
                closeable = true
            ) {
                hintAdvancedTextures = true
                SafeSP.putAny(Pref.Key.Hints.MEDIA_ADVANCED_TEXTURES, true)
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_LYT",
            visible = (tabRowSelected == 1)
        ) {
            DropDownPreference(
                title = stringResource(R.string.media_lyt_album),
                entries = albumStyleEntries,
                key = MediaControlSpKey.LYT_ALBUM.getKey(isDynamicIsland)
            ) {
                lytAlbum = it
            }
            SwitchPreference(
                title = stringResource(R.string.media_lyt_left_actions),
                key = MediaControlSpKey.LYT_LEFT_ACTIONS.getKey(isDynamicIsland)
            ) {
                lytLeftActions = it
            }
            DropDownPreference(
                title = stringResource(R.string.media_lyt_actions_order),
                entries = actionsOrderEntries,
                key = MediaControlSpKey.LYT_ACTIONS_ORDER.getKey(isDynamicIsland)
            ) {
                lytActionsOrder = it
            }
            SwitchPreference(
                title = stringResource(R.string.media_lyt_hide_time),
                summary = stringResource(R.string.media_lyt_hide_time_tips),
                key = MediaControlSpKey.LYT_HIDE_TIME.getKey(isDynamicIsland)
            ) {
                lytHideTime = it
            }
            SwitchPreference(
                title = stringResource(R.string.media_lyt_hide_seamless),
                summary = stringResource(R.string.media_lyt_hide_seamless_tips),
                key = MediaControlSpKey.LYT_HIDE_SEAMLESS.getKey(isDynamicIsland)
            ) {
                lytHideSeamless = it
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_LYT_EXT",
            last = true,
            visible = (tabRowSelected == 1)
        ) {
            SeekBarPreference(
                title = stringResource(R.string.media_lyt_header_margin),
                key = MediaControlSpKey.LYT_HEADER_MARGIN.getKey(isDynamicIsland),
                defValue = 21.0f,
                min = 0.0f,
                max = 48.0f,
                format = "%.2f dp"
            ) {
                lytHeaderMargin = it
            }
            SeekBarPreference(
                title = stringResource(R.string.media_lyt_header_padding),
                key = MediaControlSpKey.LYT_HEADER_PADDING.getKey(isDynamicIsland),
                defValue = 4.0f,
                min = 0.0f,
                max = 36.0f,
                format = "%.2f dp"
            ) {
                lytHeaderPadding = it
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_ELM",
            visible = (tabRowSelected == 2)
        ) {
            if (!isDynamicIsland) {
                SwitchPreference(
                    title = stringResource(R.string.media_elm_album_shadow),
                    key = Pref.Key.SystemUI.MediaControl.ELM_ALBUM_SHADOW,
                    defValue = true
                ) {
                    albumShadow = !isDynamicIsland && it
                }
            }
            SwitchPreference(
                title = stringResource(R.string.media_elm_album_flip_anim),
                summary = stringResource(R.string.media_elm_album_flip_anim_tips),
                key = Pref.Key.SystemUI.MediaControl.ELM_ALBUM_FLIP,
                defValue = true
            )
            SwitchPreference(
                title = stringResource(R.string.media_elm_text_size),
                key = MediaControlSpKey.ELM_TEXT_SIZE.getKey(isDynamicIsland)
            ) {
                modifyTextSize = it
            }
            AnimatedVisibility(
                modifyTextSize
            ) {
                Column {
                    SeekBarPreference(
                        title = stringResource(R.string.media_elm_text_size_title),
                        key = MediaControlSpKey.ELM_TITLE_SIZE.getKey(isDynamicIsland),
                        defValue = 18.0f,
                        min = 0.5f,
                        max = 36.0f,
                        format = "%.2f sp"
                    ) {
                        titleSize = it
                    }
                    SeekBarPreference(
                        title = stringResource(R.string.media_elm_text_size_artist),
                        key = MediaControlSpKey.ELM_ARTIST_SIZE.getKey(isDynamicIsland),
                        defValue = 12.0f,
                        min = 0.5f,
                        max = 24.0f,
                        format = "%.2f sp"
                    ) {
                        artistSize = it
                    }
                    SeekBarPreference(
                        title = stringResource(R.string.media_elm_text_size_time),
                        key = MediaControlSpKey.ELM_TIME_SIZE.getKey(isDynamicIsland),
                        defValue = 12.0f,
                        min = 0.5f,
                        max = 24.0f,
                        format = "%.2f sp"
                    ) {
                        timeSize = it
                    }
                }
            }
        }
        itemPreferenceGroup(
            key = "BACKGROUND_ELM_PROGRESS_BAR",
            last = true,
            visible = (tabRowSelected == 2)
        ) {
            DropDownPreference(
                title = stringResource(R.string.media_elm_thumb_style),
                entries = thumbStyleEntries,
                key = MediaControlSpKey.ELM_THUMB_STYLE.getKey(isDynamicIsland)
            ) {
                thumbStyle = it
            }
            SwitchPreference(
                title = stringResource(R.string.media_elm_fix_thumb_crop),
                summary = stringResource(R.string.media_elm_fix_thumb_crop_tips),
                key = MediaControlSpKey.FIX_THUMB_CROPPED.getKey(isDynamicIsland)
            )
            DropDownPreference(
                title = stringResource(R.string.media_elm_prog_style),
                entries = progressStyleEntries,
                key = MediaControlSpKey.ELM_PROGRESS_STYLE.getKey(isDynamicIsland)
            ) {
                progressStyle = it
            }
            AnimatedVisibility(
                progressStyle == 1
            ) {
                Column {
                    SeekBarPreference(
                        title = stringResource(R.string.media_elm_prog_width),
                        key = MediaControlSpKey.ELM_PROGRESS_WIDTH.getKey(isDynamicIsland),
                        defValue = 4.0f,
                        min = 0.5f,
                        max = 14.0f,
                        format = "%.2f dp"
                    ) {
                        progressWidth = it
                    }
                    SwitchPreference(
                        title = stringResource(R.string.media_elm_prog_round),
                        key = MediaControlSpKey.ELM_PROGRESS_ROUND.getKey(isDynamicIsland),
                    ) {
                        progressRound = it
                    }
                }
            }
        }
    }
}