package dev.lackluster.mihelper.hook.rules.mishare

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit.dexKitBridge
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object NoAutoOff : YukiBaseHooker() {
    private val oldMethod by lazy {
        dexKitBridge.findMethod {
            matcher {
                addUsingString("EnabledState", StringMatchType.Equals)
                addUsingString("mishare_enabled", StringMatchType.Equals)
            }
        }
    }
    private val toastMethod by lazy {
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
        }
    }
    override fun onHook() {
        hasEnable(PrefKey.MISHARE_NO_AUTO_OFF) {
            val oldMethodInstance = oldMethod.map { it.getMethodInstance(appClassLoader?:return@hasEnable ) }.toList()
            val toastMethodInstance = toastMethod.map { it.getMethodInstance(appClassLoader?:return@hasEnable) }.toList()
            oldMethodInstance.hookAll {
                replaceTo(null)
            }
            toastMethodInstance.hookAll {
                before {
                    if (this.args(1).string() == "security_agree") {
                        this.result = false
                    }
                }
            }
        }
    }
}