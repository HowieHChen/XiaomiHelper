package dev.lackluster.mihelper.hook.rules.packageinstaller

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object RemoveReport : YukiBaseHooker() {
    private val initMethod by lazy {
        DexKit.dexKitBridge.findMethod {
            matcher {
                addUsingString("findViewById(R.id.loading_icon_container)", StringMatchType.Equals)
            }
        }.singleOrNull()
    }
    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(PrefKey.PACKAGE_REMOVE_REPORT) {
            initMethod?.getMethodInstance(appClassLoader ?: return@hasEnable)?.hook {
                after {
                    val activity = this.instance as Activity
                    val reportButton = activity.findViewById<ImageView>(
                        activity.resources.getIdentifier("feedback_icon","id", activity.packageName)
                    )
                    reportButton.visibility = View.GONE
                }
            }
        }
    }
}