package dev.lackluster.mihelper.app.screen.others

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.layout.HyperXSheet
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.EditableTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.ValuePosition
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.hyperx.ui.preference.core.PreferenceActions
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.model.SearchEngineItem
import dev.lackluster.mihelper.data.preference.Preferences
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class SearchEngineDraftState(
    val enabled: Boolean = false,
    val channelNo: String = "",
    val showIcon: Boolean = true,
    val searchUrl: String = "",
    val iconUrl: String = "",
    val titleLzhCN: String = "",
    val titleLzhTW: String = "",
    val titleLenUS: String = "",
    val titleLboCN: String = "",
    val titleLugCN: String = ""
) {
    fun validate(): Int? {
        if (!enabled) return null
        if (!searchUrl.contains("{searchTerms}")) return R.string.others_search_custom_search_url
        if (showIcon && iconUrl.isEmpty()) return R.string.others_search_custom_icon_url
        if (titleLzhCN.isEmpty() && titleLzhTW.isEmpty() &&
            titleLenUS.isEmpty() && titleLboCN.isEmpty() && titleLugCN.isEmpty()
        ) {
            return R.string.others_search_custom_title
        }
        return null
    }

    fun commit(actions: PreferenceActions) {
        actions.update(Preferences.Search.ENABLE_CUSTOM_SEARCH_ENGINE, enabled)
        val item = SearchEngineItem(
            searchEngineName = "custom",
            channelNo = channelNo,
            showIcon = showIcon,
            searchUrl = searchUrl,
            iconUrl = iconUrl,
            titleLzhCN = titleLzhCN,
            titleLzhTW = titleLzhTW,
            titleLenUS = titleLenUS,
            titleLboCN = titleLboCN,
            titleLugCN = titleLugCN
        )
        actions.update(Preferences.Search.CUSTOM_SEARCH_ENGINE_ENTITY, SearchEngineItem.encodeToString(item))
    }
}

@Composable
fun SearchCustomEngineSheet(
    show: Boolean,
    onAction: (OthersUIAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val appSettingsActions = LocalPreferenceActions.current

    var draftState by remember(show) {
        val enabled = appSettingsActions.get(Preferences.Search.ENABLE_CUSTOM_SEARCH_ENGINE)
        val entityString = appSettingsActions.get(Preferences.Search.CUSTOM_SEARCH_ENGINE_ENTITY)
        val item = entityString.takeIf { it.isNotEmpty() }?.let { SearchEngineItem.decodeFromString(it) }

        mutableStateOf(
            SearchEngineDraftState(
                enabled = enabled,
                channelNo = item?.channelNo ?: "",
                showIcon = item?.showIcon ?: true,
                searchUrl = item?.searchUrl ?: "",
                iconUrl = item?.iconUrl ?: "",
                titleLzhCN = item?.titleLzhCN ?: "",
                titleLzhTW = item?.titleLzhTW ?: "",
                titleLenUS = item?.titleLenUS ?: "",
                titleLboCN = item?.titleLboCN ?: "",
                titleLugCN = item?.titleLugCN ?: ""
            )
        )
    }

    val cardColor = CardDefaults.cardColors(containerColor = MiuixTheme.colorScheme.secondaryContainer)
    val resetCardColor = CardDefaults.cardColors(containerColor = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_bg))

    HyperXSheet(
        show = show,
        title = stringResource(R.string.others_search_custom_search_engine),
        onDismissRequest = onDismissRequest,
        allowDismiss = false,
        onNegativeButton = { dismiss -> dismiss() },
        onPositiveButton = { dismiss ->
            val errorResId = draftState.validate()
            if (errorResId != null) {
                val prefix = R.string.common_invalid_input.toUiText()
                val reason = errorResId.toUiText()
                onAction(OthersUIAction.ShowToast(UiText.Combined(prefix, reason, "\n"), true))
                return@HyperXSheet
            }
            draftState.commit(appSettingsActions)
            dismiss()
        },
    ) {
        itemPreferenceGroup(
            key = "SEARCH_MASTER",
            cardColors = cardColor
        ) {
            SwitchPreference(
                title = stringResource(R.string.others_search_custom_search_engine),
                checked = draftState.enabled,
                onCheckedChange = { draftState = draftState.copy(enabled = it) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.others_search_custom_url,
            cardColors = cardColor
        ) {
            EditTextPreference(
                title = stringResource(R.string.others_search_custom_search_url),
                text = draftState.searchUrl,
                valuePosition = ValuePosition.Summary,
                dialogMessage = stringResource(R.string.others_search_custom_search_url_tips),
                dialogHint = stringResource(R.string.others_search_custom_search_url_hint),
                onTextChange = { draftState = draftState.copy(searchUrl = it) }
            )
            EditableTextPreference(
                title = stringResource(R.string.others_search_custom_channel),
                summary = stringResource(R.string.others_search_custom_channel_tips),
                text = draftState.channelNo,
                onTextChange = { draftState = draftState.copy(channelNo = it) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.others_search_custom_icon,
            cardColors = cardColor
        ) {
            SwitchPreference(
                title = stringResource(R.string.others_search_custom_show_icon),
                checked = draftState.showIcon,
                onCheckedChange = { draftState = draftState.copy(showIcon = it) }
            )
            AnimatedVisibility(visible = draftState.showIcon) {
                EditTextPreference(
                    title = stringResource(R.string.others_search_custom_icon_url),
                    text = draftState.iconUrl,
                    valuePosition = ValuePosition.Hidden,
                    dialogMessage = stringResource(R.string.others_search_custom_icon_url_tips),
                    dialogHint = stringResource(R.string.others_search_custom_icon_url_hint),
                    onTextChange = { draftState = draftState.copy(iconUrl = it) }
                )
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.others_search_custom_title,
            cardColors = cardColor
        ) {
            EditableTextPreference(
                title = stringResource(R.string.others_search_custom_title_zh_cn),
                hint = stringResource(R.string.others_search_custom_title_hint),
                text = draftState.titleLzhCN,
                onTextChange = { draftState = draftState.copy(titleLzhCN = it) }
            )
            EditableTextPreference(
                title = stringResource(R.string.others_search_custom_title_zh_tw),
                hint = stringResource(R.string.others_search_custom_title_hint),
                text = draftState.titleLzhTW,
                onTextChange = { draftState = draftState.copy(titleLzhTW = it) }
            )
            EditableTextPreference(
                title = stringResource(R.string.others_search_custom_title_en_us),
                hint = stringResource(R.string.others_search_custom_title_hint),
                text = draftState.titleLenUS,
                onTextChange = { draftState = draftState.copy(titleLenUS = it) }
            )
            EditableTextPreference(
                title = stringResource(R.string.others_search_custom_title_bo_cn),
                hint = stringResource(R.string.others_search_custom_title_hint),
                text = draftState.titleLboCN,
                onTextChange = { draftState = draftState.copy(titleLboCN = it) }
            )
            EditableTextPreference(
                title = stringResource(R.string.others_search_custom_title_ug_cn),
                hint = stringResource(R.string.others_search_custom_title_hint),
                text = draftState.titleLugCN,
                onTextChange = { draftState = draftState.copy(titleLugCN = it) }
            )
        }

        itemPreferenceGroup(
            key = "SEARCH_RESET",
            position = ItemPosition.Last,
            cardColors = resetCardColor
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        draftState = SearchEngineDraftState()
                        onAction(OthersUIAction.ShowToast(R.string.dialog_done.toUiText()))
                    }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(R.string.others_search_custom_reset),
                    fontSize = MiuixTheme.textStyles.headline1.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_fg)
                )
            }
        }
    }
}