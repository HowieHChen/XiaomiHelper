package dev.lackluster.mihelper.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

object ShellUtils {
    data class ShellResult(
        val exitCode: Int,
        val successMsg: String,
        val errorMsg: String
    )
    fun tryExec(command: String, useRoot: Boolean = false, checkSuccess: Boolean = true): ShellResult {
        return execInternal(command, useRoot).also {
            if (checkSuccess && it.exitCode != 0) {
                throw Exception(it.errorMsg)
            }
        }
    }
    fun exec(command: String, useRoot: Boolean = false): ShellResult {
        return execInternal(command, useRoot)
    }
    private fun execInternal(command: String, useRoot: Boolean = false): ShellResult {
        val exitCode: Int
        val successMsg: String
        val errorMsg: String
        var process: Process? = null
        var readerOut: BufferedReader? = null
        var readerErr: BufferedReader? = null
        var stdoutStream: InputStreamReader? = null
        var stderrStream: InputStreamReader? = null
        var outputStream: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (useRoot) "/system/bin/su" else "/system/bin/sh")
            stdoutStream = InputStreamReader(process.inputStream)
            stderrStream = InputStreamReader(process.errorStream)
            readerOut = BufferedReader(stdoutStream)
            readerErr = BufferedReader(stderrStream)
            outputStream = DataOutputStream(process.outputStream)
            outputStream.write(
                command.trimIndent().toByteArray()
            )
            outputStream.writeBytes("\nexit\n")
            outputStream.flush()
            exitCode = process.waitFor()
            val stdout = StringBuilder()
            val stderr = StringBuilder()
            var tempLine: String? = ""
            while (readerOut.readLine()?.also { tempLine = it } != null) {
                stdout.append(tempLine)
            }
            while (readerErr.readLine()?.also { tempLine = it } != null) {
                stderr.append(tempLine)
            }
            successMsg = stdout.toString()
            errorMsg = stderr.toString()
            return ShellResult(exitCode, successMsg, errorMsg)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: Exception) {
            throw e
        } finally {
            try {
                outputStream?.close()
                stdoutStream?.close()
                stderrStream?.close()
                readerOut?.close()
                readerErr?.close()
                process?.destroy()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}