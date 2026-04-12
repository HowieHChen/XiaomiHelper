package dev.lackluster.mihelper.hook.scopes

import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.rules.music.AdBlocker
import dev.lackluster.mihelper.hook.rules.music.HideFavNum
import dev.lackluster.mihelper.hook.rules.music.HideListenCount
import dev.lackluster.mihelper.hook.rules.music.HideTopTab
import dev.lackluster.mihelper.hook.rules.music.HideMyPageElement
import dev.lackluster.mihelper.hook.rules.music.SkipSplash

object Music : StaticHooker() {
    override fun onInit() {
        attach(AdBlocker)
        attach(SkipSplash)
        attach(HideTopTab)
        attach(HideMyPageElement)
        attach(HideFavNum)
        attach(HideListenCount)
    }
}