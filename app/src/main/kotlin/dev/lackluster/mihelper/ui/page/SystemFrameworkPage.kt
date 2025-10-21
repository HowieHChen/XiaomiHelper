package dev.lackluster.mihelper.ui.page

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.AlertDialog
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.EditTextDialog
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.component.RebootMenuItem
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.ShellUtils

@Composable
fun SystemFrameworkPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    var checkFontScale by remember { mutableStateOf(false) }
    var currentFontScale by remember { mutableFloatStateOf(0.0f) }
    val dialogFontScaleFailedVisibility = remember { mutableStateOf(false) }
    val dialogFontScaleVisibility = remember { mutableStateOf(false) }

    LaunchedEffect(checkFontScale) {
        try {
            ShellUtils.tryExec(
                "settings get system font_scale",
                useRoot = true,
                checkSuccess = true
            ).let { result ->
                val newScale = result.successMsg.toFloatOrNull()
                if (result.exitCode == 0 && newScale != null) {
                    currentFontScale = newScale
                } else {
                    dialogFontScaleFailedVisibility.value = true
                }
            }
        } catch (tout: Throwable) {
            Toast.makeText(
                context,
                tout.message,
                Toast.LENGTH_LONG
            ).show()
            dialogFontScaleFailedVisibility.value = true
        }
    }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_android),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        actions = {
            RebootMenuItem(
                appName = stringResource(R.string.scope_android),
                appPkg = Scope.ANDROID
            )
        }
    ) {
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_android_display),
                first = true
            ) {
                TextPreference(
                    title = stringResource(R.string.android_display_temp_font_scale),
                    summary = stringResource(R.string.android_display_temp_font_scale_tips),
                    value = String.format(Locale.current.platformLocale, "%.2f", currentFontScale)
                ) {
                    dialogFontScaleVisibility.value = true
                }
                TextPreference(
                    title = stringResource(R.string.android_display_font_scale),
                    summary = stringResource(R.string.android_display_font_scale_tips),
                    value = stringResource(
                        if (SafeSP.getBoolean(Pref.Key.Android.FONT_SCALE)) {
                            R.string.common_on
                        } else {
                            R.string.common_off
                        }
                    )
                ) {
                    navController.navigateTo(Pages.DIALOG_FONT_SCALE)
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_android_freeform)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.android_freeform_restriction),
                    summary = stringResource(R.string.android_freeform_restriction_tips),
                    key = Pref.Key.Android.DISABLE_FREEFORM_RESTRICT
                )
                SwitchPreference(
                    title = stringResource(R.string.android_freeform_allow_more),
                    summary = stringResource(R.string.android_freeform_allow_more_tips),
                    key = Pref.Key.Android.ALLOW_MORE_FREEFORM
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_android_others),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.android_others_force_dark),
                    summary = stringResource(R.string.android_others_force_dark_tips),
                    key = Pref.Key.Android.BLOCK_FORCE_DARK_WHITELIST
                )
            }
        }
    }
    AlertDialog(
        visibility = dialogFontScaleFailedVisibility,
        title = stringResource(R.string.dialog_error),
        message = stringResource(R.string.android_display_temp_font_scale_fail_msg)
    )
    EditTextDialog(
        visibility = dialogFontScaleVisibility,
        title = stringResource(R.string.android_display_temp_font_scale),
        message = stringResource(R.string.android_display_temp_font_scale_msg),
        value = String.format(Locale.current.platformLocale, "%.2f", currentFontScale)
    ) {
        val newScale = it.toFloatOrNull()
        if (newScale != null && newScale in 0.5f..<2.5f) {
            try {
                ShellUtils.tryExec("settings put system font_scale $newScale", useRoot = true)
            } catch (tout: Throwable) {
                Toast.makeText(
                    context,
                    tout.message,
                    Toast.LENGTH_LONG
                ).show()
                dialogFontScaleFailedVisibility.value = true
            }
        } else {
            dialogFontScaleFailedVisibility.value = true
        }
    }
}