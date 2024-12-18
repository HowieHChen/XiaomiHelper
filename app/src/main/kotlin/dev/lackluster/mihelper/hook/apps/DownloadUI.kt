package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.downloadui.HideXL
import dev.lackluster.mihelper.utils.DexKit

object DownloadUI : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(HideXL)
        DexKit.closeDexKit()
    }
}