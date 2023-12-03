package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.joyose.DisableCloudControl
import dev.lackluster.mihelper.utils.DexKit

object Joyose : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(DisableCloudControl)
        DexKit.closeDexKit()
    }
}