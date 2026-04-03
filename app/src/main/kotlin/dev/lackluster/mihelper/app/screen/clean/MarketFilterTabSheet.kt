package dev.lackluster.mihelper.app.screen.clean

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.layout.HyperXSheet
import dev.lackluster.hyperx.ui.preference.CheckboxPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.hyperx.ui.preference.core.PreferenceActions
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.preference.Preferences
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class MarketFilterDraftState(
    val filterTab: Boolean = false,
    val filterTabOthers: Boolean = false,
    val ignoreRestrict: Boolean = false,
    val showTabHome: Boolean = true,
    val showTabGame: Boolean = true,
    val showTabRank: Boolean = true,
    val showTabAgent: Boolean = true,
    val showTabAppAssemble: Boolean = true,
    val showTabMiniGame: Boolean = true,
    val showTabMine: Boolean = true
) {
    fun validate(): Int? {
        if (ignoreRestrict) {
            val visibleTabs = listOf(
                showTabHome, showTabGame, showTabRank, showTabAgent,
                showTabAppAssemble, showTabMiniGame, showTabMine
            ).count { it }

            if (visibleTabs < 2) {
                return R.string.cleaner_market_filter_tab_warning
            }
        }
        return null
    }

    fun commit(actions: PreferenceActions) {
        actions.update(Preferences.Market.ENABLE_FILTER_TAB, filterTab)
        actions.update(Preferences.Market.HIDE_TAB_OTHERS, filterTabOthers)
        actions.update(Preferences.Market.FILTER_TAB_IGNORE_RESTRICT, ignoreRestrict)
        actions.update(Preferences.Market.HIDE_TAB_HOME, ignoreRestrict && !showTabHome)
        actions.update(Preferences.Market.HIDE_TAB_GAME, !showTabGame)
        actions.update(Preferences.Market.HIDE_TAB_RANK, !showTabRank)
        actions.update(Preferences.Market.HIDE_TAB_AGENT, !showTabAgent)
        actions.update(Preferences.Market.HIDE_TAB_APP_ASSEMBLE, !showTabAppAssemble)
        actions.update(Preferences.Market.HIDE_TAB_MINI_GAME, !showTabMiniGame)
        actions.update(Preferences.Market.HIDE_TAB_MINE, ignoreRestrict && !showTabMine)
    }
}

@Composable
fun MarketFilterTabSheet(
    show: Boolean,
    showToast: (UiText, Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    val appSettingsActions = LocalPreferenceActions.current

    var draftState by remember(show) {
        mutableStateOf(
            MarketFilterDraftState(
                filterTab = appSettingsActions.get(Preferences.Market.ENABLE_FILTER_TAB),
                filterTabOthers = appSettingsActions.get(Preferences.Market.HIDE_TAB_OTHERS),
                ignoreRestrict = appSettingsActions.get(Preferences.Market.FILTER_TAB_IGNORE_RESTRICT),
                showTabHome = !appSettingsActions.get(Preferences.Market.HIDE_TAB_HOME),
                showTabGame = !appSettingsActions.get(Preferences.Market.HIDE_TAB_GAME),
                showTabRank = !appSettingsActions.get(Preferences.Market.HIDE_TAB_RANK),
                showTabAgent = !appSettingsActions.get(Preferences.Market.HIDE_TAB_AGENT),
                showTabAppAssemble = !appSettingsActions.get(Preferences.Market.HIDE_TAB_APP_ASSEMBLE),
                showTabMiniGame = !appSettingsActions.get(Preferences.Market.HIDE_TAB_MINI_GAME),
                showTabMine = !appSettingsActions.get(Preferences.Market.HIDE_TAB_MINE)
            )
        )
    }

    val cardColor = CardDefaults.cardColors(containerColor = MiuixTheme.colorScheme.secondaryContainer)

    HyperXSheet(
        show = show,
        title = stringResource(R.string.cleaner_market_filter_tab),
        onDismissRequest = onDismissRequest,
        allowDismiss = false,
        onNegativeButton = { dismiss -> dismiss() },
        onPositiveButton = { dismiss ->
            val warnResId = draftState.validate()
            if (warnResId != null) {
                showToast(warnResId.toUiText(), true)
            }
            draftState.commit(appSettingsActions)
            dismiss()
        },
    ) {
        itemPreferenceGroup(
            titleRes = R.string.common_general,
            cardColors = cardColor
        ) {
            SwitchPreference(
                title = stringResource(R.string.cleaner_market_filter_tab),
                summary = stringResource(R.string.cleaner_market_filter_tab_tips),
                checked = draftState.filterTab,
                onCheckedChange = { draftState = draftState.copy(filterTab = it) }
            )
            SwitchPreference(
                title = stringResource(R.string.cleaner_market_filter_unknown_tabs),
                summary = stringResource(R.string.cleaner_market_filter_unknown_tabs_tips),
                checked = draftState.filterTabOthers,
                onCheckedChange = { draftState = draftState.copy(filterTabOthers = it) }
            )
            SwitchPreference(
                title = stringResource(R.string.cleaner_market_ignore_restrict),
                checked = draftState.ignoreRestrict,
                onCheckedChange = { draftState = draftState.copy(ignoreRestrict = it) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.cleaner_market_visible_tabs,
            position = ItemPosition.Last,
            cardColors = cardColor
        ) {
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_home),
                checked = draftState.showTabHome,
                enabled = draftState.ignoreRestrict,
                onCheckedChange = { draftState = draftState.copy(showTabHome = it) }
            )
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_game),
                checked = draftState.showTabGame,
                onCheckedChange = { draftState = draftState.copy(showTabGame = it) }
            )
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_rank),
                checked = draftState.showTabRank,
                onCheckedChange = { draftState = draftState.copy(showTabRank = it) }
            )
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_agent),
                checked = draftState.showTabAgent,
                onCheckedChange = { draftState = draftState.copy(showTabAgent = it) }
            )
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_app_assemble),
                checked = draftState.showTabAppAssemble,
                onCheckedChange = { draftState = draftState.copy(showTabAppAssemble = it) }
            )
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_mini_game),
                checked = draftState.showTabMiniGame,
                onCheckedChange = { draftState = draftState.copy(showTabMiniGame = it) }
            )
            CheckboxPreference(
                title = stringResource(R.string.cleaner_market_tab_mine),
                checked = draftState.showTabMine,
                enabled = draftState.ignoreRestrict,
                onCheckedChange = { draftState = draftState.copy(showTabMine = it) }
            )
        }
    }
}