package dev.lackluster.mihelper.hook.rules.systemui

import android.view.View
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.HookParam
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object HideWifiActivityAndType : YukiBaseHooker() {
    override fun onHook() {
        "com.android.systemui.statusbar.StatusBarWifiView".toClass().apply {
            method {
                name = "applyWifiState"
                paramCount = 1
            }.hook {
                after {
                    hideWifi(this)
                }
            }
        }
    }

    private fun hideWifi(param: HookParam) {
        hasEnable(PrefKey.STATUSBAR_HIDE_WIFI_ACTIVITY) {
            (param.instance.current().field {
                name = "mWifiActivityView"
            }.any() as? ImageView)?.visibility = View.INVISIBLE
        }
        hasEnable(PrefKey.STATUSBAR_HIDE_WIFI_TYPE) {
            (param.instance.current().field {
                name = "mWifiStandardView"
            }.any() as? ImageView)?.visibility = View.INVISIBLE
        }
    }
}