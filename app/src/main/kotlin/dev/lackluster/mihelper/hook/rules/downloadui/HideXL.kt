package dev.lackluster.mihelper.hook.rules.downloadui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.android.TextViewClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object HideXL : YukiBaseHooker() {
    private val fragmentClass by lazy {
        DexKit.findClassWithCache("fragment") {
            matcher {
                addUsingString("tab_index", StringMatchType.Equals)
                addUsingString("Alt+Enter", StringMatchType.Equals)
                superClass("miuix.appcompat.app.Fragment")
            }
        }
    }
    private val downloadListDelegateClass by lazy {
        DexKit.dexKitBridge.getClassData("com.android.providers.downloads.ui.DownloadListDelegate")
    }
    private val actionBarInitMethod by lazy {
        DexKit.findMethodWithCache("action_bar_init") {
            matcher {
                returnType = "void"
                paramCount = 4
                paramTypes("android.app.Activity" ,"android.view.Window", null, "android.widget.ImageView")
                addUsingString("not found ACTION_BAR_MOVABLE_CONTAINER ", StringMatchType.Equals)
            }
            searchClasses = downloadListDelegateClass?.let { listOf(it) }
        }
    }
    override fun onHook() {
        hasEnable (Pref.Key.DownloadUI.HIDE_XL) {
            if (appClassLoader == null) return@hasEnable
            actionBarInitMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    this.args(3).setNull()
                }
                after {
                    val xlTextFiled = this.instance.current().field {
                        type = TextViewClass
                    }
                    xlTextFiled.cast<TextView>()?.apply {
                        this.parent?.let {
                            if (it is ViewGroup) {
                                it.removeView(this)
                            }
                        }
                    }
                    xlTextFiled.setNull()
                }
            }
            fragmentClass?.getInstance(appClassLoader!!)?.apply {
                method {
                    paramCount = 1
                    param("miuix.appcompat.app.ActionBar")
                }.hook {
                    after {
                        val xlIcon = this.instance.current().field {
                            type = ImageViewClass
                        }.cast<ImageView>() ?: return@after
                        xlIcon.isClickable = false
                        xlIcon.visibility = View.GONE
                    }
                }
            }
        }
    }
}