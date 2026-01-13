package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.updater.BlockAutoUpdateDialog
import dev.lackluster.mihelper.hook.rules.updater.DisableValidation
import dev.lackluster.mihelper.utils.DexKit

object Updater : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(DisableValidation)
        loadHooker(BlockAutoUpdateDialog)
        DexKit.closeDexKit()
    }
}