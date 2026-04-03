package dev.lackluster.mihelper.app.screen.systemui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.layout.HyperXSheet
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.preference.Preferences
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun NotifLayoutOptSheet(
    show: Boolean,
    onDismissRequest: () -> Unit
) {
    val cardColor = CardDefaults.cardColors(containerColor = MiuixTheme.colorScheme.secondaryContainer)
    val isOptEnabled = rememberPreferenceState(Preferences.SystemUI.NotifCenter.ENABLE_LAYOUT_RANK_OPT)

    HyperXSheet(
        show = show,
        title = stringResource(R.string.systemui_notif_lr_opt),
        onDismissRequest = onDismissRequest,
        allowDismiss = true,
        onNegativeButton = null,
        onPositiveButton = null,
    ) {
        itemPreferenceGroup(
            key = "OPT_GENERAL",
            cardColors = cardColor,
        ) {
            SwitchPreference(
                title = stringResource(R.string.systemui_notif_lr_opt),
                summary = stringResource(R.string.systemui_notif_lr_opt_tips),
                checked = isOptEnabled.value,
                onCheckedChange = { isOptEnabled.value = it }
            )
        }
        itemPreferenceGroup(
            key = "OPT_DETAIL",
            cardColors = cardColor,
            position = ItemPosition.Last
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
            SwitchPreference(
                key = Preferences.SystemUI.NotifCenter.LR_OPT_RERANK,
                title = stringResource(R.string.systemui_notif_lr_rerank),
                summary = stringResource(R.string.systemui_notif_lr_rerank_tips),
                enabled = isOptEnabled.value,
            )
        }
    }
}