package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object DisableSafeModelTip : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_SAFE_MODE_TIP) {
            "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject".toClassOrNull()
                ?.method {
                    param("com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject\$ViewHolder".toClass())
                    paramCount = 1
                }
                ?.ignored()
                ?.hookAll {
                    after {
                        val viewHolder = this.args(0).any()
                        (viewHolder?.current()?.method {
                            name = "getClContentView"
                        }?.call() as? View)?.visibility = View.GONE
                    }
                }
        }
    }
}