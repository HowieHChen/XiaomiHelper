package dev.lackluster.mihelper.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
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
import dev.lackluster.mihelper.activity.pages.sub.StatusBarClockPage
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.BackupUtils
import dev.lackluster.mihelper.utils.factory.getSP


class MainActivity : MIUIActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initAndCheck()
        versionCompatible()
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("WorldReadableFiles")
    private fun initAndCheck(): Boolean {
        try {
            setSP(getSP(this))
            if (!safeSP.getBoolean(Pref.Key.Module.ENABLED, false)) {
                MIUIDialog(this) {
                    setTitle(R.string.dialog_warning)
                    setMessage(R.string.module_disabled_tips)
                    setCancelable(false)
                    setRButton(R.string.button_ok) {
                        showFragment("page_module")
                        dismiss()
                    }
                }.show()
            }
        } catch (exception: SecurityException) {
            MIUIDialog(this) {
                setTitle(R.string.dialog_error)
                setMessage(R.string.module_inactive_tips)
                setCancelable(false)
                setRButton(R.string.button_ok) {
                    dismiss()
                }
            }.show()
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
            MIUIDialog(this) {
                setTitle(titleId)
                setMessage(getString(msgId) + errMsg)
                setCancelable(false)
                setRButton(R.string.button_ok) {
                    dismiss()
                    if (requestCode == BackupUtils.READ_DOCUMENT_CODE) showFragment("__main__")
                }
            }.show()
        }
    }

    private fun versionCompatible() {
        val spVersion = safeSP.getInt(Pref.Key.Module.SP_VERSION, 0)
        if (spVersion < 1) {
            if (safeSP.getString(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS_STR, "") == "") {
                val oldValue = safeSP.getInt(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.MiuiHome.Refactor.APPS_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
            if (safeSP.getString(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS_STR, "") == "") {
                val oldValue = safeSP.getInt(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.MiuiHome.Refactor.WALLPAPER_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
            if (safeSP.getString(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS_STR, "") == "") {
                val oldValue = safeSP.getInt(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS, -1)
                if (oldValue != -1) {
                    safeSP.putAny(Pref.Key.MiuiHome.Refactor.MINUS_BLUR_RADIUS_STR, "${oldValue}px")
                }
            }
        }
        safeSP.putAny(Pref.Key.Module.SP_VERSION, Pref.VERSION)
    }
}