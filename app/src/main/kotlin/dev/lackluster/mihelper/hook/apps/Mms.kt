package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.mms.AdBlocker
import dev.lackluster.mihelper.utils.DexKit

object Mms : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AdBlocker)
        DexKit.closeDexKit()
    }
}