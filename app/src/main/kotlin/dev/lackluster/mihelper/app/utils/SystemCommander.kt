package dev.lackluster.mihelper.app.utils

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

object SystemCommander {
    val hasRootPrivilege: Boolean
        get() = Shell.isAppGrantedRoot() == true

    fun requireRootAccess(): Boolean {
        return Shell.getShell().isRoot
    }

    data class CommandResult(
        val exitCode: Int,
        val out: String,
        val err: String,
        val isSuccess: Boolean
    )

    fun exec(
        command: String,
        useRoot: Boolean = true,
        silent: Boolean = false
    ): CommandResult {

        val cmdResult = if (useRoot) {
            val result = Shell.cmd(command).exec()
            CommandResult(
                exitCode = result.code,
                out = result.out.joinToString("\n").trim(),
                err = result.err.joinToString("\n").trim(),
                isSuccess = result.isSuccess
            )
        } else {
            val process = ProcessBuilder("sh", "-c", command).start()
            var errStr = ""
            val errThread = thread {
                errStr = process.errorStream.bufferedReader().readText().trim()
            }
            val outStr = process.inputStream.bufferedReader().readText().trim()
            errThread.join()
            val code = process.waitFor()
            CommandResult(
                exitCode = code,
                out = outStr,
                err = errStr,
                isSuccess = (code == 0)
            )
        }

        if (!silent && !cmdResult.isSuccess) {
            val errorDetails = cmdResult.err.ifBlank {
                "Exit Code: ${cmdResult.exitCode}\nOutput: ${cmdResult.out}"
            }
            throw IllegalStateException("SystemCommander Execution Failed!\nCommand: [$command]\nError: $errorDetails")
        }

        return cmdResult
    }

    suspend fun execAsync(
        command: String,
        useRoot: Boolean = true,
        silent: Boolean = false
    ): CommandResult = withContext(Dispatchers.IO) {
        val cmdResult = if (useRoot) {
            val result = Shell.cmd(command).exec()
            CommandResult(
                exitCode = result.code,
                out = result.out.joinToString("\n").trim(),
                err = result.err.joinToString("\n").trim(),
                isSuccess = result.isSuccess
            )
        } else {
            val process = ProcessBuilder("sh", "-c", command).start()
            val errDeferred = async {
                process.errorStream.bufferedReader().readText().trim()
            }
            val outDeferred = async {
                process.inputStream.bufferedReader().readText().trim()
            }
            val errStr = errDeferred.await()
            val outStr = outDeferred.await()
            val code = process.waitFor()
            CommandResult(
                exitCode = code,
                out = outStr,
                err = errStr,
                isSuccess = (code == 0)
            )
        }

        if (!silent && !cmdResult.isSuccess) {
            val errorDetails = cmdResult.err.ifBlank {
                "Exit Code: ${cmdResult.exitCode}\nOutput: ${cmdResult.out}"
            }
            throw IllegalStateException("SystemCommander Execution Failed!\nCommand: [$command]\nError: $errorDetails")
        }

        return@withContext cmdResult
    }
}