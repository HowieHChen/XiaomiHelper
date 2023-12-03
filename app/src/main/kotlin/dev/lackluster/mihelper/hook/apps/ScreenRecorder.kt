package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.screenrecorder.SaveToMovies

object ScreenRecorder : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(SaveToMovies)
    }
}