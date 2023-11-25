package dev.lackluster.mihelper.hook.rules.taplus

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object HideShop : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.TAPLUS_HIDE_SHOP) {
            "com.miui.contentextension.text.cardview.TaplusRecognitionExpandedImageCard".toClass()
                .method {
                    name = "updateLayout"
                }
                .hook {
                    after {
                        val shopping = this.instance.current().field {
                            name = "mShopping"
                            type = TextView::class.java
                        }.any() as TextView
                        val scanQR = this.instance.current().field {
                            name = "mScanQR"
                        }.any() as TextView
                        shopping.visibility = View.GONE
                        val layoutParams = scanQR.layoutParams as LinearLayout.LayoutParams
                        layoutParams.marginEnd = layoutParams.marginEnd * 2
                        scanQR.layoutParams = layoutParams
                    }
                }
        }
    }
}