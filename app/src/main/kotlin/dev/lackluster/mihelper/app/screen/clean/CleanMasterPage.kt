package dev.lackluster.mihelper.app.screen.clean

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
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.mihelper.app.component.AppRestartPreferenceItem
import dev.lackluster.mihelper.app.utils.jumpToAppDetailsSettings
import dev.lackluster.mihelper.app.utils.showToast
import dev.lackluster.mihelper.app.component.InnerHorizontalDivider
import dev.lackluster.mihelper.app.widget.preference.DropDownOption
import dev.lackluster.mihelper.app.widget.preference.DropDownPreference
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.Version
import dev.lackluster.mihelper.data.preference.Preferences

private sealed interface CleanMasterUIAction {
    data class OpenAppDetails(val packageName: String) : CleanMasterUIAction
    object OpenMarketFilterSheet : CleanMasterUIAction
}

private val PackageNameRegex = Regex("^([A-Za-z][A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$")

private val customInstallSourceOptions = listOf(
    DropDownOption(0, R.string.cleaner_package_install_source_disabled),
    DropDownOption(1, R.string.cleaner_package_install_source_file),
    DropDownOption(2, R.string.cleaner_package_install_source_market),
    DropDownOption(3, R.string.cleaner_package_install_source_custom),
)

@Composable
fun CleanMasterPage() {
    val context = LocalContext.current
    val appSettingsActions = LocalPreferenceActions.current

    val marketFilterSheetVisibility = remember { mutableStateOf(false) }
    val isMarketFilterTabOn = remember {
        mutableStateOf(appSettingsActions.get(Preferences.Market.ENABLE_FILTER_TAB))
    }

    val onAction: (CleanMasterUIAction) -> Unit = { action ->
        when (action) {
            is CleanMasterUIAction.OpenAppDetails -> context.jumpToAppDetailsSettings(action.packageName)
            CleanMasterUIAction.OpenMarketFilterSheet -> marketFilterSheetVisibility.value = true
        }
    }

    CleanMasterPageContent(
        isMarketFilterTabOn = isMarketFilterTabOn.value,
        onAction = onAction
    )

    MarketFilterTabSheet(
        show = marketFilterSheetVisibility.value,
        showToast = { message, long ->
            context.showToast(message.asString(context), long)
        },
        onDismissRequest = {
            marketFilterSheetVisibility.value = false
            isMarketFilterTabOn.value = appSettingsActions.get(Preferences.Market.ENABLE_FILTER_TAB)
        }
    )
}

