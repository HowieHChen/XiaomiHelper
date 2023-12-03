package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.gallery.RecognitionOptimize

object Gallery : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(RecognitionOptimize)
    }
}