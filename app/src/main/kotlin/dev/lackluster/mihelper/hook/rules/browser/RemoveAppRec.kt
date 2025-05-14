package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import de.robv.android.xposed.XposedHelpers
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
    private val initSugWebViewMethod by lazy {
        DexKit.findMethodWithCache("init_sug_web_view") {
            matcher {
                addUsingString("SearchSuggestionManager", StringMatchType.Equals)
                addUsingString("miui", StringMatchType.Equals)
                addUsingString("sug", StringMatchType.Equals)
            }
        }
    }
    private val getSugCardExperimentMethod by lazy {
        DexKit.findMethodWithCache("get_sug_card_exp") {
            matcher {
                name = "getSugCardExperiment"
                addUsingString("pref_sug_card_type", StringMatchType.Equals)
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
            initSugWebViewMethod?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val webView = this.instance.current().field {
                        type = "miui.browser.webview.BrowserWebView"
                    }.any() ?: return@after
                    XposedHelpers.callMethod(
                        webView,
                        "removeJavascriptInterface",
                        "quicksearchbox_api"
                    )
                }
            }
            getSugCardExperimentMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceTo("1")
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