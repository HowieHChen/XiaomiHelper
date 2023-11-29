package dev.lackluster.mihelper.hook.rules.miuihome

import android.appwidget.AppWidgetProviderInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object WidgetResizable : YukiBaseHooker() {
    private val method2 by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                name = "getAppWidgetInfo"

            }
        }
    }
    override fun onHook() {
        hasEnable(PrefKey.HOME_WIDGET_RESIZABLE) {
            val method1 = "android.appwidget.AppWidgetHostView".toClass(null)
                .method {
                    name = "getAppWidgetInfo"
                }.giveAll().toList()
            val method2Instance = method2.map { it.getMethodInstance(appClassLoader ?: return@hasEnable) }.toList()
            setOf(method1, method2Instance).forEach { methods1 ->
                methods1.filter {
                    it.returnType == AppWidgetProviderInfo::class.java
                }.forEach {
                    it.hook {
                        after {
                            val widgetInfo = this.result as AppWidgetProviderInfo
                            widgetInfo.resizeMode = AppWidgetProviderInfo.RESIZE_VERTICAL or AppWidgetProviderInfo.RESIZE_HORIZONTAL
                            widgetInfo.minHeight = 0
                            widgetInfo.minWidth = 0
                            widgetInfo.minResizeHeight = 0
                            widgetInfo.minResizeWidth = 0
                            this.result = widgetInfo
                        }
                    }
                }
            }
        }
    }
}