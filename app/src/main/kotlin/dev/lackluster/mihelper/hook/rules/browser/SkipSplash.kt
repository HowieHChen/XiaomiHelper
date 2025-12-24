package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object SkipSplash : YukiBaseHooker() {
    private val thirdPartyLaunchAdMethod by lazy {
        DexKit.findMethodWithCache("skip_splash_third") {
            matcher {
                returnType = "void"
                paramCount = 1
                paramTypes = listOf("android.content.Context")
                addUsingString("onTrackAppOpenThird appLaunchWay:", StringMatchType.Equals)
                addUsingString("第三方调起", StringMatchType.Equals)
            }
        }
    }
    private val iconLaunchAdMethod by lazy {
        DexKit.findMethodWithCache("skip_splash_icon") {
            matcher {
                returnType = "void"
                addUsingString("SplashActiveAdManager", StringMatchType.Equals)
                addUsingString("requestAd", StringMatchType.Equals)
                addUsingString("msa_request", StringMatchType.Equals)
            }
        }
    }
    private val supportPassive by lazy {
        DexKit.findMethodWithCache("skip_splash_support") {
            matcher {
                returnType = "boolean"
                addUsingString("SystemSplashAd", StringMatchType.Equals)
                addUsingString("support_passive", StringMatchType.Equals)
                addUsingString("content://com.miui.systemAdSolution.extContentProvider/supportPassive", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Browser.SKIP_SPLASH) {
            if (appClassLoader == null) return@hasEnable
            thirdPartyLaunchAdMethod?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            iconLaunchAdMethod?.getMethodInstance(appClassLoader!!)?.hook {
                intercept()
            }
            supportPassive?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            "com.android.browser.splash.SplashAdManager".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "inWhiteList"
                }?.hook {
                    replaceToTrue()
                }
            }
//            "com.android.browser.xiangkan.AppDownloadHelper".toClass().apply {
//                method {
//                    name = "onLoadData"
//                }.hook {
//                    intercept()
//                }
//            }
        }
    }
}