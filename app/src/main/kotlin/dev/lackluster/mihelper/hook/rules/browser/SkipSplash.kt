package dev.lackluster.mihelper.hook.rules.browser

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
//            "com.android.browser.xiangkan.AppDownloadHelper".toClass().apply {
//                method {
//                    name = "onLoadData"
//                }.hook {
//                    intercept()
//                }
//            }
//            ActivityClass.apply {
//                method {
//                    name = "startActivity"
//                    paramCount = 1
//                    param(IntentClass)
//                }.hook {
//                    before {
//                        val intent = this.args(0).cast<Intent?>() ?: return@before
//                        if (intent.dataString?.contains("mimarket://details") == true) {
//                            val argMaps = intent.dataString?.split('&') ?: return@before
//                            val sourceFileName = argMaps.filter { it.startsWith("sourceFileName") }.firstOrNull()?.replace("sourceFileName=", "")
//                            val sourceFileUrl = argMaps.filter { it.startsWith("sourceFileUrl") }.firstOrNull()?.replace("sourceFileUrl=", "")
//                            YLog.info("sourceFileName: $sourceFileName sourceFileUrl: $sourceFileUrl" )
//                            // this.result = null
//                        }
//                    }
//                }
//            }
        }
    }
}