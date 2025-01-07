package dev.lackluster.mihelper.activity.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Pref

@Composable
fun SystemFrameworkPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_android),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_android_freeform),
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.android_freeform_restriction),
                    summary = stringResource(R.string.android_freeform_restriction_tips),
                    key = Pref.Key.Android.DISABLE_FREEFORM_RESTRICT
                )
                SwitchPreference(
                    title = stringResource(R.string.android_freeform_allow_more),
                    summary = stringResource(R.string.android_freeform_allow_more_tips),
                    key = Pref.Key.Android.ALLOW_MORE_FREEFORM
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_android_others),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.android_others_force_dark),
                    summary = stringResource(R.string.android_others_force_dark_tips),
                    key = Pref.Key.Android.BLOCK_FORCE_DARK_WHITELIST
                )
            }
        }
    }
}