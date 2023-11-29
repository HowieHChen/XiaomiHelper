package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.CustomRefreshRate
import dev.lackluster.mihelper.utils.DexKit

object MiSettings : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(CustomRefreshRate)
        DexKit.closeDexKit()
    }
}