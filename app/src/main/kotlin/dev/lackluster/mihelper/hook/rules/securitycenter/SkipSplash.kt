package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private val clzScreenAdUtils by lazy {
        DexKit.findClassWithCache("screen_ad_utils") {
            matcher {
                addUsingString("ScreenAdUtils", StringMatchType.Equals)
                addUsingString("content://com.miui.systemAdSolution", StringMatchType.StartsWith)
            }
        }
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.SecurityCenter.SKIP_SPLASH.observe {
                updateSelfState(it)
            }
        )
        clzScreenAdUtils
    }

    override fun onStart() {
        clzScreenAdUtils?.getInstance(classLoader)?.apply {
            resolve().firstMethodOrNull {
                parameterCount = 2
                returnType = Boolean::class
            }?.hook {
                result(true)
            }
            resolve().firstMethodOrNull {
                parameterCount = 3
                returnType = Void.TYPE
            }?.hook {
                val newArgs = args.toTypedArray()
                newArgs[2] = true
                result(proceed(newArgs))
            }
        }
    }
}