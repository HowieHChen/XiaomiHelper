package dev.lackluster.mihelper.hook.rules.market

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object HideTabItem : YukiBaseHooker() {
    private val hideGame = Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_GAME, false)
    private val hideRank = Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_RANK, false)
    private val hideAppAssemble = Prefs.getBoolean(Pref.Key.Market.HIDE_TAB_APP_ASSEMBLE, false)
    private val tabInfoClass by lazy {
        "com.xiaomi.market.model.TabInfo".toClassOrNull()
    }

    override fun onHook() {
        if (hideGame || hideRank || hideAppAssemble) {
            val tabTagField = tabInfoClass?.field {
                name = "tag"
                type = StringClass
            }
            tabInfoClass?.apply {
                method {
                    name = "fromJSON"
                    paramCount = 1
                }.hook {
                    after {
                        val list = (this.result as List<*>).toMutableList()
                        this.result = list.filter {
                            val tag = tabTagField?.get(it)?.string() ?: return@filter true
                            if (tag.startsWith("native_market_game")) !hideGame
                            else if (tag.startsWith("native_market_rank")) !hideRank
                            else if (
                                tag.startsWith("native_app_assemble") || tag.startsWith("native_market_video")
                            ) !hideAppAssemble
                            else true
                        }.toList()
                    }
                }
            }
        }
    }
}