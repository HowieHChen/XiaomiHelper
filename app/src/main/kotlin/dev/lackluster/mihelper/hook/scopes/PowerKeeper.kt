package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.powerkeeper.BlockBatteryWhitelist
import dev.lackluster.mihelper.hook.rules.powerkeeper.CustomRefreshRate
import dev.lackluster.mihelper.hook.rules.powerkeeper.GMSBackgroundRunning

object PowerKeeper : StaticHooker() {
    override val requireDexKit: Boolean = true

    override fun onInit() {
        attach(CustomRefreshRate)
        attach(BlockBatteryWhitelist)
        attach(GMSBackgroundRunning)
    }
}