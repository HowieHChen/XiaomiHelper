package dev.lackluster.mihelper.app.screen.systemui.media.tabs

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.core.utils.toDecimalString
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences

private val albumStyleOptions = listOf(
    DropDownOption(0, R.string.media_lyt_album_default),
    DropDownOption(1, R.string.media_lyt_album_hide_app),
    DropDownOption(2, R.string.media_lyt_album_gone),
)
private val actionsOrderOptions = listOf(
    DropDownOption(0, R.string.media_lyt_actions_order_default),
    DropDownOption(1, R.string.media_lyt_actions_order_custom_right),
    DropDownOption(2, R.string.media_lyt_actions_order_play_left),
)

fun LazyListScope.layoutTabContent(
    isVisible: Boolean,
    isIsland: Boolean,
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "LYT_GENERAL"
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.get(isIsland),
            title = stringResource(R.string.media_lyt_album),
            options = albumStyleOptions,
        )
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_UNLOCK_ACTION,
            title = stringResource(R.string.media_lyt_unlock_action),
            summary = stringResource(R.string.media_lyt_unlock_action_tips),
        )
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_LEFT_ACTIONS.get(isIsland),
            title = stringResource(R.string.media_lyt_left_actions),
        )
        DropDownPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_ACTIONS_ORDER.get(isIsland),
            title = stringResource(R.string.media_lyt_actions_order),
            options = actionsOrderOptions,
        )
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_TIME.get(isIsland),
            title = stringResource(R.string.media_lyt_hide_time),
            summary = stringResource(R.string.media_lyt_hide_time_tips),
        )
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_SEAMLESS.get(isIsland),
            title = stringResource(R.string.media_lyt_hide_seamless),
            summary = stringResource(R.string.media_lyt_hide_seamless_tips),
        )
    }
    itemPreferenceGroup(
        key = "LYT_EXT",
        position = ItemPosition.Last
    ) {
        SeekBarPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_TOP_MARGIN.get(isIsland),
            title = stringResource(R.string.media_lyt_header_margin),
            valueFormatter = { "${it.toDecimalString()}dp" },
            min = 0.0f,
            max = 48.0f,
        )
        SeekBarPreference(
            key = Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_PADDING.get(isIsland),
            title = stringResource(R.string.media_lyt_header_padding),
            valueFormatter = { "${it.toDecimalString()}dp" },
            min = 0.0f,
            max = 36.0f,
        )
    }
}