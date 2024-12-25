package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.taplus.CustomSearch
import dev.lackluster.mihelper.hook.rules.taplus.HideShop

object Taplus : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(CustomSearch)
        loadHooker(HideShop)
    }
}