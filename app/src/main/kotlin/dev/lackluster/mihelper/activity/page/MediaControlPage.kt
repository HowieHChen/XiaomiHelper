package dev.lackluster.mihelper.activity.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    var backgroundStyle by remember { mutableIntStateOf(
        SafeSP.getInt(Pref.Key.SystemUI.MediaControl.BACKGROUND_STYLE, 0)
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