package dev.lackluster.mihelper.hook.rules.systemui

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object IconPosition : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.STATUSBAR_SWAP_MOBILE_WIFI) {
            val signalIcons: Array<String> = arrayOf(
                "wifi",
                "demo_wifi",
                "airplane",
                "hd",
                "mobile",
                "demo_mobile",
                "no_sim"
            )
            val signalRelatedIcons = ArrayList(listOf<String>(*signalIcons))
            "com.android.systemui.statusbar.phone.StatusBarIconList".toClass()
                .constructor {
                    paramCount = 1
                }
                .hook {
                    before {
                        val isRightController = "StatusBarIconList" == this.instance.javaClass.simpleName
                        if (isRightController) {
                            val allStatusIcons = ArrayList<String>(mutableListOf<String>(*this.args(0).array<String>()))
                            var startIndex = allStatusIcons.indexOf("airplane")
                            val endIndex = allStatusIcons.indexOf("demo_wifi") + 1
                            val removedIcons = allStatusIcons.subList(startIndex, endIndex)
                            removedIcons.clear()
                            startIndex = allStatusIcons.indexOf("phone") + 1
                            allStatusIcons.addAll(startIndex, signalRelatedIcons)
                            this.args(0).set(allStatusIcons.toTypedArray<String>())
                        }
                    }
                }
        }

    }
}