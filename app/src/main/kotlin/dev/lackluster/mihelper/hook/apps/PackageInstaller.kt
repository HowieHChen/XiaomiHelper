package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.packageinstaller.AdBlocker
import dev.lackluster.mihelper.hook.rules.packageinstaller.BlockUploadAppInfo
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableCountCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.DisableRiskCheck
import dev.lackluster.mihelper.hook.rules.packageinstaller.MoreInfo
import dev.lackluster.mihelper.hook.rules.packageinstaller.UpdateSystemApps
import dev.lackluster.mihelper.utils.DexKit

object PackageInstaller : YukiBaseHooker(){
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(AdBlocker)
        loadHooker(DisableCountCheck)
        loadHooker(DisableRiskCheck)
        loadHooker(BlockUploadAppInfo)
        loadHooker(UpdateSystemApps)
//        loadHooker(MoreInfo)
        DexKit.closeDexKit()
    }
}