package dev.lackluster.mihelper.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavEntry
import dev.lackluster.hyperx.core.HyperXActivity
import dev.lackluster.hyperx.ui.layout.HyperXAppLayout
import dev.lackluster.mihelper.data.Route
import dev.lackluster.mihelper.app.screen.MainPage
import dev.lackluster.mihelper.app.screen.AboutPage
import dev.lackluster.mihelper.app.screen.EmptyPage
import dev.lackluster.mihelper.app.screen.MiuiHomePage
import dev.lackluster.mihelper.app.screen.SecurityCenterPage
import dev.lackluster.mihelper.app.screen.clean.CleanMasterPage
import dev.lackluster.mihelper.app.screen.settings.ModuleSettingsPage
import dev.lackluster.mihelper.app.screen.system.SystemFrameworkPage
import dev.lackluster.mihelper.app.state.GlobalUIViewModel
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.mihelper.app.provider.AppPreferenceActions
import dev.lackluster.mihelper.app.screen.MenuPage
import dev.lackluster.mihelper.app.screen.others.OthersPage
import dev.lackluster.mihelper.app.screen.systemui.SystemUIPage
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.IconDetailPage
import dev.lackluster.mihelper.app.screen.systemui.icon.position.IconPositionPage
import dev.lackluster.mihelper.app.screen.systemui.icon.IconTunerPage
import dev.lackluster.mihelper.app.screen.systemui.media.MediaControlPage
import dev.lackluster.mihelper.app.screen.systemui.statusbar.StatusBarClockPage
import dev.lackluster.mihelper.app.screen.systemui.statusbar.StatusBarFontPage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : HyperXActivity() {

    @Composable
    override fun AppContent() {
        val globalUiVm: GlobalUIViewModel = koinViewModel()
        val uiConfig by globalUiVm.configFlow.collectAsState()

        val appPreferenceActions: AppPreferenceActions = koinInject()

        CompositionLocalProvider(
            LocalPreferenceActions provides appPreferenceActions
        ) {
            HyperXAppLayout(
                config = uiConfig,
                primaryContent = { MainPage() },
                emptyContent = { EmptyPage() },
                customEntryProvider = { key ->
                    NavEntry(key) {
                        when (key) {
                            is Route.ModuleSettings -> ModuleSettingsPage()
                            is Route.SystemUI -> SystemUIPage()
                            is Route.SystemFramework -> SystemFrameworkPage()
                            is Route.MiuiHome -> MiuiHomePage()
                            is Route.CleanMaster -> CleanMasterPage()
                            is Route.SecurityCenter -> SecurityCenterPage()
                            is Route.Others -> OthersPage()
                            is Route.About -> AboutPage()
                            is Route.Menu -> MenuPage()
//                            is Route.DevUITest -> UITestPage(navigator, adjustPadding, mode)
                            is Route.StatusBarFont -> StatusBarFontPage()
                            is Route.StatusBarClock -> StatusBarClockPage()
                            is Route.IconTuner -> IconTunerPage()
                            is Route.IconDetail -> IconDetailPage()
                            is Route.NotifMediaControl -> MediaControlPage(false)
                            is Route.IslandMediaControl -> MediaControlPage(true)
                            is Route.StatusBarIconPosition -> IconPositionPage()
//                            is Route.DevUITest2 -> MediaActionResizePage(navigator, adjustPadding, "MediaActionResizePage", mode = mode)
                            else -> {}
                        }
                    }
                }
            )
        }
    }
}
