package dev.lackluster.mihelper.app.screen.others

import android.content.ComponentName
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.compose.AnimatedColumn
import dev.lackluster.mihelper.app.utils.showToast
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.utils.Device

sealed interface OthersUIAction {
    data class ShowToast(val message: UiText, val long: Boolean = false) : OthersUIAction
    object OpenCellularNetworkSettings : OthersUIAction
    object OpenSearchCustomEngineSheet : OthersUIAction
}

private val searchEngineOptions = listOf(
    DropDownOption(0, R.string.search_engine_default),
    DropDownOption(1, R.string.search_engine_baidu),
    DropDownOption(2, R.string.search_engine_sogou),
    DropDownOption(3, R.string.search_engine_bing),
    DropDownOption(4, R.string.search_engine_google),
    DropDownOption(5, R.string.search_engine_custom)
)

@Composable
fun OthersPage() {
    val context = LocalContext.current
    val appSettingsActions = LocalPreferenceActions.current
    val showCustomSearchEngineSheet = remember { mutableStateOf(false) }

    val customSearchEngineEnabled = remember {
        mutableStateOf(appSettingsActions.get(Preferences.Search.ENABLE_CUSTOM_SEARCH_ENGINE))
    }

    val onAction: (OthersUIAction) -> Unit = { action ->
        when (action) {
            is OthersUIAction.ShowToast -> {
                context.showToast(action.message.asString(context), action.long)
            }
            OthersUIAction.OpenCellularNetworkSettings -> {
                context.let {
                    try {
                        context.startActivity(
                            Intent().apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                component = ComponentName(
                                    "com.xiaomi.phone",
                                    "com.xiaomi.phone.settings.development.CellularNetworkActivity"
                                )
                            }
                        )
                    } catch (t: Throwable) {
                        context.showToast(t.message ?: "Unknown Error", true)
                    }
                }
            }
            OthersUIAction.OpenSearchCustomEngineSheet -> {
                showCustomSearchEngineSheet.value = true
            }
        }
    }

    OthersPageContent(
        customSearchEngineEnabled = customSearchEngineEnabled.value,
        onAction = onAction
    )

    SearchCustomEngineSheet(
        show = showCustomSearchEngineSheet.value,
        onAction = onAction,
        onDismissRequest = {
            showCustomSearchEngineSheet.value = false
            customSearchEngineEnabled.value = appSettingsActions.get(Preferences.Search.ENABLE_CUSTOM_SEARCH_ENGINE)
        }
    )
}

