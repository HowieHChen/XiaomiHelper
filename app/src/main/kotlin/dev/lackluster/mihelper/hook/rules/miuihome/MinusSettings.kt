package dev.lackluster.mihelper.hook.rules.miuihome

import android.content.Intent
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object MinusSettings : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_MINUS_RESTORE_SETTING) {
            val clazzUtilities = "com.miui.home.launcher.common.Utilities".toClass()
            val clazzLauncher = "com.miui.home.launcher.Launcher".toClass()
            val clazzMiuiHomeSettings = "com.miui.home.settings.MiuiHomeSettings".toClass()
            "com.miui.home.launcher.DeviceConfig".toClass()
                .method {
                    name = "isUseGoogleMinusScreen"
                }
                .hook {
                    before {
                        "com.miui.home.launcher.LauncherAssistantCompat".toClass().field {
                            name = "CAN_SWITCH_MINUS_SCREEN"
                            modifiers { isStatic }
                        }.get().setTrue()
                    }
                }
            "com.miui.home.launcher.LauncherAssistantCompat".toClass()
                .method {
                    name = "newInstance"
                    param(clazzLauncher.name)
                }
                .hook {
                    before {
                        val isPersonalAssistantGoogle = clazzUtilities.method {
                            name = "getCurrentPersonalAssistant"
                        }.get().string() == "personal_assistant_google"
                        "miui.os.Build".toClass().field {
                            name = "IS_INTERNATIONAL_BUILD"
                            modifiers { isStatic }
                        }.get().set(isPersonalAssistantGoogle)
                    }
                    after {
                        "miui.os.Build".toClass().field {
                            name = "IS_INTERNATIONAL_BUILD"
                            modifiers { isStatic }
                        }.get().setFalse()
                    }
                }
            clazzLauncher.constructor().hook {
                before {
                    "miui.os.Build".toClass().field {
                        name = "IS_INTERNATIONAL_BUILD"
                        modifiers { isStatic }
                    }.get().setTrue()
                }
                after {
                    "miui.os.Build".toClass().field {
                        name = "IS_INTERNATIONAL_BUILD"
                        modifiers { isStatic }
                    }.get().setFalse()
                }
            }
            clazzMiuiHomeSettings.apply {
                method {
                    name = "onCreatePreferences"
                    param(BundleClass, StringClass)
                }.hook {
                    after {
                        val mSwitchPersonalAssistant = this.instance.current().field {
                            name = "mSwitchPersonalAssistant"
                        }.any() ?: return@after
                        mSwitchPersonalAssistant.current().method {
                            name = "setIntent"
                            superClass()
                        }.call(Intent("com.miui.home.action.LAUNCHER_PERSONAL_ASSISTANT_SETTING"))
                        mSwitchPersonalAssistant.current().method {
                            name = "setOnPreferenceChangeListener"
                            superClass()
                        }.call(this.instance)
                        this.instance.current().method {
                            name = "getPreferenceScreen"
                            superClass()
                        }.call()?.current()?.method {
                            name = "addPreference"
                            superClass()
                        }?.call(mSwitchPersonalAssistant)
                    }
                }
                method {
                    name = "onResume"
                }.hook {
                    after {
                        this.instance.current().field {
                            name = "mSwitchPersonalAssistant"
                        }.any()?.current()?.method {
                            name = "setVisible"
                            superClass()
                        }?.call(true)
                    }
                }
            }
        }
    }
}