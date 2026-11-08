package dev.lackluster.mihelper.hook.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/** 只管理模块自己的任务；shutdown 用于整代退役，关闭后不可重启，普通功能停止应使用 cancel(tag)。 */
object HostExecutor {
    private val taskLock = Any()
    private var executor: ExecutorService? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    // 单独标记本工具投递的消息，退役时只移除自己的排队回调，不影响宿主其他消息。
    private val callbackToken = Any()
    @Volatile private var closed = false
    private val taskMap = ConcurrentHashMap<Any, TaskToken>()

    private class TaskToken {
        @Volatile var future: Future<*>? = null
    }

    fun runOnMain(task: () -> Unit) = synchronized(taskLock) {
        if (closed) return@synchronized
        if (Looper.myLooper() == Looper.getMainLooper()) task()
        else mainHandler.postAtTime({ if (!closed) task() }, callbackToken, SystemClock.uptimeMillis())
        Unit
    }

    fun cancel(tag: Any) = synchronized(taskLock) {
        taskMap.remove(tag)?.future?.cancel(true)
        Unit
    }

    /**
     * 先禁止新任务、使旧结果失效并移除排队回调，再请求线程中断、等待线程池退出。
     * 忽略中断的任务无法安全强杀，超时返回 false，入口必须报告重载失败而非宣称已安全退役。
     * 正在执行的主线程回调不由此等待结束，业务仍需检查自身启用周期并协调宿主写入。
     */
    fun shutdown(timeoutMillis: Long = 5_000): Boolean {
        val previous = synchronized(taskLock) {
            closed = true
            taskMap.values.forEach { it.future?.cancel(true) }
            taskMap.clear()
            mainHandler.removeCallbacksAndMessages(callbackToken)
            executor.also { it?.shutdownNow() }
        }
        return try {
            // 不能持 taskLock 等待，后台任务完成/投递结果时也需要此锁。
            previous == null || previous.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        }
    }

    fun <T> execute(
        tag: Any,
        backgroundTask: () -> T?,
        runOnMain: Boolean = true,
        onResult: ((T) -> Unit)? = null,
    ) = synchronized(taskLock) {
        if (closed) return@synchronized
        cancel(tag)
        // 同名任务之间用 token 区分：即使旧任务未响应中断，也不能再投递结果或删除新任务的记录。
        val token = TaskToken()
        taskMap[tag] = token
        val pool = executor ?: Executors.newFixedThreadPool(4).also { executor = it }
        try {
            token.future = pool.submit {
                var posted = false
                try {
                    if (closed || Thread.currentThread().isInterrupted || taskMap[tag] !== token) return@submit
                    val result = backgroundTask() ?: return@submit
                    if (closed || Thread.currentThread().isInterrupted || taskMap[tag] !== token) return@submit
                    if (onResult != null) {
                        if (runOnMain) {
                            synchronized(taskLock) {
                                if (!closed && taskMap[tag] === token) {
                                    posted = mainHandler.postAtTime({
                                        try {
                                            // 投递后到执行前仍可能被取消或替换，主线程真正执行时再检查一次。
                                            if (!closed && taskMap[tag] === token) onResult(result)
                                        } finally { taskMap.remove(tag, token) }
                                    }, callbackToken, SystemClock.uptimeMillis())
                                }
                            }
                        } else {
                            onResult(result)
                        }
                    }
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (failure: Exception) {
                    failure.printStackTrace()
                } finally {
                    if (!posted) taskMap.remove(tag, token)
                }
            }
        } catch (failure: Throwable) {
            taskMap.remove(tag, token)
            throw failure
        }
    }
}
