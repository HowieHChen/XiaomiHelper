package dev.lackluster.mihelper.hook.rules.shared

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object AllowSendAllApp : YukiBaseHooker() {
    private val subScreen by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("support_all_app_sub_screen", StringMatchType.Equals)
                returnType = "boolean"
            }
        }.firstOrNull()
    }

    private val relayAppMessageClazz by lazy {
        DexKit.dexKitBridge.findClass {
            matcher {
                addUsingString("RelayAppMessage{type=", StringMatchType.Equals)
            }
        }.firstOrNull()
    }

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
                    "com.xiaomi.mirror.message.proto.RelayApp\$RelayApplication".toClass().apply {
                        method {
                            name = "getIsHideIcon"
                        }.hook {
                            replaceToFalse()
                        }
                        method {
                            name = "getSupportHandOff"
                        }.hook {
                            replaceToTrue()
                        }
                        method {
                            name = "getSupportSubScreen"
                        }.hook {
                            replaceToTrue()
                        }
                    }
                    subScreen?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                        replaceToTrue()
                    }
                    val relayAppMessageClz = relayAppMessageClazz?.getInstance(appClassLoader ?: return@hasEnable)
                    val booleans = relayAppMessageClz?.field {
                        type = BooleanType
                    }?.giveAll()
                    booleans?.sortBy {
                        it.name
                    }
                    val fieldName = booleans?.get(1)?.name ?: return@hasEnable
                    relayAppMessageClz.method {
                        returnType = relayAppMessageClz
                    }.hookAll {
                        after {
                            this.result?.current(true)?.field {
                                name = fieldName
                            }?.setFalse()
                        }
                    }
                }
            }
        }
    }
}