package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.app.Dialog
import android.content.Context
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.DialogClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object RemoveElement : YukiBaseHooker() {
    private val menuCreateClass1 by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("FullSafeStrategyType", StringMatchType.Contains)
            }
        }
    }
    private val onCreateOptionsMenuMethod1 by lazy {
        DexKit.findMethodWithCache("create_options_menu1") {
            matcher {
                name = "onCreateOptionsMenu"
                returnType = "boolean"
            }
            searchClasses = menuCreateClass1
        }
    }
    private val menuCreateClass2 by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("R.id.main_content", StringMatchType.Contains)
                addUsingString("dark_loading.json", StringMatchType.Equals)
            }
        }
    }
    private val onCreateOptionsMenuMethod2 by lazy {
        DexKit.findMethodWithCache("create_options_menu2") {
            matcher {
                name = "onCreateOptionsMenu"
                returnType = "boolean"
            }
            searchClasses = menuCreateClass2
        }
    }
    private val showPopupMethod by lazy {
        DexKit.findMethodWithCache("safe_mode_show_popup") {
            matcher {
                addUsingString("null cannot be cast to non-null type com.miui.packageInstaller.analytics.IPage" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_open_btn" ,StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn" ,StringMatchType.Equals)
            }
        }
    }
    private val cancelClickMethod by lazy {
        DexKit.findMethodWithCache("safe_mode_popup_cancel") {
            matcher {
                addUsingString("pure_mode_guide_dialog_day_finish", StringMatchType.Equals)
                addUsingString("is_remember", StringMatchType.Equals)
                addUsingString("safe_mode_guidance_popup_cancel_btn", StringMatchType.Equals)
                modifiers = Modifier.STATIC
            }
        }
    }
    private val miuixDialogClass by lazy {
        DexKit.findClassWithCache("miuix_dialog") {
            matcher {
                className("miuix.appcompat.app", StringMatchType.StartsWith)
                addField {
                    type = "miuix.appcompat.app.AlertController"
                    modifiers = Modifier.FINAL
                }
                addUsingString("android.ui", StringMatchType.Equals)
                addUsingString("android.imms", StringMatchType.Equals)
                addUsingString("system_server", StringMatchType.Equals)
            }
        }
    }
    private val safeModeTipViewObjectClass by lazy {
        "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject".toClassOrNull()
    }
    private val safeModeTipViewObjectViewHolderClass by lazy {
        "com.miui.packageInstaller.ui.listcomponets.SafeModeTipViewObject\$ViewHolder".toClass()
    }

    override fun onHook() {
        hasEnable(Pref.Key.PackageInstaller.REMOVE_ELEMENT) {
            if (appClassLoader == null) return@hasEnable
            onCreateOptionsMenuMethod1?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val menu = this.args(0).cast<Menu>() ?: return@after
                    menu.findItem(ResourcesUtils.feedback)?.setVisible(false)
                }
            }
            onCreateOptionsMenuMethod2?.getMethodInstance(appClassLoader!!)?.hook {
                after {
                    val menu = this.args(0).cast<Menu>() ?: return@after
                    menu.findItem(ResourcesUtils.feedback)?.setVisible(false)
                }
            }
            safeModeTipViewObjectClass?.method {
                param(safeModeTipViewObjectViewHolderClass)
                paramCount = 1
            }?.ignored()?.hookAll {
                after {
                    val viewHolder = this.args(0).any()
                    (viewHolder?.current()?.method {
                        name = "getClContentView"
                    }?.call() as? View)?.visibility = View.GONE
                }
            }
            val cancelClickInstance = cancelClickMethod?.getMethodInstance(appClassLoader!!)
            val miuixDialogClz = miuixDialogClass?.getInstance(appClassLoader!!) ?: DialogClass
            showPopupMethod?.getMethodInstance(appClassLoader!!)?.hook {
                before {
                    val dialog = this.instance.current().field {
                        type = miuixDialogClz
                    }.any() as? Dialog
                    val context = this.instance.current().field { type = ContextClass }.any() as? Context
                    if (dialog == null || context == null) return@before
                    if (cancelClickInstance == null) {
                        dialog.dismiss()
                        YLog.error("[PackageInstaller] Can't click the negative button of the dialog")
                    } else {
                        val checkBox = CheckBox(context).apply {
                            isChecked = true
                        }
                        checkBox.isChecked = true
                        cancelClickInstance.invoke(null, this.instance, checkBox, dialog, -2)
                    }
                    this.result = null
                }
            }
        }
    }
}