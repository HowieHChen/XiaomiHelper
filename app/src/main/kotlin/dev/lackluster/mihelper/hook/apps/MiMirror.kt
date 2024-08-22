package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.shared.AllowSendAllApp
import dev.lackluster.mihelper.utils.DexKit

object MiMirror : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AllowSendAllApp)
        DexKit.closeDexKit()
    }
}