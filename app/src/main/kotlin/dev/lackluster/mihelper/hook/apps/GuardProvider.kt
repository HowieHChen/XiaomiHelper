package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.guardprovider.BlockUploadAppList
import dev.lackluster.mihelper.utils.DexKit

object GuardProvider: YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(BlockUploadAppList)
        DexKit.closeDexKit()
    }
}