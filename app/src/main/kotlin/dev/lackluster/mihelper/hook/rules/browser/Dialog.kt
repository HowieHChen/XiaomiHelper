package dev.lackluster.mihelper.hook.rules.browser

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object Dialog : YukiBaseHooker() {
    override fun onHook() {
        if (appClassLoader == null) return
        hasEnable(Pref.Key.Browser.BLOCK_DIALOG) {
            "com.android.browser.Controller".toClassOrNull()?.apply {
                setOf(
                    "showHotListWidgetAddDialog",
                    "showChildProtectDialog",
                    "showShortcutDialog",
                    "showCommonWidgetAddDialog",
                ).forEach { methodName ->
                    resolve().firstMethodOrNull {
                        name = methodName
                    }?.hook {
                        intercept()
                    }
                }
            }
            "com.android.browser.util.AiSearchScanUtil".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "showScanScanGuideDialog"
                }?.hook {
                    intercept()
                }
            }
        }
    }
}