package dev.lackluster.mihelper.hook.rules.music

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object AdBlock : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.MUSIC_AD_BLOCK) {
            "com.tencent.qqmusiclite.activity.SplashAdActivity".toClassOrNull()
                ?.method {
                    name = "onCreate"
                    param(BundleClass)
                }?.ignored()
                ?.hook {
                    after {
                        "android.app.Activity".toClassOrNull()
                            ?.getMethod("finish")
                            ?.invoke(this.instance)
                    }
                }
        }
    }
}