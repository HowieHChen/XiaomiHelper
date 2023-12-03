package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.download.RemoveXLDownload

object Download : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(RemoveXLDownload)
    }
}