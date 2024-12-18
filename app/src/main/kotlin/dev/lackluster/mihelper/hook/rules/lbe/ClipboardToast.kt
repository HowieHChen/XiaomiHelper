package dev.lackluster.mihelper.hook.rules.lbe

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.utils.factory.hasEnable

object ClipboardToast : YukiBaseHooker() {
    private var overlay_read_clip_toast = 0

    @SuppressLint("DiscouragedApi")
    override fun onHook() {
        hasEnable(Pref.Key.LBE.CLIPBOARD_TOAST) {
            "com.lbe.security.utility.ToastUtil".toClassOrNull()?.apply {
                method {
                    name = "initToastView"
                }.hook {
                    before {
                        val type = this.args(1).int()
                        if (type == 1) {
                            val mContext = this.instance.current().field {
                                name = "mContext"
                            }.cast<Context>() ?: return@before
                            val pkgName = this.args(0).string()
                            if (overlay_read_clip_toast == 0) {
                                overlay_read_clip_toast = mContext.resources.getIdentifier(
                                    "overlay_read_clip_toast",
                                    "string",
                                    Scope.LBE
                                )
                            }
                            val appName = mContext.packageManager.let {
                                it.getPackageInfo(pkgName, 0).applicationInfo?.loadLabel(it)?.toString()
                            }
                            appName?.let {
                                Toast.makeText(mContext, mContext.getString(overlay_read_clip_toast, appName), Toast.LENGTH_SHORT).show()
                                this.result = null
                            }
                        }
                    }
                }
            }
        }
    }
}