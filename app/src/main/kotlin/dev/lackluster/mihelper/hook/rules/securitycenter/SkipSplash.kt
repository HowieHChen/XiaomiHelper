package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : YukiBaseHooker() {
    private val screenAdUtilsClass by lazy {
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
            screenAdUtilsClass?.getInstance(appClassLoader!!)?.apply {
                method {
                    paramCount = 2
                    returnType = BooleanType
                }.hook {
                    replaceToTrue()
                }
                method {
                    paramCount = 3
                    returnType = Void.TYPE
                }.hook {
                    before {
                        this.args(2).setTrue()
                    }
                }
            }
        }
    }
}