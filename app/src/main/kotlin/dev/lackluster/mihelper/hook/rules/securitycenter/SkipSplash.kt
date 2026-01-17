package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : YukiBaseHooker() {
    private val clzScreenAdUtils by lazy {
        DexKit.findClassWithCache("screen_ad_utils") {
            matcher {
                addUsingString("ScreenAdUtils", StringMatchType.Equals)
                addUsingString("content://com.miui.systemAdSolution", StringMatchType.StartsWith)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.SecurityCenter.SKIP_SPLASH) {
            if (appClassLoader == null) return@hasEnable
            clzScreenAdUtils?.getInstance(appClassLoader!!)?.apply {
                resolve().firstMethodOrNull {
                    parameterCount = 2
                    returnType = Boolean::class
                }?.hook {
                    replaceToTrue()
                }
                resolve().firstMethodOrNull {
                    parameterCount = 3
                    returnType = Void.TYPE
                }?.hook {
                    before {
                        this.args(2).setTrue()
                    }
                }
            }
        }
    }
}