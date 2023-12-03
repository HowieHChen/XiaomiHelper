package dev.lackluster.mihelper.hook.rules.miuihome

import android.appwidget.AppWidgetProviderInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object WidgetResizable : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_WIDGET_RESIZABLE) {
            "android.appwidget.AppWidgetHostView".toClass(null)
                .method {
                    name = "getAppWidgetInfo"
                    returnType = "android.appwidget.AppWidgetProviderInfo"
                }
                .hookAll {
                    after {
                        val widgetInfo = (this.result ?:return@after) as AppWidgetProviderInfo
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