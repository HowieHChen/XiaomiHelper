package dev.lackluster.mihelper.app.screen.systemui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.data.Route
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences

@Composable
fun NotifLayoutOptPage() {
    val navigator = LocalNavigator.current

    NotifLayoutOptPageContent(
        onNavigateTo = {
            navigator.push(it)
        }
    )
}

@Composable
private fun NotifLayoutOptPageContent(
    onNavigateTo: (Route) -> Unit
) {
    val isOptEnabled = rememberPreferenceState(Preferences.SystemUI.NotifCenter.ENABLE_LAYOUT_RANK_OPT)

    HyperXPage(
        title = stringResource(R.string.systemui_notif_lr_opt),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI, Scope.SYSTEM_UI_PLUGIN),
            )
        }
    ) {
        itemPreferenceGroup(key = "OPT_GENERAL") {
            SwitchPreference(
                title = stringResource(R.string.systemui_notif_lr_opt),
                summary = stringResource(R.string.systemui_notif_lr_opt_tips),
                checked = isOptEnabled.value,
                onCheckedChange = { isOptEnabled.value = it },
            )
        }
        itemPreferenceGroup(
            key = "OPT_DETAIL",
            position = ItemPosition.Last,
        ) {
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.LR_OPT_HIDE_SECTION_HEADER,
                title = stringResource(R.string.systemui_notif_lr_hide_section_header),
                summary = stringResource(R.string.systemui_notif_lr_hide_section_header_tips),
                enabled = isOptEnabled.value,
            )
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.LR_OPT_HIDE_SECTION_GAP,
                title = stringResource(R.string.systemui_notif_lr_hide_section_gap),
                summary = stringResource(R.string.systemui_notif_lr_hide_section_gap_tips),
                enabled = isOptEnabled.value,
            )
            val isRerankEnabled = rememberPreferenceState(Preferences.SystemUI.NotifCenter.LR_OPT_RERANK)
            SwitchPreference(
                title = stringResource(R.string.systemui_notif_lr_rerank),
                summary = stringResource(R.string.systemui_notif_lr_rerank_tips),
                enabled = isOptEnabled.value,
                checked = isRerankEnabled.value,
                onCheckedChange = { isRerankEnabled.value = it }
            )
            val isPinAppNotificationsOn = rememberPreferenceState(Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ENABLED)
            AnimatedVisibility(isRerankEnabled.value) {
                TextPreference(
                    title = stringResource(R.string.systemui_notif_lr_pinned_apps),
                    summary = stringResource(R.string.systemui_notif_lr_pinned_apps_tips),
                    enabled = isOptEnabled.value,
                    value = stringResource(if (isPinAppNotificationsOn.value) R.string.common_on else R.string.common_off),
                    onClick = { onNavigateTo(Route.NotifPinnedApps) },
                )
            }
        }
    }
}
