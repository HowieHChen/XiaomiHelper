@file:SuppressLint("DiscouragedApi")

package dev.lackluster.mihelper.hook.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import com.highcapable.kavaref.KavaRef.Companion.resolve

abstract class ContextAwareHooker : StaticHooker() {
    abstract val targetPackage: String

    private var isInitialized = false

    private val metAttachBaseContext by lazy {
        "android.app.Application".toClass().resolve().firstMethod {
            name = "attachBaseContext"
            parameters(Context::class)
            superclass()
        }
    }

    final override fun onHook() {
        metAttachBaseContext.hook {
            if (!isInitialized) {
                val baseContext = getArg(0) as? Context
                if (baseContext != null) {
                    val scope = ContextScope(baseContext, targetPackage)
                    scope.onReady()
                    isInitialized = true
                }
            }
            result(proceed())
        }
    }

    abstract fun ContextScope.onReady()
}

class ContextScope(val context: Context, val pkgName: String) {
    val res: Resources get() = context.resources

    fun String.toId(): Int = res.getIdentifier(this, "id", pkgName)
    fun String.toArrayId(): Int = res.getIdentifier(this, "array", pkgName)
    fun String.toBoolId(): Int = res.getIdentifier(this, "bool", pkgName)
    fun String.toDimenId(): Int = res.getIdentifier(this, "dimen", pkgName)
    fun String.toDrawableId(): Int = res.getIdentifier(this, "drawable", pkgName)
    fun String.toLayoutId(): Int = res.getIdentifier(this, "layout", pkgName)
    fun String.toStringId(): Int = res.getIdentifier(this, "string", pkgName)
    fun String.toStyleId(): Int = res.getIdentifier(this, "style", pkgName)

    fun String.getString(): String = res.getString(this.toStringId())

    fun String.getDimen(): Float = res.getDimension(this.toDimenId())

    fun Int.dp2px(): Int = (this * res.displayMetrics.density + 0.5f).toInt()
}