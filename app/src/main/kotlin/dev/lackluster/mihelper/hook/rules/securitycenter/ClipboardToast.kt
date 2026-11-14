package dev.lackluster.mihelper.hook.rules.securitycenter

import android.content.Context
import android.view.View
import android.widget.Toast
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.data.preference.Preferences
import dev.lackluster.mihelper.hook.base.BaseHooker
import dev.lackluster.mihelper.hook.rules.securitycenter.ResourcesUtils.overlay_read_clip_toast
import dev.lackluster.mihelper.hook.utils.DexKit
import dev.lackluster.mihelper.hook.utils.RemotePreferences.observe
import dev.lackluster.mihelper.hook.utils.toTyped
import org.luckypray.dexkit.query.enums.StringMatchType

object ClipboardToast : BaseHooker() {
    override val supportsHotReload: Boolean = true
    override val supportsRuntimeToggle: Boolean = true

    private val clzToastUtil by lazy {
        DexKit.findClassWithCache("clipboard_toast_util") {
            matcher {
                addUsingString("ToastUtil", StringMatchType.Equals)
                addUsingString("permission.intent.action.CLIPBOARD_CLOSE_TIP", StringMatchType.Equals)
            }
        }
    }

    override fun onInit() {
        registerDisposeCleanup(
            Preferences.LBE.TOAST_CLIPBOARD_USAGE.observe {
                updateSelfState(it)
            }
        )
        clzToastUtil
    }

    override fun onStart() {
        clzToastUtil?.getInstance(classLoader)?.apply {
            val mContext = resolve().firstFieldOrNull {
                type(Context::class)
            }?.toTyped<Context>()
            resolve().firstMethodOrNull {
                parameters(String::class, Int::class)
                returnType(View::class)
            }?.hook {
                val type = getArg(1) as? Int
                if (type == 1) {
                    val context = mContext?.get(thisObject)
                    val pkgName = getArg(0) as? String
                    if (context != null && pkgName != null && overlay_read_clip_toast != 0) {
                        context.packageManager.let {
                            it.getPackageInfo(pkgName, 0).applicationInfo?.loadLabel(it)?.toString()
                        }?.let {
                            Toast.makeText(
                                context,
                                context.getString(overlay_read_clip_toast, it),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@hook result(null)
                        }
                    }
                }
                result(proceed())
            }
        }
    }
}