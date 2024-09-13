package dev.lackluster.mihelper.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import dev.lackluster.hyperx.app.AlertDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.pages.bezier.AnimCurvePreviewAppsPage
import dev.lackluster.mihelper.activity.pages.bezier.AnimCurvePreviewFolderPage
import dev.lackluster.mihelper.activity.pages.bezier.AnimCurvePreviewWallpaperPage
import dev.lackluster.mihelper.activity.pages.main.AboutPage
import dev.lackluster.mihelper.activity.pages.main.MainPage
import dev.lackluster.mihelper.activity.pages.main.MenuPage
import dev.lackluster.mihelper.activity.pages.prefs.CleanMasterPage
import dev.lackluster.mihelper.activity.pages.prefs.InterconnectionPage
import dev.lackluster.mihelper.activity.pages.prefs.MiuiHomePage
import dev.lackluster.mihelper.activity.pages.prefs.ModuleSettingsPage
import dev.lackluster.mihelper.activity.pages.prefs.OthersPage
import dev.lackluster.mihelper.activity.pages.prefs.SecurityCenterPage
import dev.lackluster.mihelper.activity.pages.prefs.SystemFrameworkPage
import dev.lackluster.mihelper.activity.pages.prefs.SystemUIPage
import dev.lackluster.mihelper.activity.pages.sub.DisableFixedOrientationPage
import dev.lackluster.mihelper.activity.pages.sub.HomeRefactorPage
import dev.lackluster.mihelper.activity.pages.sub.IconTunerPage
import dev.lackluster.mihelper.activity.pages.sub.MediaControlStylePage
import dev.lackluster.mihelper.activity.pages.sub.MinusBlurPage
import dev.lackluster.mihelper.activity.pages.sub.StatusBarClockPage
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.BackupUtils
import dev.lackluster.mihelper.utils.factory.getSP

