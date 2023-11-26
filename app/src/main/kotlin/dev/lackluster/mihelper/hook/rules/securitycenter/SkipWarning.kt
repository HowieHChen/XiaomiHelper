package dev.lackluster.mihelper.hook.rules.securitycenter

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object SkipWarning : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_SKIP_WARNING) {
            "android.widget.TextView".toClass().apply {
                method {
                    name = "setEnabled"
                    param(BooleanType)
                }.hook {
                    before {
                        this.args(0).set(true)
                    }
                }
                 method {
                     name = "setText"
                     paramCount = 4
                 }.hook {
                     before {
                         if (this.args.isNotEmpty()) {
                             val buttonText = this.args(0).string()
                             if (buttonText.startsWith("确定（"))
                                 this.args(0).set("确定")
                             else if (buttonText.startsWith("允许（"))
                                 this.args(0).set("允许")
                             else if (buttonText.startsWith("下一步（"))
                                 this.args(0).set("下一步")
                             else if (buttonText.startsWith("OK ("))
                                 this.args(0).set("OK")
                             else if (buttonText.startsWith("Accept ("))
                                 this.args(0).set("Accept")
                             else if (buttonText.startsWith("Next step ("))
                                 this.args(0).set("Next step")
                         }
                     }
                 }
            }
        }
    }
}