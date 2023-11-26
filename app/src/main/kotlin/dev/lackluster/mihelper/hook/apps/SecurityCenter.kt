package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.securitycenter.LockScore
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipOpenApp
import dev.lackluster.mihelper.hook.rules.securitycenter.SkipWarning
import dev.lackluster.mihelper.utils.DexKit

object SecurityCenter : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(SkipWarning)
        loadHooker(SkipOpenApp)
        loadHooker(LockScore)
        DexKit.closeDexKit()
    }
}