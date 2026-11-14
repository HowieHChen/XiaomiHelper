@file:SuppressLint("DiscouragedApi")

package dev.lackluster.mihelper.hook.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import com.highcapable.kavaref.KavaRef.Companion.resolve
import dev.lackluster.mihelper.hook.utils.e

/**
 * 每个实例取得一次目标应用的 Context，供 onReady 初始化资源缓存。
 * 这不代表 Application 已完成启动；onReady 不随功能启停重复执行。
 * 子类若在 onReady 中创建监听或任务，仍需自行明确其清理生命周期和能力声明。
 */
abstract class ContextAwareHooker : BaseHooker() {
    abstract val targetPackage: String

    private var resourcesInitialized = false
    private var initializingResources = false

    private val metAttachBaseContext by lazy {
        "android.app.Application".toClass().resolve().firstMethod {
            name = "attachBaseContext"
            parameters(Context::class)
            superclass()
        }
    }

    final override fun onInit() {
        // 在 onInit 注册，归入实例生命周期：普通 stop 不移除，dispose 由 BaseHooker 清理。
        // 先监听再查询，兼顾 Application 尚未创建和已经存在的情况。
        metAttachBaseContext.hook(id = "$hookerId.attachBaseContext") {
            // 保留原先的调用时机，确保宿主后续初始化中的 Hook 回调能使用资源 ID。
            (getArg(0) as? Context)?.let(::initializeResourcesOnce)
            result(proceed())
        }

        // 热重载只重建模块实例，不会重放宿主 attachBaseContext；必须主动查询已有对象。
        // 在 onInit 内同步完成，父节点随后 attach 的业务节点才能读到本代资源缓存。
        findExistingHostContext()?.let(::initializeResourcesOnce)
    }

    private fun findExistingHostContext(): Context? = try {
        "android.app.ActivityThread".toClassOrNull()?.resolve()?.firstMethodOrNull {
            name = "currentApplication"
        }?.invoke() as? Context
    } catch (failure: ReflectiveOperationException) {
        e(failure) { "Failed to query existing Application for $targetPackage; waiting for attachBaseContext" }
        null
    }

    private fun initializeResourcesOnce(context: Context) = synchronized(session.lock) {
        // 生命周期、启动事件和已有 Application 查询共用一把锁；volatile 无法保证只初始化一次。
        // initializingResources 另外防止 onReady 间接触发宿主加载时在同一线程递归进入。
        if (!isActive || resourcesInitialized || initializingResources) return@synchronized
        if (context.packageName != targetPackage) return@synchronized
        initializingResources = true
        try {
            with(ContextScope(context, targetPackage)) { onReady() }
            resourcesInitialized = true
        } finally {
            initializingResources = false
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
