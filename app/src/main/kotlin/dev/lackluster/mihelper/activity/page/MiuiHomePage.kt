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
fun MiuiHomePage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_miui_home),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_home_recent)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.home_recent_dismiss_anim),
                    key = Pref.Key.MiuiHome.RECENT_CARD_ANIM
                )
            }
        }
    }
}