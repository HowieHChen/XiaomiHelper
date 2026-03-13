package dev.lackluster.mihelper.hook.rules.systemui.plugin

import android.content.ComponentName
import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.mihelper.data.Scope

object PluginFactory : YukiBaseHooker() {
    private const val PACKAGE_NAME_PLUGIN = Scope.SYSTEM_UI_PLUGIN
    private const val CLASS_NAME_FOCUS_NOTIFICATION = "miui.systemui.notification.FocusNotificationPluginImpl"
    private const val CLASS_NAME_CONTROL_CENTER = "miui.systemui.controlcenter.MiuiControlCenter"

    override fun onHook() {
        $$"com.android.systemui.shared.plugins.PluginInstance$PluginFactory".toClassOrNull()?.apply {
            val fldComponentName = resolve().firstFieldOrNull {
                name = "mComponentName"
            }
            resolve().firstMethodOrNull {
                name = "createPluginContext"
            }?.hook {
                after {
                    val context = this.result<Context>()
                    val classLoader = context?.classLoader ?: return@after
                    val componentName = fldComponentName?.copy()?.of(this.instance)?.get<ComponentName>()
                    if (componentName?.packageName == PACKAGE_NAME_PLUGIN) {
                        YLog.info(componentName.className)
                        when (componentName.className) {
                            CLASS_NAME_FOCUS_NOTIFICATION -> {
                                listOf(
                                    IslandWhitelist,
                                ).forEach { hooker ->
                                    hooker.appClassLoader = classLoader
                                    loadHooker(hooker)
                                }
                            }
                            CLASS_NAME_CONTROL_CENTER -> {
                                listOf(
                                    HideEditButton,
                                ).forEach { hooker ->
                                    hooker.appClassLoader = classLoader
                                    loadHooker(hooker)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}