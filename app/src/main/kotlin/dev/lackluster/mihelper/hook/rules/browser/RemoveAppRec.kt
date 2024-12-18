package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object RemoveAppRec : YukiBaseHooker() {
    private val getApkVersionMethods by lazy {
        DexKit.findMethodsWithCache("remove_app_rec") {
            matcher {
                returnType = "int"
                name = "getApkVersion"
                paramTypes = listOf("java.lang.String")
                addUsingString("getApkVersion", StringMatchType.Equals)
                addUsingString("packageName", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Browser.REMOVE_APP_REC) {
            if (appClassLoader == null) return@hasEnable
            getApkVersionMethods.map { it.getMethodInstance(appClassLoader!!) }.hookAll {
                before {
                    val packageName = this.args(0).string()
                    if (packageName != "com.android.browser") {
                        this.result = Int.MAX_VALUE
                    }
                }
            }
//            "com.android.browser.hybrid.HybridActionDispatcher".toClass().apply {
//                method {
//                    name = "send"
//                }.hook {
//                    after {
//                        val uri = this.args(0).string()
//                        YLog.info("$uri \n ${this.result.toString()}")
//                        if (uri.startsWith("nativechannel://getApkVersion")) {
//                            this.result = Int.MAX_VALUE.toString()
//                        }
//                    }
//                }
//            }
        }
    }
}