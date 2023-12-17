package dev.lackluster.mihelper.hook.rules.securitycenter

import android.annotation.SuppressLint
import android.content.Context
import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object RemoveBubbleSettingsRestriction : YukiBaseHooker() {
    @SuppressLint("PrivateApi")
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_BUBBLE_RESTRICTION) {
            "com.miui.bubbles.settings.BubblesSettings".toClass()
                .method {
                    name = "getDefaultBubbles"
                }
                .hook {
                    before {
                        val bubbleAppClz = "com.miui.bubbles.settings.BubbleApp".toClass()
                        val arrayMap = ArrayMap<String, Any>()
                        val context = this.instance.current().field {
                            name = "mContext"
                        }.any() as Context
                        val currentUserId = this.instance.current().field {
                            name = "mCurrentUserId"
                        }.int()
                        val freeformSuggestionList = "android.util.MiuiMultiWindowUtils".toClass().method {
                            name = "getFreeformSuggestionList"
                            param(ContextClass)
                            paramCount = 1
                            modifiers { isStatic }
                        }.get().list<String>(context)
                        if (freeformSuggestionList.isNotEmpty()) {
                            for (str in freeformSuggestionList) {
                                val bubbleApp = bubbleAppClz.constructor().get().call(str, currentUserId)
                                bubbleApp?.current()?.method {
                                    name = "setChecked"
                                    param(BooleanType)
                                }?.call(true)
                                arrayMap[str] = bubbleApp
                            }
                        }
                        this.result = arrayMap
                    }
                }
        }
    }
}