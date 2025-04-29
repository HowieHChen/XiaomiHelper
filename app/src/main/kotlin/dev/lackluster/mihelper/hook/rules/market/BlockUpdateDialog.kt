package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object BlockUpdateDialog : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Market.BLOCK_UPDATE_DIALOG) {
            "com.xiaomi.market.ui.UpdateListFragment".toClassOrNull()?.apply {
                method {
                    name = "tryShowDialog"
                }.ignored().hook {
                    intercept()
                }
            }
            "com.xiaomi.market.ui.update.UpdatePushDialogManager".toClassOrNull()?.apply {
                method {
                    name = "tryShowDialog"
                }.hook {
                    intercept()
                }
            }
        }
    }
}