package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.taplus.CustomSearch
import dev.lackluster.mihelper.hook.rules.taplus.HideShop
import dev.lackluster.mihelper.hook.rules.taplus.Landscape
import dev.lackluster.mihelper.hook.rules.shared.UnlockForPad

object Taplus : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(CustomSearch)
        loadHooker(Landscape)
        loadHooker(HideShop)
        loadHooker(UnlockForPad)
    }
}