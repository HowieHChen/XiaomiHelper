package dev.lackluster.mihelper.hook.rules.aiengine

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.factory.hasEnable

object CopyWebsite : YukiBaseHooker() {
//    private const val SMART_PASS_WORD_XIAOMI_BROWSER = 11

    override fun onHook() {
        hasEnable(Pref.Key.AIEngine.COPY_LINK_CUSTOM_BROWSER) {
            "com.xiaomi.aicr.copydirect.util.SmartPasswordUtils".toClassOrNull()?.apply {
                resolve().firstMethodOrNull {
                    name = "jumpToXiaoMiBrowser"
                }?.hook {
                    replaceUnit {
                        val context = this.args(0).cast<Context>() ?: return@replaceUnit
                        val url = this.args(1).cast<String>()?.let {
                            if (it.startsWith("http://") || it.startsWith("https://")) it
                            else "https://$it"
                        } ?: return@replaceUnit
                        Intent(Intent.ACTION_VIEW, url.toUri()).let { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}