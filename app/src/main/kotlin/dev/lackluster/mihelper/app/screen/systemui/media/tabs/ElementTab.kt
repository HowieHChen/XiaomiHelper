package dev.lackluster.mihelper.app.screen.systemui.media.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.core.utils.toDecimalString
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.media.MediaElementState
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences

private val thumbStyleOptions = listOf(
    DropDownOption(0, R.string.media_elm_thumb_style_default),
    DropDownOption(1, R.string.media_elm_thumb_style_gone),
    DropDownOption(2, R.string.media_elm_thumb_style_vbar),
)
private val progressStyleOptions = listOfNotNull(
    DropDownOption(0, R.string.media_elm_prog_style_default),
    DropDownOption(1, R.string.media_elm_prog_style_custom),
    DropDownOption(2, R.string.media_elm_prog_style_squiggly),
)

fun LazyListScope.elementTabContent(
    isVisible: Boolean,
    isIsland: Boolean,
    state: MediaElementState,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "ELM_GENERAL",
    ) {
        if (!isIsland) {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.NotifCenter.ELM_ALBUM_SHADOW,
                title = stringResource(R.string.media_elm_album_shadow),
            )
        }
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.ELM_ALBUM_FLIP,
            title = stringResource(R.string.media_elm_album_flip_anim),
            summary = stringResource(R.string.media_elm_album_flip_anim_tips),
        )
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.ELM_CUSTOM_TEXT_SIZE.get(isIsland),
            title = stringResource(R.string.media_elm_text_size),
        )
        AnimatedColumn(
            state.customTextSize
        ) {
            SeekBarPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_TITLE_SIZE.get(isIsland),
                title = stringResource(R.string.media_elm_text_size_title),
                valueFormatter = { "${it.toDecimalString()}sp" },
                min = 0.5f,
                max = 36.0f,
            )
            SeekBarPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_ARTIST_SIZE.get(isIsland),
                title = stringResource(R.string.media_elm_text_size_artist),
                valueFormatter = { "${it.toDecimalString()}sp" },
                min = 0.5f,
                max = 24.0f,
            )
            SeekBarPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_TIME_SIZE.get(isIsland),
                title = stringResource(R.string.media_elm_text_size_time),
                valueFormatter = { "${it.toDecimalString()}sp" },
                min = 0.5f,
                max = 24.0f,
            )
        }
    }
    itemPreferenceGroup(
        key = "ELM_PROGRESS",
        position = ItemPosition.Last,
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_STYLE.get(isIsland),
            title = stringResource(R.string.media_elm_prog_style),
            options = progressStyleOptions,
        )
        AnimatedVisibility(state.progressStyle in 0..1) {
            SeekBarPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_WIDTH.get(isIsland),
                title = stringResource(R.string.media_elm_prog_width),
                valueFormatter = { "${it.toDecimalString()}dp" },
                min = 0.5f,
                max = 14.0f,
            )
        }
        AnimatedVisibility(state.progressStyle == 1) {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_COMET.get(isIsland),
                title = stringResource(R.string.media_elm_prog_comet),
                summary = stringResource(R.string.media_elm_prog_comet_tips),
            )
        }
        AnimatedVisibility(state.progressStyle != 0) {
            DropDownPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_THUMB_STYLE.get(isIsland),
                title = stringResource(R.string.media_elm_thumb_style),
                options = thumbStyleOptions,
            )
        }
        AnimatedVisibility(state.progressStyle == 1 && state.thumbStyle == 1) {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_ROUND.get(isIsland),
                title = stringResource(R.string.media_elm_prog_round),
            )
        }
//            AnimatedVisibility(
//                progressStyle != 1
//            ) {
//                SwitchPreference(
//                    title = stringResource(R.string.media_elm_fix_thumb_crop),
//                    summary = stringResource(R.string.media_elm_fix_thumb_crop_tips),
//                    key = MediaControlSpKey.FIX_THUMB_CROPPED.getKey(isDynamicIsland)
//                )
//            }
    }
}