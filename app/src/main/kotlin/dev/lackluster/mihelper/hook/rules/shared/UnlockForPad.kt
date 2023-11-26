package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object UnlockForPad : YukiBaseHooker() {
//    private val isPad by lazy {
//        "miui.os.Build".toClass().field {
//            name = "IS_TABLET"
//        }.get().boolean()
//    }
    override fun onHook() {
        hasEnable(PrefKey.TAPLUS_UNLOCK_PAD, extraCondition = { Device.isPad }) {
            when (packageName) {
                Scope.TAPLUS -> {
                    "com.miui.contentextension.setting.activity.MainSettingsActivity".toClass()
                        .method {
                            name = "getFragment"
                        }
                        .hook {
                            "miui.os.Build".toClass().field {
                                name = "IS_TABLET"
                                modifiers {
                                    isStatic
                                }
                            }.get().set(false)
                        }
                }
                Scope.SETTINGS -> {
                    "com.android.settings.utils.SettingsFeatures".toClass()
                        .method {
                            name = "isNeedRemoveContentExtension"
                        }
                        .hook {
                            replaceToFalse()
                        }
                }
            }
        }
    }
}