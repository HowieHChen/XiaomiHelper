package dev.lackluster.mihelper.app.screen.systemui.media.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.ui.component.Hint
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SeekBarPreference
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemAnimated
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.screen.systemui.media.MediaBackgroundState
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences

private val styleOptions = listOf(
    DropDownOption(0, R.string.media_bg_custom_default),
    DropDownOption(1, R.string.media_bg_custom_art),
    DropDownOption(2, R.string.media_bg_custom_blur),
    DropDownOption(3, R.string.media_bg_custom_radial_gradient),
    DropDownOption(4, R.string.media_bg_custom_linear_gradient),
)

private val ambientLightOptions = listOf(
    DropDownOption(0, R.string.media_bg_ambient_light_default),
    DropDownOption(1, R.string.media_bg_ambient_light_hidden),
    DropDownOption(2, R.string.media_bg_ambient_light_custom),
)

sealed interface BackgroundTabAction {
    object CloseAmbientLightHint : BackgroundTabAction
    object CloseStyleHint : BackgroundTabAction
}

fun LazyListScope.backgroundTabContent(
    isVisible: Boolean,
    isIsland: Boolean,
    state: MediaBackgroundState,
    showAmbientHint: Boolean,
    showStyleHint: Boolean,
    onAction: (BackgroundTabAction) -> Unit
) {
    if (!isVisible) return

    itemPreferenceGroup(
        key = "BG_STYLE"
    ) {
        DropDownPreference(
            key = Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(isIsland),
            title = stringResource(R.string.media_bg_custom),
            options = styleOptions,
        )
        if (!isIsland) {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.NotifCenter.BG_NOTIF_CORNER,
                title = stringResource(R.string.media_bg_use_notif_corner_radius),
            )
        }
    }
    itemPreferenceGroup(
        key = "BG_STYLE_0_EXT",
        visible = state.style == 0,
        position = ItemPosition.Last
    ) {
        if (isIsland) {
            DropDownPreference(
                key = Preferences.SystemUI.MediaControl.DynamicIsland.BG_AMBIENT_LIGHT_TYPE,
                title = stringResource(R.string.media_bg_ambient_light),
                options = ambientLightOptions,
            )
        } else {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.NotifCenter.BG_AMBIENT_LIGHT,
                title = stringResource(R.string.media_bg_ambient_light),
                summary = stringResource(R.string.media_bg_ambient_light_tips),
            )
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.NotifCenter.BG_ALWAYS_DARK,
                title = stringResource(R.string.media_bg_always_dark),
                summary = stringResource(R.string.media_bg_always_dark_tips),
            )
        }
        AnimatedVisibility(state.ambientLight && state.ambientLightType == 2) {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.get(isIsland),
                title = stringResource(R.string.media_bg_ambient_light_opt),
                summary = stringResource(R.string.media_bg_ambient_light_opt_tips),
            )
        }
    }
    itemPreferenceGroup(
        key = "BG_STYLE_NOT_0_EXT",
        visible = state.style != 0,
        position = ItemPosition.Last
    ) {
        SwitchPreference(
            key = Preferences.SystemUI.MediaControl.Shared.BG_COLOR_ANIM.get(isIsland),
            title = stringResource(R.string.media_bg_use_anim),
            summary = stringResource(R.string.media_bg_use_anim_tips),
        )
        AnimatedVisibility(state.style == 2) {
            SeekBarPreference(
                key = Preferences.SystemUI.MediaControl.Shared.BG_BLUR_RADIUS.get(isIsland),
                title = stringResource(R.string.media_bg_blur_radius),
                min = 1,
                max = 20,
                valueFormatter = { "${it}%" }
            )
        }
        AnimatedVisibility(state.style == 4) {
            SwitchPreference(
                key = Preferences.SystemUI.MediaControl.Shared.BG_ALLOW_REVERSE.get(isIsland),
                title = stringResource(R.string.media_bg_auto_color_reverse)
            )
        }
    }
    itemAnimated(
        key = "BG_STYLE_HINT_AMBIENT_LIGHT",
        visible = showAmbientHint && state.style == 0
    ) {
        Hint(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
            text = stringResource(R.string.media_hint_ambient_lighting),
            closeable = true,
            onClose = { onAction(BackgroundTabAction.CloseAmbientLightHint) }
        )
    }
    itemAnimated(
        key = "BG_STYLE_HINT",
        visible = showStyleHint
    ) {
        Hint(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
            text = stringResource(R.string.media_hint_advanced_textures),
            closeable = true,
            onClose = { onAction(BackgroundTabAction.CloseStyleHint) }
        )
    }
}