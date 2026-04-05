package dev.lackluster.mihelper.app.screen.systemui.media

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.layout.TabRow
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.screen.systemui.media.component.MediaControlCard
import dev.lackluster.mihelper.app.screen.systemui.media.tabs.BackgroundTabAction
import dev.lackluster.mihelper.app.screen.systemui.media.tabs.backgroundTabContent
import dev.lackluster.mihelper.app.screen.systemui.media.tabs.elementTabContent
import dev.lackluster.mihelper.app.screen.systemui.media.tabs.layoutTabContent
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import org.koin.androidx.compose.koinViewModel

private enum class MediaControlTab(@param:StringRes val titleResId: Int) {
    BACKGROUND(R.string.ui_title_media_bg),
    LAYOUT(R.string.ui_title_media_layout),
    ELEMENT(R.string.ui_title_media_elements),
}

@Composable
fun MediaControlPage(
    isIsland: Boolean = false,
    viewModel: MediaControlViewModel = koinViewModel()
) {
    val hapticFeedback = LocalHapticFeedback.current

    val state by (if (isIsland) viewModel.islandState else viewModel.normalState).collectAsState()
    var selectedTab by remember { mutableStateOf(MediaControlTab.BACKGROUND) }
    val tabs = MediaControlTab.entries

    val showBgAmbientLightHint = rememberPreferenceState(Preferences.HintState.MEDIA_AMBIENT_LIGHTING)
    val showBgStyleHint = rememberPreferenceState(Preferences.HintState.MEDIA_ADVANCED_TEXTURES)

    val onBackgroundAction: (BackgroundTabAction) -> Unit = { action ->
        when (action) {
            BackgroundTabAction.CloseAmbientLightHint -> showBgAmbientLightHint.value = false
            BackgroundTabAction.CloseStyleHint -> showBgStyleHint.value = false
        }
    }

    HyperXPage(
        title = stringResource(R.string.page_media_control_style),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_systemui),
                appPkg = arrayOf(Scope.SYSTEM_UI),
            )
        },
        fixedHeader = {
            MediaControlCard(
                isIsland = isIsland,
                state = state
            )
        }
    ) {
        item(
            key = "TAB_ROW"
        ) {
            TabRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                tabs = tabs.map { stringResource(it.titleResId) },
                selectedTabIndex = selectedTab.ordinal,
                onTabSelected = { index ->
                    selectedTab = tabs[index]
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                }
            )
        }
        backgroundTabContent(
            isVisible = selectedTab == MediaControlTab.BACKGROUND,
            isIsland = isIsland,
            state = state.background,
            showAmbientHint = showBgAmbientLightHint.value,
            showStyleHint = showBgStyleHint.value,
            onAction = onBackgroundAction
        )
        layoutTabContent(
            isVisible = selectedTab == MediaControlTab.LAYOUT,
            isIsland = isIsland
        )
        elementTabContent(
            isVisible = selectedTab == MediaControlTab.ELEMENT,
            isIsland = isIsland,
            state = state.element
        )
    }
}