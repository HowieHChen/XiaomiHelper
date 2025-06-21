package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideAiSearchEntry : YukiBaseHooker() {
    private val getAiSearchEntryMethod by lazy {
        DexKit.findMethodWithCache("ai_search_keyboard") {
            matcher {
                addUsingString("key_ai_search_keyboard_entry")
                returnType = "boolean"
                paramCount = 0
            }
        }
    }
    private val setAiSearchEntryMethod by lazy {
        DexKit.findMethodWithCache("ai_search_keyboard") {
            matcher {
                addUsingString("key_ai_search_keyboard_entry")
                paramTypes("boolean")
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.Browser.HIDE_AI_SEARCH_ENTRY) {
            if (appClassLoader == null) return@hasEnable
            getAiSearchEntryMethod?.getMethodInstance(appClassLoader!!)?.hook {
                replaceToFalse()
            }
            setAiSearchEntryMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    this.args(0).setFalse()
                }
            }
        }
    }
}