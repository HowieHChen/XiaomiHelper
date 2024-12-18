package dev.lackluster.mihelper.hook.apps

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.hook.rules.music.AdBlocker
import dev.lackluster.mihelper.hook.rules.music.HideLongAudioTab
import dev.lackluster.mihelper.hook.rules.music.HideMyPageElement
import dev.lackluster.mihelper.hook.rules.music.SkipSplash

object Music : YukiBaseHooker() {
    override fun onHook() {
        loadHooker(AdBlocker)
        loadHooker(SkipSplash)
        loadHooker(HideLongAudioTab)
        loadHooker(HideMyPageElement)
    }
}