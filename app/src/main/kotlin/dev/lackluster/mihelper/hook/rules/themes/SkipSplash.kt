package dev.lackluster.mihelper.hook.rules.themes

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : YukiBaseHooker() {
    private val tryAdSplashMethod by lazy {
        DexKit.findMethodWithCache("try_ad_splash") {
            matcher {
                addUsingString("trySplash: calling package = ", StringMatchType.Equals)
                addUsingString("tryAdSplash : start", StringMatchType.Equals)
                addUsingString("tryAdSplash : end", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Themes.SKIP_SPLASH) {
            if (appClassLoader == null) return@hasEnable
            tryAdSplashMethod?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
        }
    }
}