package dev.lackluster.mihelper.activity

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.composable
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.HyperXApp
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.dialog.SearchCustomEngineDialog
import dev.lackluster.mihelper.activity.page.AboutPage
import dev.lackluster.mihelper.activity.page.CleanMasterPage
import dev.lackluster.mihelper.activity.page.MainPage
import dev.lackluster.mihelper.activity.page.MenuPage
import dev.lackluster.mihelper.activity.page.MiuiHomePage
import dev.lackluster.mihelper.activity.page.ModuleSettingsPage
import dev.lackluster.mihelper.activity.page.OthersPage
import dev.lackluster.mihelper.activity.page.SecurityCenterPage
import dev.lackluster.mihelper.activity.page.SystemUIPage
import dev.lackluster.mihelper.activity.page.UITestPage
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.ShellUtils
import dev.lackluster.mihelper.utils.factory.getSP
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.theme.MiuixTheme

class MainActivity : HyperXActivity() {
    companion object {
        val moduleActive: MutableState<Boolean> = mutableStateOf(false)
        val moduleEnabled: MutableState<Boolean> = mutableStateOf(false)
        val liteMode: MutableState<Boolean> = mutableStateOf(false)
        val blurEnabled: MutableState<Boolean> = mutableStateOf(true)
        val blurTintAlphaLight: MutableFloatState = mutableFloatStateOf(0.6f)
        val blurTintAlphaDark: MutableFloatState = mutableFloatStateOf(0.5f)
        val splitEnabled: MutableState<Boolean> = mutableStateOf(Device.isPad)
        val rootGranted: MutableState<Boolean> = mutableStateOf(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAndCheck()
    }

    @Composable
    override fun AppContent() {
        HyperXApp(
            autoSplitView = splitEnabled,
            mainPageContent = { navController, adjustPadding, mode ->
                MainPage(navController, adjustPadding, mode)
            },
            emptyPageContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val foregroundColor: Color
                    val backgroundColor: Color
                    MiuixTheme.colorScheme.onBackground.let {
                        if (it.luminance() >= 0.5f) {
                            foregroundColor = it.copy(alpha = 0.2f)
                            backgroundColor = it.copy(alpha = 0.12f)
                        }
                        else {
                            foregroundColor = it.copy(alpha = 0.1f)
                            backgroundColor = it.copy(alpha = 0.06f)
                        }
                    }
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.empty_page_background),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(backgroundColor),
                        contentScale = ContentScale.Crop
                    )
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.empty_page_foreground),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(foregroundColor),
                        contentScale = ContentScale.Inside
                    )
                }
            },
            otherPageBuilder = { navController, adjustPadding, mode ->
                composable(Pages.MENU) { MenuPage(navController, adjustPadding, mode) }
                composable(Pages.MODULE_SETTINGS) { ModuleSettingsPage(navController, adjustPadding) }
                composable(Pages.SYSTEM_UI) { SystemUIPage(navController, adjustPadding)}

                composable(Pages.MIUI_HOME) { MiuiHomePage(navController, adjustPadding, mode) }
                composable(Pages.CLEAN_MASTER) { CleanMasterPage(navController, adjustPadding, mode) }
                composable(Pages.SECURITY_CENTER) { SecurityCenterPage(navController, adjustPadding, mode) }

                composable(Pages.OTHERS) { OthersPage(navController, adjustPadding, mode) }
                composable(Pages.ABOUT) { AboutPage(navController, adjustPadding, mode) }
                composable(Pages.DEV_UI_TEST) { UITestPage(navController, adjustPadding, mode) }

                composable(Pages.DIALOG_SEARCH_CUSTOM_ENGINE) { SearchCustomEngineDialog(navController, adjustPadding, mode) }
            }
        )
    }

    private fun initAndCheck() {
        try {
            SafeSP.setSP(getSP(this))
            versionCompatible()
            moduleActive.value = true
            moduleEnabled.value = SafeSP.getBoolean(Pref.Key.Module.ENABLED, false)
            blurEnabled.value = SafeSP.getBoolean(Pref.Key.App.HAZE_BLUR, true)
            blurTintAlphaLight.floatValue =
                SafeSP.getInt(Pref.Key.App.HAZE_TINT_ALPHA_LIGHT, 60) / 100f
            blurTintAlphaDark.floatValue =
                SafeSP.getInt(Pref.Key.App.HAZE_TINT_ALPHA_LIGHT, 50) / 100f
            splitEnabled.value = SafeSP.getBoolean(Pref.Key.App.SPLIT_VIEW, Device.isPad)
            rootGranted.value = if (!SafeSP.getBoolean(Pref.Key.App.SKIP_ROOT_CHECK, false)) {
                try {
                    ShellUtils.tryExec(
                        "whoami",
                        useRoot = true,
                        checkSuccess = true
                    ).successMsg.trim().contentEquals("root")
                } catch (exception: Exception) {
                    false
                }
            } else {
                true
            }
        } catch (exception: SecurityException) {
            moduleActive.value = false
            moduleEnabled.value = false
            blurEnabled.value = true
            blurTintAlphaLight.floatValue = 0.6f
            blurTintAlphaDark.floatValue = 0.5f
            splitEnabled.value = Device.isPad
        }
    }

    private fun versionCompatible() {
        val spVersion = SafeSP.getInt(Pref.Key.Module.SP_VERSION, 0)
        if (spVersion < 1) {
            if (SafeSP.getString(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS_STR, "") == "") {
                val oldValue = SafeSP.getInt(Pref.OldKey.MiuiHome.Refactor.D_APPS_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    SafeSP.putAny(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
            if (SafeSP.getString(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS_STR, "") == "") {
                val oldValue = SafeSP.getInt(Pref.OldKey.MiuiHome.Refactor.D_WALLPAPER_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    SafeSP.putAny(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
            if (SafeSP.getString(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS_STR, "") == "") {
                val oldValue = SafeSP.getInt(Pref.OldKey.MiuiHome.Refactor.D_MINUS_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    SafeSP.putAny(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
        }
        if (spVersion < 2) {
            if (SafeSP.getFloat(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_LEFT, -1f) == -1f) {
                val oldValue = SafeSP.getInt(Pref.OldKey.SystemUI.IconTurner.D_BATTERY_PADDING_LEFT, -1)
                if (oldValue != -1) {
                    SafeSP.putAny(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_LEFT, oldValue.toFloat())
                }
            }
            if (SafeSP.getFloat(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_RIGHT, -1f) == -1f) {
                val oldValue = SafeSP.getInt(Pref.OldKey.SystemUI.IconTurner.D_BATTERY_PADDING_RIGHT, -1)
                if (oldValue != -1) {
                    SafeSP.putAny(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_RIGHT, oldValue.toFloat())
                }
            }
            if (SafeSP.getInt(Pref.Key.SystemUI.IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, -1) == -1) {
                val hidePercentageSymbol = SafeSP.getBoolean(Pref.OldKey.SystemUI.IconTurner.D_HIDE_BATTERY_PERCENT_SYMBOL, false)
                val uniPercentageSymbolSize = SafeSP.getBoolean(Pref.OldKey.SystemUI.IconTurner.D_CHANGE_BATTERY_PERCENT_SYMBOL, false)
                val newValue = if (hidePercentageSymbol) {
                    2
                } else if (uniPercentageSymbolSize) {
                    1
                } else {
                    0
                }
                SafeSP.putAny(Pref.Key.SystemUI.IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, newValue)
            }
        }
        if (spVersion < 3) {
            if (SafeSP.getInt(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, -1) == -1) {
                val oldValue = SafeSP.getBoolean(Pref.OldKey.MiuiHome.D_MINUS_BLUR, false)
                val newValue = if (oldValue) 1 else 0
                SafeSP.putAny(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, newValue)
            }
            if (SafeSP.getInt(Pref.Key.MiuiHome.Refactor.MINUS_MODE, -1) == -1) {
                val overlap = SafeSP.getBoolean(Pref.OldKey.MiuiHome.Refactor.D_MINUS_OVERLAP, false)
                val showLaunchInMinus = SafeSP.getBoolean(Pref.OldKey.MiuiHome.Refactor.D_SHOW_LAUNCH_IN_MINUS, false)
                val newValue = if (overlap) {
                    2
                } else if (showLaunchInMinus) {
                    1
                } else {
                    0
                }
                SafeSP.putAny(Pref.Key.MiuiHome.Refactor.MINUS_MODE, newValue)
            }
        }
        if (spVersion < 4) {
            if (SafeSP.getInt(Pref.Key.PackageInstaller.INSTALL_SOURCE, -1) == -1) {
                val oldValue = SafeSP.getBoolean(Pref.OldKey.PackageInstaller.UPDATE_SYSTEM_APP, false)
                val newValue = if (oldValue) 1 else 0
                SafeSP.putAny(Pref.Key.PackageInstaller.INSTALL_SOURCE, newValue)
            }
            if (SafeSP.getInt(Pref.Key.SecurityCenter.LINK_START, -1) == -1) {
                val oldValue = SafeSP.getBoolean(Pref.OldKey.SecurityCenter.SKIP_WARNING, false)
                val newValue = if (oldValue) 1 else 0
                SafeSP.putAny(Pref.Key.SecurityCenter.LINK_START, newValue)
            }
        }
        SafeSP.putAny(Pref.Key.Module.SP_VERSION, Pref.VERSION)
    }
}