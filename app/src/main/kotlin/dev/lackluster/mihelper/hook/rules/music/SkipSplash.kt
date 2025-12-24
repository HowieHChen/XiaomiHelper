package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object SkipSplash : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.Music.SKIP_SPLASH) {
            val enableClass = "com.tencent.qqmusiclite.business.splashad.data.enums.Enable".toClassOrNull()
            val disableVIP = if (enableClass?.isEnum == true) enableClass.enumConstants?.get(4) else null
            if (disableVIP != null) {
                "com.tencent.qqmusiclite.business.splashad.ams.AmsGlobal".toClassOrNull()?.apply {
                    resolve().firstMethodOrNull {
                        name = "isNeedAd"
                    }?.hook {
                        replaceTo(disableVIP)
                    }
                    resolve().firstMethodOrNull {
                    name = "isNeedSplashAd"
                    }?.hook {
                        replaceTo(disableVIP)
                    }
                }
            } else {
                "com.tencent.qqmusiclite.activity.MainActivity".toClassOrNull()?.apply {
                    resolve().firstMethodOrNull {
                        name = "checkColdSplash"
                    }?.hook {
                        intercept()
                    }
                }
                "com.tencent.qqmusiclite.business.splashad.ams.AmsGlobal".toClassOrNull()?.apply {
                    resolve().firstMethodOrNull {
                        name = "checkHotSplash"
                    }?.hook {
                        intercept()
                    }
                }
            }
        }
    }
}