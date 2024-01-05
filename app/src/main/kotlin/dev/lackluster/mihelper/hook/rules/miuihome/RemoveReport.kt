package dev.lackluster.mihelper.hook.rules.miuihome

import android.view.View
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object RemoveReport : YukiBaseHooker(){
    override fun onHook() {
        hasEnable(PrefKey.HOME_REMOVE_REPORT, extraCondition = { !Device.isPad }) {
            "com.miui.home.launcher.uninstall.BaseUninstallDialog".toClass()
                .method {
                    name = "init"
                    paramCount = 2
                }
                .hook {
                    after {
                        val report = (this.instance.current().field {
                            name = "mDialogView"
                            superClass()
                        }.any())?.current(true)?.field {
                            name = "mReport"
                        }?.any() as? TextView?
                        report?.visibility = View.GONE
                    }
                }
        }
    }
}