package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.screenshot.SaveAsPng
import dev.lackluster.mihelper.hook.rules.screenshot.SaveToPictures
import dev.lackluster.mihelper.hook.rules.shared.UnlimitedCropping
import dev.lackluster.mihelper.utils.DexKit

object Screenshot : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(UnlimitedCropping)
        loadHooker(SaveAsPng)
        loadHooker(SaveToPictures)
        DexKit.closeDexKit()
    }
}