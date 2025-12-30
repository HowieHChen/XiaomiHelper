package dev.lackluster.mihelper.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

object HostExecutor {
    private val executor: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val taskMap = ConcurrentHashMap<Any, TaskToken>()

    private class TaskToken {
        @Volatile var future: Future<*>? = null
    }

    fun <T> execute(
        tag: Any,
        backgroundTask: () -> T?,
        runOnMain: Boolean = true,
        onResult: ((T) -> Unit)? = null
    ) {
        val oldToken = taskMap[tag]
        oldToken?.future?.cancel(true)

        val newToken = TaskToken()
        // 先占座。如果此时有更更惨的任务C来了覆盖了位置，没关系，下面会处理
        taskMap[tag] = newToken

        val future = executor.submit {
            if (Thread.currentThread().isInterrupted) return@submit

            try {
                if (taskMap[tag] != newToken) return@submit

                val result = backgroundTask()

                if (result == null) {
                    taskMap.remove(tag, newToken)
                    return@submit
                }

                if (Thread.currentThread().isInterrupted) return@submit

                if (taskMap[tag] != newToken) return@submit

                if (onResult != null) {
                    if (runOnMain) {
                        mainHandler.post {
                            if (taskMap[tag] == newToken) {
                                onResult(result)
                                taskMap.remove(tag, newToken)
                            }
                        }
                        return@submit
                    } else {
                        onResult(result)
                    }
                }
                taskMap.remove(tag, newToken)
            } catch (_: InterruptedException) {

            } catch (e: Exception) {
                e.printStackTrace()
                taskMap.remove(tag, newToken)
            }
        }

        newToken.future = future
    }
}