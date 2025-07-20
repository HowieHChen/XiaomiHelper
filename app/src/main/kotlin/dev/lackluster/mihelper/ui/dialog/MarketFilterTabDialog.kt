package dev.lackluster.mihelper.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.component.FullScreenDialog
import dev.lackluster.hyperx.compose.preference.CheckboxPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.ui.MainActivity

@Composable
fun MarketFilterTabDialog(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    var spValueFilterTab by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Market.FILTER_TAB, false)) }
    var spValueFilterTabOthers by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_OTHERS, false)) }
    var spValueIgnoreRestrict by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Market.FILTER_TAB_IGNORE_RESTRICT, false)) }
    var showTabHome by remember { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_HOME, false)) }
    var showTabGame by remember { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_GAME, false)) }
    var showTabRank by remember { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_RANK, false)) }
    var showTabAgent by remember { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_AGENT, false)) }
    var showTabAppAssemble by remember { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE, false)) }
    var showTabMine by remember { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_MINE, false)) }

    FullScreenDialog(
        navController,
        adjustPadding,
        stringResource(R.string.cleaner_market_filter_tab),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        onPositiveButton = {
            SafeSP.putAny(Pref.Key.Market.FILTER_TAB, spValueFilterTab)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_OTHERS, spValueFilterTabOthers)
            SafeSP.putAny(Pref.Key.Market.FILTER_TAB_IGNORE_RESTRICT, spValueIgnoreRestrict)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_HOME, spValueIgnoreRestrict && !showTabHome)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_GAME, !showTabGame)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_RANK, !showTabRank)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_AGENT, !showTabAgent)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE, !showTabAppAssemble)
            SafeSP.putAny(Pref.Key.Market.HIDE_TAB_MINE, spValueIgnoreRestrict && !showTabMine)
            if (spValueIgnoreRestrict) {
                var visibleTabs = 0
                if (showTabHome) visibleTabs++
                if (showTabGame) visibleTabs++
                if (showTabRank) visibleTabs++
                if (showTabAgent) visibleTabs++
                if (showTabAppAssemble) visibleTabs++
                if (showTabMine) visibleTabs++
                if (visibleTabs < 2) {
                    Toast.makeText(context, context.getString(R.string.cleaner_market_filter_tab_warning), Toast.LENGTH_SHORT).show()
                }
            }
            navController.popBackStack()
        }
    ) {
        item {
            PreferenceGroup(
                first = true,
                title = stringResource(R.string.common_general)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_filter_tab),
                    summary = stringResource(R.string.cleaner_market_filter_tab_tips),
                    defValue = spValueFilterTab
                ) {
                    spValueFilterTab = it
                }
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_filter_unknown_tabs),
                    summary = stringResource(R.string.cleaner_market_filter_unknown_tabs_tips),
                    defValue = spValueFilterTabOthers
                ) {
                    spValueFilterTabOthers = it
                }
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_ignore_restrict),
                    defValue = spValueIgnoreRestrict
                ) {
                    spValueIgnoreRestrict = it
                }
            }
        }
        item {
            PreferenceGroup(
                last = true,
                title = stringResource(R.string.cleaner_market_visible_tabs)
            ) {
                CheckboxPreference(
                    title = stringResource(R.string.cleaner_market_tab_home),
                    defValue = showTabHome,
                    enabled = spValueIgnoreRestrict
                ) {
                    showTabHome = it
                }
                CheckboxPreference(
                    title = stringResource(R.string.cleaner_market_tab_game),
                    defValue = showTabGame
                ) {
                    showTabGame = it
                }
                CheckboxPreference(
                    title = stringResource(R.string.cleaner_market_tab_rank),
                    defValue = showTabRank
                ) {
                    showTabRank = it
                }
                CheckboxPreference(
                    title = stringResource(R.string.cleaner_market_tab_agent),
                    defValue = showTabAgent
                ) {
                    showTabAgent = it
                }
                CheckboxPreference(
                    title = stringResource(R.string.cleaner_market_tab_app_assemble),
                    defValue = showTabAppAssemble
                ) {
                    showTabAppAssemble = it
                }
                CheckboxPreference(
                    title = stringResource(R.string.cleaner_market_tab_mine),
                    defValue = showTabMine,
                    enabled = spValueIgnoreRestrict
                ) {
                    showTabMine = it
                }
            }
        }
    }
}