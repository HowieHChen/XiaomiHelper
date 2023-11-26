package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object AllowSendAllApp : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.MISMARTHUB_ALL_APP) {
            when (packageName) {
                Scope.CASTING -> {
                    "com.xiaomi.mirror.synergy.MiuiSynergySdk".toClass()
                        .method {
                            name = "isSupportSendApp"
                        }
                        .hook {
                            after {
                                this.result = true
                            }
                        }
                }
                Scope.MI_SMART_HUB -> {
                    "com.xiaomi.mirror.message.RelayAppMessage".toClass()
                        .method {
                            returnType = this.current().name
                        }
                        .hookAll {
                            after {
                                this.result?.current()?.field {
                                    name = "isHideIcon"
                                }?.set(false)
                            }
                        }
                    "com.xiaomi.mirror.settings.micloud.MiCloudUtils".toClass()
                        .method {
                            name = "isSupportSubScreen"
                        }
                        .hook {
                            replaceToTrue()
                        }
                }
            }
        }
    }
}