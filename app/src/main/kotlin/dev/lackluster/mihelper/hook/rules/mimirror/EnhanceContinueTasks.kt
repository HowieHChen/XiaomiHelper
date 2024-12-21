package dev.lackluster.mihelper.hook.rules.mimirror

import android.app.Application
import androidx.core.graphics.drawable.toBitmap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BitmapClass
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.DexKit
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable
import org.luckypray.dexkit.query.enums.StringMatchType

object EnhanceContinueTasks : YukiBaseHooker() {
    private val mirrorClas by lazy {
        "com.xiaomi.mirror.Mirror".toClass()
    }
    private val lyraUtilsClass by lazy {
        DexKit.findClassWithCache("lyra_utils") {
            matcher {
                addUsingString("LyraUtils", StringMatchType.Equals)
            }
        }
    }

    override fun onHook() {
        hasEnable(Pref.Key.MiMirror.ENHANCE_CONTINUE_TASKS, extraCondition = {
            Prefs.getBoolean(Pref.Key.MiMirror.CONTINUE_ALL_TASKS, false)
        }) {
            if (appClassLoader == null) return@hasEnable
            val mirrorInstance = mirrorClas.method {
                returnType = mirrorClas
                modifiers { isStatic }
            }.get(null)
            lyraUtilsClass?.getInstance(appClassLoader!!)?.apply {
                method {
                    returnType = BitmapClass
                }.hook {
                    before {
                        val uri = this.args(0).string()
                        if (uri.isBlank()) return@before
                        if (uri.startsWith("pkg://")) {
                            val pkg = uri.replace("pkg://", "")
                            val application = mirrorInstance.call() as? Application ?: return@before
                            this.result = application.applicationContext.packageManager.getApplicationIcon(pkg).toBitmap()
                        }
                    }
                }
            }
        }
    }
}