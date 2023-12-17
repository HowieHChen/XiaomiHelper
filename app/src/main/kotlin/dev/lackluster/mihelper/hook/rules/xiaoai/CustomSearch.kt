package dev.lackluster.mihelper.hook.rules.xiaoai

import android.content.Intent
import android.net.Uri
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object CustomSearch : YukiBaseHooker() {
    private val openBrowser by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("sendIntentInner fail because empty type", StringMatchType.Equals)
                returnType = "int"
            }
        }.firstOrNull()
    }
    private val searchEngine = Prefs.getInt(PrefKey.XIAOAI_SEARCH_ENGINE, 0)
    private val searchEngineUrl = Prefs.getString(PrefKey.XIAOAI_SEARCH_URL, "")
    private val searchUrlValues = arrayOf(
        "",
        "https://www.baidu.com/s?wd=%s",
        "https://www.sogou.com/web?query=%s",
        "https://www.bing.com/search?q=%s",
        "https://www.google.com/search?q=%s",
    )
    private val queryStringRegex by lazy {
        Regex(pattern = "https://.*?&word=(.*?)&bd_ck=")
    }
    override fun onHook() {
        hasEnable(PrefKey.XIAOAI_USE_BROWSER) {
            openBrowser?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                before {
                    if (this.args(1).string() == "activity" && this.args(3).string() == "com.android.browser") {
                        val intent = this.args(0).any() as Intent
                        val queryString = queryStringRegex.find(intent.dataString.toString())?.groupValues?.get(1)
                        if (queryString.isNullOrBlank()) {
                            return@before
                        }
                        YLog.info(intent.flags.toString())
                        val newIntent = Intent()
                        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        val searchUrl =
                            when (searchEngine) {
                                in 1..4 -> searchUrlValues[searchEngine].replaceFirst("%s",queryString)
                                5 -> searchEngineUrl?.replaceFirst("%s",queryString)
                                else -> ""
                            }
                        if (searchEngine == 0 || searchUrl.isNullOrBlank()) {
                            newIntent.action = Intent.ACTION_WEB_SEARCH
                            newIntent.putExtra("query", queryString)
                        }
                        else {
                            newIntent.action = Intent.ACTION_VIEW
                            newIntent.data = Uri.parse(searchUrl)
                        }
                        val intentUri = newIntent.toUri(Intent.URI_INTENT_SCHEME)
                        this.args(0).set(newIntent)
                        this.args(2).set(intentUri)
                        // this.args(3).set("")
                    }
                }
            }
        }
    }
}