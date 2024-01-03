package dev.lackluster.mihelper.hook.rules.securitycenter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.CharSequenceClass
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object SkipOpenApp : YukiBaseHooker() {
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(PrefKey.SECURITY_SKIP_OPEN_APP) {
            "android.widget.TextView".toClass()
                .method {
                    name = "setText"
                    param(CharSequenceClass)
                }
                .hook {
                    after {
                        val textView = this.instance as TextView
                        if (this.args.isNotEmpty() && (this.args(0).any() as? SpannableString ?: return@after).toString() == textView.context.resources.getString(
                            textView.context.resources.getIdentifier(
                                "button_text_accept",
                                "string",
                                textView.context.packageName
                            )
                        )
                        ) {
                            textView.performClick()
                        }
                    }
                }
        }
    }
}