@Composable
private fun OthersPageContent(
    customSearchEngineEnabled: Boolean,
    onAction: (OthersUIAction) -> Unit
) {
    HyperXPage(
        title = stringResource(R.string.page_others)
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_aiengine,
            position = ItemPosition.First
        ) {
            SwitchPreference(
                key = Preferences.AiEngine.OPEN_LINK_WITH_CUSTOM_BROWSER,
                title = stringResource(R.string.others_aiengine_copy_link_island_browser),
                summary = stringResource(R.string.others_aiengine_copy_link_island_browser_tips),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_miai
        ) {
            SwitchPreference(
                key = Preferences.MiAi.HIDE_WATERMARK,
                title = stringResource(R.string.others_miai_hide_watermark),
            )
            val miAiSearchUseBrowser = rememberPreferenceState(Preferences.MiAi.SEARCH_USE_BROWSER)
            SwitchPreference(
                title = stringResource(R.string.search_use_browser),
                checked = miAiSearchUseBrowser.value,
                onCheckedChange = { miAiSearchUseBrowser.value = it }
            )
            AnimatedColumn(miAiSearchUseBrowser.value) {
                val miAiSearchEngine = rememberPreferenceState(Preferences.MiAi.SEARCH_ENGINE)
                DropDownPreference(
                    title = stringResource(R.string.search_engine),
                    value = miAiSearchEngine.value,
                    options = searchEngineOptions,
                    onValueChange = { miAiSearchEngine.value = it}
                )
                AnimatedVisibility(miAiSearchEngine.value == 5) {
                    EditTextPreference(
                        key = Preferences.MiAi.CUSTOM_SEARCH_URL,
                        title = stringResource(R.string.search_engine_custom_url),
                        dialogMessage = stringResource(R.string.search_engine_custom_url_toast) + "\nhttps://example.com/s?q=%s",
                        isValueValid = { it.isBlank() || it.contains("%s") },
                        valuePosition = ValuePosition.Summary
                    )
                }
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_mimirror,
        ) {
            SwitchPreference(
                key = Preferences.MiMirror.CONTINUE_ALL_TASKS,
                title = stringResource(R.string.others_mimirror_all_app),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_search
        ) {
            val searchMoreEngines = rememberPreferenceState(Preferences.Search.MORE_SEARCH_ENGINE)
            SwitchPreference(
                title = stringResource(R.string.others_search_more_search_engines),
                summary = stringResource(R.string.others_search_more_search_engines_tips),
                checked = searchMoreEngines.value,
                onCheckedChange = { searchMoreEngines.value = it },
            )
            AnimatedVisibility(searchMoreEngines.value) {
                TextPreference(
                    title = stringResource(R.string.others_search_custom_search_engine),
                    value = stringResource(if (customSearchEngineEnabled) R.string.common_on else R.string.common_off),
                    onClick = { onAction(OthersUIAction.OpenSearchCustomEngineSheet) },
                )
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_taplus
        ) {
            val taplusSearchUseBrowser = rememberPreferenceState(Preferences.Taplus.SEARCH_USE_BROWSER)
            SwitchPreference(
                title = stringResource(R.string.search_use_browser),
                checked = taplusSearchUseBrowser.value,
                onCheckedChange = { taplusSearchUseBrowser.value = it }
            )
            AnimatedColumn(taplusSearchUseBrowser.value) {
                val taplusSearchEngine = rememberPreferenceState(Preferences.Taplus.SEARCH_ENGINE)
                DropDownPreference(
                    title = stringResource(R.string.search_engine),
                    value = taplusSearchEngine.value,
                    options = searchEngineOptions,
                    onValueChange = { taplusSearchEngine.value = it}
                )
                AnimatedVisibility(taplusSearchEngine.value == 5) {
                    EditTextPreference(
                        key = Preferences.Taplus.CUSTOM_SEARCH_URL,
                        title = stringResource(R.string.search_engine_custom_url),
                        dialogMessage = stringResource(R.string.search_engine_custom_url_toast) + "\nhttps://example.com/s?q=%s",
                        isValueValid = { it.isBlank() || it.contains("%s") },
                        valuePosition = ValuePosition.Summary
                    )
                }
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_settings
        ) {
            SwitchPreference(
                key = Preferences.Settings.SHOW_GOOGLE_ENTRY,
                title = stringResource(R.string.others_settings_show_google),
            )
            TextPreference(title = stringResource(R.string.others_settings_cellular_debug)) {
                onAction(OthersUIAction.OpenCellularNetworkSettings) // 副作用剥离！
            }
            AnimatedVisibility(Device.isPad) {
                SwitchPreference(
                    key = Preferences.Settings.UNLOCK_TAPLUS_FOR_PAD,
                    title = stringResource(R.string.others_settings_unlock_taplus_for_pad),
                )
            }
            SwitchPreference(
                key = Preferences.Settings.QUICK_PER_OVERLAY,
                title = stringResource(R.string.others_settings_quick_per_overlay),
                summary = stringResource(R.string.others_settings_quick_per_tips),
            )
            SwitchPreference(
                key = Preferences.Settings.QUICK_PER_INSTALL_SOURCE,
                title = stringResource(R.string.others_settings_quick_per_install),
                summary = stringResource(R.string.others_settings_quick_per_tips),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_others_updater,
            position = ItemPosition.Last
        ) {
            SwitchPreference(
                key = Preferences.Updater.BLOCK_AUTO_UPDATE_DIALOG,
                title = stringResource(R.string.others_updater_block_dialog),
            )
            SwitchPreference(
                key = Preferences.Updater.DISABLE_VALIDATION,
                title = stringResource(R.string.others_updater_disable_validation),
                summary = stringResource(R.string.others_updater_disable_validation_tips),
            )
        }
    }
}