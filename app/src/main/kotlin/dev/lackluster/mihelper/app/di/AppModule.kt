package dev.lackluster.mihelper.app.di

import dev.lackluster.mihelper.app.manager.AppEnvironmentManager
import dev.lackluster.mihelper.app.provider.AppPreferenceActions
import dev.lackluster.mihelper.app.repository.FontRepository
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.repository.StackedMobileRepository
import dev.lackluster.mihelper.app.screen.settings.ModuleSettingsViewModel
import dev.lackluster.mihelper.app.screen.system.SystemFrameworkViewModel
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.IconDetailViewModel
import dev.lackluster.mihelper.app.screen.systemui.icon.position.IconPositionViewModel
import dev.lackluster.mihelper.app.screen.systemui.icon.detail.StackedMobileViewModel
import dev.lackluster.mihelper.app.screen.systemui.media.MediaControlViewModel
import dev.lackluster.mihelper.app.screen.systemui.statusbar.StatusBarFontViewModel
import dev.lackluster.mihelper.app.manager.XposedServiceManager
import dev.lackluster.mihelper.app.state.AppEnvViewModel
import dev.lackluster.mihelper.app.state.GlobalUIViewModel
import dev.lackluster.mihelper.app.utils.RemoteFileStore
import dev.lackluster.mihelper.app.utils.RemotePreferenceStore
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::XposedServiceManager) { createdAtStart() }
    singleOf(::AppEnvironmentManager) { createdAtStart() }

    singleOf(::RemotePreferenceStore)
    singleOf(::RemoteFileStore)

    singleOf(::GlobalPreferencesRepository) { createdAtStart() }
    singleOf(::FontRepository)
    singleOf(::StackedMobileRepository) { createdAtStart() }
    singleOf(::AppPreferenceActions)

    viewModelOf(::GlobalUIViewModel)
    viewModelOf(::AppEnvViewModel)

    viewModelOf(::ModuleSettingsViewModel)
    viewModelOf(::SystemFrameworkViewModel)
    viewModelOf(::StatusBarFontViewModel)
    viewModelOf(::IconDetailViewModel)
    viewModelOf(::StackedMobileViewModel)
    viewModelOf(::IconPositionViewModel)
    viewModelOf(::MediaControlViewModel)
}