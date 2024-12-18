package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideLongAudioTab : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Music.HIDE_LONG_AUDIO) {
            "com.tencent.qqmusiclite.fragment.home.BaseHomeFragment".toClassOrNull()?.apply {
                method {
                    name = "setupViewPager"
                }.hook {
                    before {
                        this.instance.current().field {
                            name = "mIsLongAudioEnable"
                        }.setFalse()
                    }
                }
            }
        }
    }
}