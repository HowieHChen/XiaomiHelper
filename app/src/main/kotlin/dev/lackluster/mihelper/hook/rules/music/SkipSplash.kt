package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SkipSplash : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Music.SKIP_SPLASH) {
            val enableClass = "com.tencent.qqmusiclite.business.splashad.data.enums.Enable".toClassOrNull()
            val disableVIP = if (enableClass?.isEnum == true) enableClass.enumConstants[4] else null
            if (disableVIP != null) {
                "com.tencent.qqmusiclite.business.splashad.ams.AmsGlobal".toClassOrNull()?.apply {
                    method {
                        name = "isNeedAd"
                    }.hook {
                        replaceTo(disableVIP)
                    }
                    method {
                        name = "isNeedSplashAd"
                    }.hook {
                        replaceTo(disableVIP)
                    }
                }
            } else {
                "com.tencent.qqmusiclite.activity.MainActivity".toClassOrNull()?.apply {
                    method {
                        name = "checkColdSplash"
                    }.hook {
                        intercept()
                    }
                }
                "com.tencent.qqmusiclite.business.splashad.ams.AmsGlobal".toClassOrNull()?.apply {
                    method {
                        name = "checkHotSplash"
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}