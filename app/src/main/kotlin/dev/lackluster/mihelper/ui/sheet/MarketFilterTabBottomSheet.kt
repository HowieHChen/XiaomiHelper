package dev.lackluster.mihelper.ui.sheet

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.icon.ImmersionClose
import dev.lackluster.hyperx.compose.icon.ImmersionConfirm
import dev.lackluster.hyperx.compose.preference.CheckboxPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.extra.SuperBottomSheet
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.theme.LocalDismissState
import top.yukonga.miuix.kmp.theme.MiuixTheme

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun MarketFilterTabBottomSheet(
    show: MutableState<Boolean>
) {
    val context = LocalContext.current

    var spValueFilterTab by remember(show.value) { mutableStateOf(SafeSP.getBoolean(Pref.Key.Market.FILTER_TAB, false)) }
    var spValueFilterTabOthers by remember(show.value) { mutableStateOf(SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_OTHERS, false)) }
    var spValueIgnoreRestrict by remember(show.value) { mutableStateOf(SafeSP.getBoolean(Pref.Key.Market.FILTER_TAB_IGNORE_RESTRICT, false)) }
    var showTabHome by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_HOME, false)) }
    var showTabGame by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_GAME, false)) }
    var showTabRank by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_RANK, false)) }
    var showTabAgent by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_AGENT, false)) }
    var showTabAppAssemble by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE, false)) }
    var showTabMiniGame by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_MINI_GAME, false)) }
    var showTabMine by remember(show.value) { mutableStateOf(!SafeSP.getBoolean(Pref.Key.Market.HIDE_TAB_MINE, false)) }

    SuperBottomSheet(
        show = show.value,
        title = stringResource(R.string.cleaner_market_filter_tab),
        allowDismiss = false,
        startAction = {
            val dismiss = LocalDismissState.current
            IconButton(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(40.dp),
                onClick = { dismiss?.invoke() }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.ImmersionClose,
                    contentDescription = "Close",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        },
        endAction = {
            val dismiss = LocalDismissState.current
            IconButton(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp),
                onClick = {
                    SafeSP.putAny(Pref.Key.Market.FILTER_TAB, spValueFilterTab)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_OTHERS, spValueFilterTabOthers)
                    SafeSP.putAny(Pref.Key.Market.FILTER_TAB_IGNORE_RESTRICT, spValueIgnoreRestrict)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_HOME, spValueIgnoreRestrict && !showTabHome)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_GAME, !showTabGame)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_RANK, !showTabRank)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_AGENT, !showTabAgent)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE, !showTabAppAssemble)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_MINI_GAME, !showTabMiniGame)
                    SafeSP.putAny(Pref.Key.Market.HIDE_TAB_MINE, spValueIgnoreRestrict && !showTabMine)
                    if (spValueIgnoreRestrict) {
                        var visibleTabs = 0
                        if (showTabHome) visibleTabs++
                        if (showTabGame) visibleTabs++
                        if (showTabRank) visibleTabs++
                        if (showTabAgent) visibleTabs++
                        if (showTabAppAssemble) visibleTabs++
                        if (showTabMiniGame) visibleTabs++
                        if (showTabMine) visibleTabs++
                        if (visibleTabs < 2) {
                            Toast.makeText(context, context.getString(R.string.cleaner_market_filter_tab_warning), Toast.LENGTH_SHORT).show()
                        }
                    }
                    dismiss?.invoke()
                }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.ImmersionConfirm,
                    contentDescription = "Confirm",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        },
        onDismissRequest = {
            show.value = false
        },
        insideMargin = DpSize(0.dp, 12.dp),
        backgroundColor = MiuixTheme.colorScheme.surface,
    ) {
        LazyColumn {
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
                        title = stringResource(R.string.cleaner_market_tab_mini_game),
                        defValue = showTabMiniGame
                    ) {
                        showTabMiniGame = it
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
            item {
                Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 12.dp))
            }
        }
    }
}
