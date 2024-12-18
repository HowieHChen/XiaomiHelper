package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.powerkeeper.BlockBatteryWhitelist
import dev.lackluster.mihelper.hook.rules.powerkeeper.CustomRefreshRate
import dev.lackluster.mihelper.hook.rules.powerkeeper.DoNotKillApp
import dev.lackluster.mihelper.hook.rules.powerkeeper.GMSBackgroundRunning
import dev.lackluster.mihelper.utils.DexKit

object PowerKeeper : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(CustomRefreshRate)
        loadHooker(DoNotKillApp)
        loadHooker(BlockBatteryWhitelist)
        loadHooker(GMSBackgroundRunning)
        DexKit.closeDexKit()
    }
}