package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.packageinstaller.AdBlock
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableCountCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableSafeModelTip
import dev.lackluster.mihelper.hook.rules.packageinstaller.SkipInstallRiskCheck
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object PackageInstaller : YukiBaseHooker(){
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AdBlock)
        loadHooker(DisableCountCheck)
        loadHooker(DisableSafeModelTip)
        loadHooker(SkipInstallRiskCheck)
        DexKit.closeDexKit()
    }
}