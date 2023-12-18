package dev.lackluster.mihelper.hook.rules.taplus

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object CustomSearch : YukiBaseHooker() {
    private val searchEngine = Prefs.getInt(PrefKey.TAPLUS_SEARCH_ENGINE, 0)
    private val searchEngineUrl = Prefs.getString(PrefKey.TAPLUS_SEARCH_URL, "")
    private val searchUrlValues = arrayOf(
        "",
        "https://www.baidu.com/s?wd=%s",
        "https://www.sogou.com/web?query=%s",
        "https://www.bing.com/search?q=%s",
        "https://www.google.com/search?q=%s",
    )
    override fun onHook() {
        hasEnable(PrefKey.TAPLUS_USE_BROWSER) {
            "com.miui.contentextension.utils.AppsUtils".toClass()
                .method {
                    name = "openGlobalSearch"
                }
                .hook {
                    before {
                        val context = this.args[0] as Context
                        val queryString = this.args[1] as String
                        var searchUrl =
                            when (searchEngine) {
                                in 1..4 -> searchUrlValues[searchEngine].replaceFirst("%s",queryString)
                                5 -> searchEngineUrl?.replaceFirst("%s",queryString)
                                else -> ""
                            }
                        val intent = Intent()
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        if (searchEngine == 0 || searchUrl.isNullOrBlank()) {
                            intent.action = Intent.ACTION_WEB_SEARCH
                            intent.putExtra("query", queryString)
                        }
                        else {
                            if (!searchUrl.startsWith("https://") && !searchUrl.startsWith("http://")) {
                                searchUrl = "https://$searchUrl"
                            }
                            intent.action = Intent.ACTION_VIEW
                            intent.data = Uri.parse(searchUrl)
                        }
                        context.startActivity(intent)
                        this.result = null
                    }
                }
        }
    }
}