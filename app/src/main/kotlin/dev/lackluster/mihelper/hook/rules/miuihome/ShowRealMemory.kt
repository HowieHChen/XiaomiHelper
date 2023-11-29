package dev.lackluster.mihelper.hook.rules.miuihome

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object ShowRealMemory : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(PrefKey.HOME_SHOW_REAL_MEMORY) {
            lateinit var context: Context
            var memoryInfo1StringId: Int? = null
            var memoryInfo2StringId: Int? = null

            fun Any.formatSize(): String = Formatter.formatFileSize(context, this as Long)

            val recentContainerClass =
                if (Device.isPad) "com.miui.home.recents.views.RecentsDecorations"
                else "com.miui.home.recents.views.RecentsContainer"

            recentContainerClass.toClass()
                .constructor {
                    paramCount = 2
                }
                .hook {
                    after {
                        context = this.args(0).any() as Context
                        memoryInfo1StringId = context.resources.getIdentifier(
                            "status_bar_recent_memory_info1",
                            "string",
                            "com.miui.home"
                        )
                        memoryInfo2StringId = context.resources.getIdentifier(
                            "status_bar_recent_memory_info2",
                            "string",
                            "com.miui.home"
                        )
                    }
                }

            recentContainerClass.toClass()
                .method {
                    name = "refreshMemoryInfo"
                }
                .hook {
                    before {
                        this.result = null
                        val memoryInfo = ActivityManager.MemoryInfo()
                        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        activityManager.getMemoryInfo(memoryInfo)
                        val totalMem = memoryInfo.totalMem.formatSize()
                        val availMem = memoryInfo.availMem.formatSize()
                        this.instance.current().field {
                            name = "mTxtMemoryInfo1"
                        }.cast<TextView>()?.text = context.getString(memoryInfo1StringId!!, availMem, totalMem)
                        this.instance.current().field {
                            name = "mTxtMemoryInfo2"
                        }.cast<TextView>()?.text = context.getString(memoryInfo2StringId!!, availMem, totalMem)
                    }
                }

        }
    }
}