package dev.lackluster.mihelper.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.activity.pages.main.MainPage
import dev.lackluster.mihelper.activity.pages.main.MenuPage
import dev.lackluster.mihelper.activity.pages.scope.AndroidPage
import dev.lackluster.mihelper.activity.pages.scope.MiConnectPage
import dev.lackluster.mihelper.activity.pages.scope.MiuiHomePage
import dev.lackluster.mihelper.activity.pages.scope.OthersPage
import dev.lackluster.mihelper.activity.pages.scope.SecurityCenterPage
import dev.lackluster.mihelper.activity.pages.scope.SystemUIPage
import dev.lackluster.mihelper.activity.pages.sub.DisableFixedOrientationPage
import dev.lackluster.mihelper.activity.pages.sub.HideIconPage
import dev.lackluster.mihelper.utils.factory.getSP

class MainActivity : MIUIActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initAndCheck()
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("WorldReadableFiles")
    private fun initAndCheck(): Boolean {
        try {
            setSP(getSP(this))
            return true
        } catch (exception: SecurityException) {
            MIUIDialog(this) {
                setTitle(R.string.dialog_error)
                setMessage(R.string.module_inactive_tips)
                setCancelable(false)
                setRButton(R.string.button_ok) {
                    dismiss()
                }
            }.show()
        }
        return false
    }

    init {
        registerPage(MainPage::class.java)
        registerPage(MenuPage::class.java)
        registerPage(AndroidPage::class.java)
        registerPage(OthersPage::class.java)
        registerPage(MiConnectPage::class.java)
        registerPage(SecurityCenterPage::class.java)
        registerPage(SystemUIPage::class.java)
        registerPage(MiuiHomePage::class.java)
        registerPage(DisableFixedOrientationPage::class.java)
        registerPage(HideIconPage::class.java)
        when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.TIRAMISU -> {

            }
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {

            }
        }
    }
}