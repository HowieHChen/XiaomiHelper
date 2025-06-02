package dev.lackluster.mihelper.hook.rules.systemui

import android.annotation.SuppressLint
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.mihelper.data.Pref

object FuckStatusBarGestures : YukiBaseHooker() {
    private val centralSurfacesImplClass by lazy {
        "com.android.systemui.statusbar.phone.CentralSurfacesImpl".toClassOrNull()
    }
    private val needHook by lazy {
        centralSurfacesImplClass?.field {
            name = "mGestureRec"
        }?.ignored()?.give() != null
    }

    @SuppressLint("SdCardPath")
    override fun onHook() {
        if (SafeSP.getBoolean(Pref.Key.SystemUI.FUCK_GESTURES_DAT) && needHook) {
            centralSurfacesImplClass?.apply {
                constructor().hook {
                    after {
                        val mGestureRec = this.instance.current().field {
                            name = "mGestureRec"
                        }.any() ?: return@after
                        val mLogfile = mGestureRec.current().field {
                            name = "mLogfile"
                        }.any() as? String ?: return@after
                        val path = mLogfile.substring(0, mLogfile.lastIndexOf('/'))
                        if (path.endsWith("/sdcard")) {
                            val newPath = "${path}/MIUI/"
                            val file = mLogfile.substring(mLogfile.lastIndexOf('/') + 1)
                            mGestureRec.current().field {
                                name = "mLogfile"
                            }.set("${newPath}${file}")
                        }
                    }
                }
            }
        }
    }
}