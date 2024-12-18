package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.lbe.BlockRemoveAutoStart
import dev.lackluster.mihelper.hook.rules.lbe.ClipboardToast

object LBE : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(BlockRemoveAutoStart)
        loadHooker(ClipboardToast)
    }
}