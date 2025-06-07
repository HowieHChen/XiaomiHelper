package dev.lackluster.mihelper.hook.rules.settings

import android.app.Activity
import android.provider.Settings
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object QuickPermission : YukiBaseHooker() {
    private val perOverlay = Prefs.getBoolean(Pref.Key.Settings.QUICK_PER_OVERLAY, false)
    private val perInstallSource = Prefs.getBoolean(Pref.Key.Settings.QUICK_PER_INSTALL_SOURCE, false)

    override fun onHook() {
        if (perOverlay || perInstallSource) {
            "com.android.settings.SettingsActivity".toClass().apply {
                method {
                    name = "redirectTabletActivity"
                    param(BundleClass)
                }.hook {
                     before {
                         val intent = this.instance<Activity>().intent
                         if (intent?.data == null || intent.data?.scheme != "package") return@before
                         if (perOverlay && intent.action == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                             this.instance.current().field {
                                 name = "initialFragmentName"
                                 superClass()
                             }.set("com.android.settings.applications.appinfo.DrawOverlayDetails")
                         }
                         if (perInstallSource && intent.action == Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES) {
                             this.instance.current().field {
                                 name = "initialFragmentName"
                                 superClass()
                             }.set("com.android.settings.applications.appinfo.ExternalSourcesDetails")
                         }
                     }
                }
            }
        }
    }
}