@Composable
private fun CleanMasterPageContent(
    isMarketFilterTabOn: Boolean,
    onAction: (CleanMasterUIAction) -> Unit
) {
    HyperXPage(
        title = stringResource(R.string.page_cleaner)
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_browser,
        ) {
            AppRestartPreferenceItem(
                packageName = Scope.BROWSER,
                title = stringResource(R.string.cleaner_common_restart_app, stringResource(R.string.scope_browser)),
                verifiedVersion = Version.BROWSER,
                onFallbackAction = { onAction(CleanMasterUIAction.OpenAppDetails(Scope.BROWSER)) }
            )
            InnerHorizontalDivider()
            SwitchPreference(
                key = Preferences.Browser.AD_BLOCKER,
                title = stringResource(R.string.cleaner_common_ad_blocker),
            )
            SwitchPreference(
                key = Preferences.Browser.SKIP_SPLASH,
                title = stringResource(R.string.cleaner_common_skip_splash),
                summary = stringResource(R.string.cleaner_common_skip_splash_tips),
            )
            SwitchPreference(
                key = Preferences.Browser.SHOW_SUG_SWITCH_ENTRY,
                title = stringResource(R.string.cleaner_browser_show_sug_switch),
                summary = stringResource(R.string.cleaner_browser_show_sug_switch_tips),
            )
            SwitchPreference(
                key = Preferences.Browser.HIDE_HOMEPAGE_TOP_BAR,
                title = stringResource(R.string.cleaner_browser_hide_homepage_topbar),
                summary = stringResource(R.string.cleaner_browser_hide_homepage_topbar_tips),
            )
            SwitchPreference(
                key = Preferences.Browser.BLOCK_DIALOG,
                title = stringResource(R.string.cleaner_browser_block_dialog),
                summary = stringResource(R.string.cleaner_browser_block_dialog_tips),
            )
            SwitchPreference(
                key = Preferences.Browser.DEBUG_MODE,
                title = stringResource(R.string.cleaner_browser_debug_mode),
                summary = stringResource(R.string.cleaner_browser_debug_mode_tips),
            )
            SwitchPreference(
                key = Preferences.Browser.SWITCH_ENV,
                title = stringResource(R.string.cleaner_browser_switch_env),
                summary = stringResource(R.string.cleaner_browser_switch_env_tips),
            )
            SwitchPreference(
                key = Preferences.Browser.BLOCK_UPDATE,
                title = stringResource(R.string.cleaner_browser_disable_update),
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_download,
        ) {
            SwitchPreference(
                key = Preferences.DownloadUI.HIDE_XL,
                title = stringResource(R.string.cleaner_downloadui_hide_xl)
            )
            SwitchPreference(
                key = Preferences.Download.FUCK_XL,
                title = stringResource(R.string.cleaner_download_fuck_xl)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_incallui,
        ) {
            SwitchPreference(
                key = Preferences.InCallUI.HIDE_CRBT,
                title = stringResource(R.string.cleaner_incallui_hide_crbt)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_market,
        ) {
            AppRestartPreferenceItem(
                packageName = Scope.MARKET,
                title = stringResource(R.string.cleaner_common_restart_app, stringResource(R.string.scope_market)),
                verifiedVersion = Version.MARKET,
                onFallbackAction = { onAction(CleanMasterUIAction.OpenAppDetails(Scope.MARKET)) }
            )
            InnerHorizontalDivider()
            SwitchPreference(
                key = Preferences.Market.AD_BLOCKER,
                title = stringResource(R.string.cleaner_common_ad_blocker)
            )
            SwitchPreference(
                key = Preferences.Market.SKIP_SPLASH,
                title = stringResource(R.string.cleaner_common_skip_splash),
                summary = stringResource(R.string.cleaner_common_skip_splash_tips)
            )
            SwitchPreference(
                key = Preferences.Market.BLOCK_UPDATE_DIALOG,
                title = stringResource(R.string.cleaner_market_block_update_dailog),
                summary = stringResource(R.string.cleaner_market_block_update_dailog_tips)
            )
            SwitchPreference(
                key = Preferences.Market.HIDE_APP_SECURITY,
                title = stringResource(R.string.cleaner_market_hide_app_security),
                summary = stringResource(R.string.cleaner_market_hide_app_security_tips)
            )
            SwitchPreference(
                key = Preferences.Market.TAB_BLUR,
                title = stringResource(R.string.cleaner_market_bottom_tab_blur),
                summary = stringResource(R.string.cleaner_market_bottom_tab_blur_tips)
            )
            SwitchPreference(
                key = Preferences.Market.DISABLE_CUSTOMIZE_ICON,
                title = stringResource(R.string.cleaner_market_disable_customize_icon),
                summary = stringResource(R.string.cleaner_market_disable_customize_icon_tips)
            )
            TextPreference(
                title = stringResource(R.string.cleaner_market_filter_tab),
                summary = stringResource(R.string.cleaner_market_filter_tab_tips),
                value = stringResource(if (isMarketFilterTabOn) R.string.common_on else R.string.common_off),
                onClick = { onAction(CleanMasterUIAction.OpenMarketFilterSheet) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_milink,
        ) {
            SwitchPreference(
                key = Preferences.MiLink.FUCK_HPPLAY,
                title = stringResource(R.string.cleaner_milink_fuck_hpplay)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_mms
        ) {
            SwitchPreference(
                key = Preferences.MMS.AD_BLOCKER,
                title = stringResource(R.string.cleaner_common_ad_blocker)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_music,
        ) {
            AppRestartPreferenceItem(
                packageName = Scope.MUSIC,
                title = stringResource(R.string.cleaner_common_restart_app, stringResource(R.string.scope_music)),
                verifiedVersion = Version.MUSIC,
                onFallbackAction = { onAction(CleanMasterUIAction.OpenAppDetails(Scope.MUSIC)) }
            )
            InnerHorizontalDivider()
            SwitchPreference(
                key = Preferences.Music.AD_BLOCKER,
                title = stringResource(R.string.cleaner_common_ad_blocker)
            )
            SwitchPreference(
                key = Preferences.Music.SKIP_SPLASH,
                title = stringResource(R.string.cleaner_common_skip_splash),
                summary = stringResource(R.string.cleaner_common_skip_splash_tips)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_KARAOKE,
                title = stringResource(R.string.cleaner_music_hide_karaoke),
                summary = stringResource(R.string.cleaner_music_hide_karaoke_tips)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_LONG_AUDIO,
                title = stringResource(R.string.cleaner_music_hide_long_audio),
                summary = stringResource(R.string.cleaner_music_hide_long_audio_tips)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_DISCOVER,
                title = stringResource(R.string.cleaner_music_hide_discover),
                summary = stringResource(R.string.cleaner_music_hide_discover_tips)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_MY_BANNER,
                title = stringResource(R.string.cleaner_music_hide_my_banner)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_MY_REC_PLAYLIST,
                title = stringResource(R.string.cleaner_music_hide_my_rec_playlist)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_FAV_NUM,
                title = stringResource(R.string.cleaner_music_hide_fav_num),
                summary = stringResource(R.string.cleaner_music_hide_fav_num_tips)
            )
            SwitchPreference(
                key = Preferences.Music.HIDE_LISTEN_COUNT,
                title = stringResource(R.string.cleaner_music_hide_listen_count),
                summary = stringResource(R.string.cleaner_music_hide_listen_count_tips)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_package,
        ) {
            AppRestartPreferenceItem(
                packageName = Scope.PACKAGE_INSTALLER,
                title = stringResource(R.string.cleaner_common_restart_app, stringResource(R.string.scope_package_installer)),
                verifiedVersion = Version.PACKAGE_INSTALLER,
                onFallbackAction = { onAction(CleanMasterUIAction.OpenAppDetails(Scope.PACKAGE_INSTALLER)) }
            )
            InnerHorizontalDivider()
            SwitchPreference(
                key = Preferences.PackageInstaller.REMOVE_ELEMENT,
                title = stringResource(R.string.cleaner_package_remove_element)
            )
            SwitchPreference(
                key = Preferences.PackageInstaller.DISABLE_RISK_CHECK,
                title = stringResource(R.string.cleaner_package_skip_risk_check)
            )
            SwitchPreference(
                key = Preferences.PackageInstaller.DISGUISE_NO_NETWORK,
                title = stringResource(R.string.cleaner_package_no_network)
            )
            SwitchPreference(
                key = Preferences.PackageInstaller.DISABLE_COUNT_CHECK,
                title = stringResource(R.string.cleaner_package_no_count_check)
            )
            SwitchPreference(
                key = Preferences.PackageInstaller.BLOCK_UPLOAD_INFO,
                title = stringResource(R.string.cleaner_package_block_upload_app_info)
            )
            val installSource = rememberPreferenceState(Preferences.PackageInstaller.CUSTOM_INSTALL_SOURCE)
            DropDownPreference(
                title = stringResource(R.string.cleaner_package_install_source),
                summary = stringResource(R.string.cleaner_package_install_source_tips),
                options = customInstallSourceOptions,
                value = installSource.value,
                onValueChange = { installSource.value = it }
            )
            AnimatedVisibility(installSource.value == 3) {
                val customPkgName = rememberPreferenceState(Preferences.PackageInstaller.INSTALL_SOURCE_PKG)
                EditTextPreference(
                    title = stringResource(R.string.cleaner_package_custom_install_source),
                    text = customPkgName.value,
                    valuePosition = ValuePosition.Summary,
                    onTextChange = {
                        if (it.matches(PackageNameRegex)) customPkgName.value = it
                    }
                )
            }
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_remote,
        ) {
            SwitchPreference(
                key = Preferences.RemoteController.AD_BLOCKER,
                title = stringResource(R.string.cleaner_common_ad_blocker)
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_cleaner_themes,
            position = ItemPosition.Last
        ) {
            SwitchPreference(
                key = Preferences.Themes.SKIP_SPLASH,
                title = stringResource(R.string.cleaner_common_skip_splash),
                summary = stringResource(R.string.cleaner_common_skip_splash_tips)
            )
        }
    }
}