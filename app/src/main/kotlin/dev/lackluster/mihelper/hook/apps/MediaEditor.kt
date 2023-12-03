package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.UnlimitedCropping
import dev.lackluster.mihelper.utils.DexKit

object MediaEditor : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(UnlimitedCropping)
        DexKit.closeDexKit()
    }
}