package dev.lackluster.mihelper.hook.rules.mishare

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit.dexKitBridge
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Modifier

object NoAutoOff : YukiBaseHooker() {
    private val autoTurnNull1 by lazy {
        dexKitBridge.findMethod {
            matcher {
                addUsingNumber(600000)
                paramCount = 0
                modifiers = Modifier.PUBLIC
            }
        }.firstOrNull()
    }
    private val autoTurnNull2 by lazy {
        dexKitBridge.findMethod {
            matcher {
                addUsingNumber(600000)
                paramCount = 0
                modifiers = Modifier.PRIVATE
            }
        }.firstOrNull()
    }
    private val shareSa by lazy {
        dexKitBridge.findMethod {
            matcher {
                paramCount = 2
                addCall {
                    addUsingNumber(600000)
                    paramCount = 2
                }
            }
        }.firstOrNull()
    }
    private val checkRunJobsClazz by lazy {
        dexKitBridge.findClass {
            matcher {
                addUsingString("no more running jobs, will release after", StringMatchType.Equals)
            }
        }.firstOrNull()
    }
    private val shareToast by lazy {
        dexKitBridge.findMethod {
            matcher {
                declaredClass {
                    addUsingString("null context", StringMatchType.Equals)
                    addUsingString("cta_agree", StringMatchType.Equals)
                }
                returnType = "boolean"
                paramTypes = listOf("android.content.Context", "java.lang.String")
                paramCount = 2
            }
        }.firstOrNull()
    }
    private val shareToast2 by lazy {
        dexKitBridge.findMethod {
            matcher {
                returnType = "void"
                paramTypes = listOf("android.content.Context", "java.lang.CharSequence", "int")
                paramCount = 3
                modifiers = Modifier.STATIC
            }
        }.firstOrNull()
    }
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(PrefKey.MISHARE_NO_AUTO_OFF) {
            autoTurnNull1?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                intercept()
            }
            autoTurnNull2?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                before {
                    (this.current().field {
                        type = HandlerClass
                    }.any() as? Handler)?.removeCallbacksAndMessages(null)
                }
            }
            shareSa?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                intercept()
            }
            checkRunJobsClazz?.current()?.field {
                type = IntType
                modifiers { isStatic }
            }
            shareToast?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                before {
                    if (this.args(1).string() == "security_agree") {
                        this.result = false
                    }
                }
            }
            shareToast2?.getMethodInstance(appClassLoader?:return@hasEnable)?.hook {
                before {
                    val context = this.args(0).any() as Context
                    val stringSecurityAgree = String.format(context.getString(
                        context.resources.getIdentifier("toast_auto_close_in_minutes", "string", context.packageName)
                    ), 10)
                    if (this.args(1).any().toString() == stringSecurityAgree) {
                        this.result = null
                    }
                }
            }
        }
    }
}