package dev.lackluster.mihelper.activity.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Info
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CleanMasterPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var visibilityCustomInstallSource by remember { mutableStateOf(SafeSP.getInt(Pref.Key.PackageInstaller.INSTALL_SOURCE) == 3) }

    val dropdownEntriesCustomInstallSource = listOf(
        DropDownEntry(stringResource(R.string.cleaner_package_install_source_disabled)),
        DropDownEntry(stringResource(R.string.cleaner_package_install_source_file)),
        DropDownEntry(stringResource(R.string.cleaner_package_install_source_market)),
        DropDownEntry(stringResource(R.string.cleaner_package_install_source_custom)),
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_cleaner),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_ad_blocker)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.scope_market),
                    key = Pref.Key.Market.AD_BLOCKER
                )
                SwitchPreference(
                    title = stringResource(R.string.scope_music),
                    key = Pref.Key.Music.AD_BLOCKER
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_privacy)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_privacy_block_upload_app),
                    summary = stringResource(R.string.cleaner_privacy_block_upload_app_tips),
                    key = Pref.Key.GuardProvider.BLOCK_UPLOAD_APP
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_privacy_block_ul_app_info),
                    summary = stringResource(R.string.cleaner_privacy_block_ul_app_info_tips),
                    key = Pref.Key.PackageInstaller.BLOCK_UPLOAD_INFO
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_skip_splash)
            ) {
                BasicComponent(
                    summary = stringResource(R.string.cleaner_skip_splash_tips),
                    leftAction = {
                        Image(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(28.dp),
                            imageVector = MiuixIcons.Info,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        )
                    }
                )
            }
            PreferenceGroup {
                SwitchPreference(
                    title = stringResource(R.string.scope_browser),
                    key = Pref.Key.Browser.SKIP_SPLASH
                )
                SwitchPreference(
                    title = stringResource(R.string.scope_market),
                    key = Pref.Key.Market.SKIP_SPLASH
                )
                SwitchPreference(
                    title = stringResource(R.string.scope_music),
                    key = Pref.Key.Music.SKIP_SPLASH
                )
                SwitchPreference(
                    title = stringResource(R.string.scope_security_center),
                    key = Pref.Key.SecurityCenter.SKIP_SPLASH
                )
                SwitchPreference(
                    title = stringResource(R.string.scope_themes),
                    key = Pref.Key.Themes.SKIP_SPLASH
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_browser)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_browser_remove_app_rec),
                    summary = stringResource(R.string.cleaner_browser_remove_app_rec_tips),
                    key = Pref.Key.Browser.REMOVE_APP_REC
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_download)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_downloadui_hide_xl),
                    key = Pref.Key.DownloadUI.HIDE_XL
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_download_fuck_xl),
                    key = Pref.Key.Download.FUCK_XL
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_market)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_block_update_dailog),
                    summary = stringResource(R.string.cleaner_market_block_update_dailog_tips),
                    key = Pref.Key.Market.BLOCK_UPDATE_DIALOG
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_hide_game),
                    summary = stringResource(R.string.cleaner_market_hide_game_tips),
                    key = Pref.Key.Market.HIDE_TAB_GAME
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_hide_rank),
                    summary = stringResource(R.string.cleaner_market_hide_rank_tips),
                    key = Pref.Key.Market.HIDE_TAB_RANK
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_market_hide_video),
                    summary = stringResource(R.string.cleaner_market_hide_video_tips),
                    key = Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_music)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_music_hide_long_audio),
                    summary = stringResource(R.string.cleaner_music_hide_long_audio_tips),
                    key = Pref.Key.Music.HIDE_LONG_AUDIO
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_music_hide_my_banner),
                    key = Pref.Key.Music.MY_HIDE_BANNER
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_music_hide_my_rec_playlist),
                    key = Pref.Key.Music.MY_HIDE_REC_PLAYLIST
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_package)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_package_remove_element),
                    key = Pref.Key.PackageInstaller.REMOVE_ELEMENT
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_package_skip_risk_check),
                    key = Pref.Key.PackageInstaller.DISABLE_RISK_CHECK
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_package_no_count_check),
                    key = Pref.Key.PackageInstaller.DISABLE_COUNT_CHECK
                )
                DropDownPreference(
                    title = stringResource(R.string.cleaner_package_install_source),
                    summary = stringResource(R.string.cleaner_package_install_source_tips),
                    entries = dropdownEntriesCustomInstallSource,
                    key = Pref.Key.PackageInstaller.INSTALL_SOURCE
                ) {
                    visibilityCustomInstallSource = (it == 3)
                }
                AnimatedVisibility(
                    visibilityCustomInstallSource
                ) {
                    EditTextPreference(
                        title = stringResource(R.string.cleaner_package_custom_install_source),
                        key = Pref.Key.PackageInstaller.SOURCE_PKG_NAME,
                        dataType = EditTextDataType.STRING,
                        isValueValid = { value ->
                            val string = value as String
                            string.matches(Regex("^([A-Za-z][A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$"))
                        },
                        valuePosition = ValuePosition.SUMMARY_VIEW
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(
                    if (Device.isPad) R.string.ui_title_cleaner_security_pad
                    else R.string.ui_title_cleaner_security
                )
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_lock_score),
                    summary = stringResource(R.string.cleaner_security_lock_score_tips),
                    key = Pref.Key.SecurityCenter.LOCK_SCORE
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_hide_red_dot),
                    summary = stringResource(R.string.cleaner_security_hide_red_dot_tips),
                    key = Pref.Key.SecurityCenter.HIDE_RED_DOT
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_hide_home_rec),
                    summary = stringResource(R.string.cleaner_security_hide_home_rec_tips),
                    key = Pref.Key.SecurityCenter.HIDE_HOME_REC
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_hide_home_common),
                    key = Pref.Key.SecurityCenter.HIDE_HOME_COMMON
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_hide_home_popular),
                    summary = stringResource(R.string.cleaner_security_hide_home_popular_tips),
                    key = Pref.Key.SecurityCenter.HIDE_HOME_POPULAR
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_disable_risk_app_notif),
                    key = Pref.Key.SecurityCenter.DISABLE_RISK_APP_NOTIF
                )
                SwitchPreference(
                    title = stringResource(R.string.cleaner_security_remove_report),
                    key = Pref.Key.SecurityCenter.REMOVE_REPORT
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_systemui)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_systemui_fuck_gesture),
                    key = Pref.Key.SystemUI.FUCK_GESTURES_DAT
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_cleaner_taplus)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.cleaner_taplus_hide_shop),
                    key = Pref.Key.Taplus.HIDE_SHOP
                )
            }
        }
    }
}