class MainActivity : MIUIActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAndCheck()
    }

    @SuppressLint("WorldReadableFiles")
    private fun initAndCheck(): Boolean {
        try {
            setSP(getSP(this))
            versionCompatible()
            if (!safeSP.getBoolean(Pref.Key.Module.ENABLED, false)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_warning)
                    .setMessage(R.string.module_disabled_tips)
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.button_ok) { dialog, _ ->
                        showFragment(Pages.MODULE_SETTINGS)
                        dialog.dismiss()
                    }
                    .show()
            }
        } catch (exception: SecurityException) {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_error)
                .setMessage(R.string.module_inactive_tips)
                .setCancelable(false)
                .setPositiveButton(R.string.button_ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return false
        }
        return true
    }

    init {
        registerPage(MainPage::class.java)
        registerPage(MenuPage::class.java)
        registerPage(AboutPage::class.java)
        registerPage(ModuleSettingsPage::class.java)
        registerPage(SystemUIPage::class.java)
        registerPage(MiuiHomePage::class.java)
        registerPage(CleanMasterPage::class.java)
        registerPage(SystemFrameworkPage::class.java)
        registerPage(SecurityCenterPage::class.java)
        registerPage(InterconnectionPage::class.java)
        registerPage(OthersPage::class.java)
        registerPage(StatusBarClockPage::class.java)
        registerPage(IconTunerPage::class.java)
        registerPage(MediaControlStylePage::class.java)
        registerPage(DisableFixedOrientationPage::class.java)
        registerPage(HomeRefactorPage::class.java)
        registerPage(AnimCurvePreviewAppsPage::class.java)
        registerPage(AnimCurvePreviewFolderPage::class.java)
        registerPage(AnimCurvePreviewWallpaperPage::class.java)
        registerPage(MinusBlurPage::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        var titleId = R.string.dialog_error
        var msgId = R.string.module_unknown_failure
        var errMsg = ""
        try {
            when (requestCode) {
                BackupUtils.WRITE_DOCUMENT_CODE -> {
                    BackupUtils.handleBackup(this, data.data)
                    titleId = R.string.dialog_done
                    msgId = R.string.module_backup_success
                }
                BackupUtils.READ_DOCUMENT_CODE -> {
                    BackupUtils.handleRestore(this, data.data)
                    titleId = R.string.dialog_done
                    msgId = R.string.module_restore_success
                }
                else -> return
            }
        } catch (t: Throwable) {
            when (requestCode) {
                BackupUtils.WRITE_DOCUMENT_CODE -> {
                    msgId = R.string.module_backup_failure
                }
                BackupUtils.READ_DOCUMENT_CODE -> {
                    msgId = R.string.module_restore_failure
                }
            }
            errMsg = "\n" + t.stackTraceToString()
        } finally {
            AlertDialog.Builder(this)
                .setTitle(titleId)
                .setMessage(getString(msgId) + errMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.button_ok) { dialog, _ ->
                    dialog.dismiss()
                    if (requestCode == BackupUtils.READ_DOCUMENT_CODE) {
                        showFragment(Pages.MAIN)
                        initAndCheck()
                    }
                }
                .show()
        }
    }

    private fun versionCompatible() {
        val spVersion = safeSP.getInt(Pref.Key.Module.SP_VERSION, 0)
        if (spVersion < 1) {
            if (safeSP.getString(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS_STR, "") == "") {
                val oldValue = safeSP.getInt(Pref.OldKey.MiuiHome.Refactor.D_APPS_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
            if (safeSP.getString(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS_STR, "") == "") {
                val oldValue = safeSP.getInt(Pref.OldKey.MiuiHome.Refactor.D_WALLPAPER_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
            if (safeSP.getString(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS_STR, "") == "") {
                val oldValue = safeSP.getInt(Pref.OldKey.MiuiHome.Refactor.D_MINUS_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
        }
        if (spVersion < 2) {
            if (safeSP.getFloat(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_LEFT, -1f) == -1f) {
                val oldValue = safeSP.getInt(Pref.OldKey.SystemUI.IconTurner.D_BATTERY_PADDING_LEFT, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_LEFT, oldValue.toFloat())
                }
            }
            if (safeSP.getFloat(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_RIGHT, -1f) == -1f) {
                val oldValue = safeSP.getInt(Pref.OldKey.SystemUI.IconTurner.D_BATTERY_PADDING_RIGHT, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.SystemUI.IconTurner.BATTERY_PADDING_RIGHT, oldValue.toFloat())
                }
            }
            if (safeSP.getInt(Pref.Key.SystemUI.IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, -1) == -1) {
                val hidePercentageSymbol = safeSP.getBoolean(Pref.OldKey.SystemUI.IconTurner.D_HIDE_BATTERY_PERCENT_SYMBOL, false)
                val uniPercentageSymbolSize = safeSP.getBoolean(Pref.OldKey.SystemUI.IconTurner.D_CHANGE_BATTERY_PERCENT_SYMBOL, false)
                val newValue = if (hidePercentageSymbol) {
                    2
                } else if (uniPercentageSymbolSize) {
                    1
                } else {
                    0
                }
                safeSP.putAny(Pref.Key.SystemUI.IconTurner.BATTERY_PERCENTAGE_SYMBOL_STYLE, newValue)
            }
        }
        if (spVersion < 3) {
            if (safeSP.getInt(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, -1) == -1) {
                val oldValue = safeSP.getBoolean(Pref.OldKey.MiuiHome.D_MINUS_BLUR, false)
                val newValue = if (oldValue) 1 else 0
                safeSP.putAny(Pref.Key.MiuiHome.MINUS_BLUR_TYPE, newValue)
            }
            if (safeSP.getInt(Pref.Key.MiuiHome.Refactor.MINUS_MODE, -1) == -1) {
                val overlap = safeSP.getBoolean(Pref.OldKey.MiuiHome.Refactor.D_MINUS_OVERLAP, false)
                val showLaunchInMinus = safeSP.getBoolean(Pref.OldKey.MiuiHome.Refactor.D_SHOW_LAUNCH_IN_MINUS, false)
                val newValue = if (overlap) {
                    2
                } else if (showLaunchInMinus) {
                    1
                } else {
                    0
                }
                safeSP.putAny(Pref.Key.MiuiHome.Refactor.MINUS_MODE, newValue)
            }
        }
        safeSP.putAny(Pref.Key.Module.SP_VERSION, Pref.VERSION)
    }
}