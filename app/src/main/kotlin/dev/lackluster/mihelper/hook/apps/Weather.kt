package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.weather.CardColor
import dev.lackluster.mihelper.utils.DexKit

object Weather : YukiBaseHooker() {
    override fun onHook() {
        DexKit.initDexKit(this)
        loadHooker(CardColor)
        DexKit.closeDexKit()
    }
}