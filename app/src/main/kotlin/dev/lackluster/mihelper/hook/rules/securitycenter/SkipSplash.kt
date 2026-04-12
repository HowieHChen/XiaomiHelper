package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.StaticHooker
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.get
import dev.lackluster.mihelper.hook.utils.ifTrue
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : StaticHooker() {
    private val clzScreenAdUtils by lazy {
        DexKit.findClassWithCache("screen_ad_utils") {
            matcher {
                addUsingString("ScreenAdUtils", StringMatchType.Equals)
                addUsingString("content://com.miui.systemAdSolution", StringMatchType.StartsWith)
            }
        }
    }

    override fun onInit() {
        Preferences.SecurityCenter.SKIP_SPLASH.get().also {
            updateSelfState(it)
        }.ifTrue {
            clzScreenAdUtils
        }
    }

    override fun onHook() {
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