package dev.lackluster.mihelper.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

object Shell {
    fun exec(command: String, useRoot: Boolean = false): String {
        var process: Process? = null
        var reader: BufferedReader? = null
        var inputStream: InputStreamReader? = null
        var outputStream: DataOutputStream? = null
        return try {
            process = Runtime.getRuntime().exec(if (useRoot) "su" else "sh")
            inputStream = InputStreamReader(process.inputStream)
            reader = BufferedReader(inputStream)
            outputStream = DataOutputStream(process.outputStream)
            outputStream.write(
                command.trimIndent().toByteArray()
            )
            outputStream.writeBytes("\nexit\n")
            outputStream.flush()
            var read: Int
            val buffer = CharArray(4096)
            val output = StringBuilder()
            while (reader.read(buffer).also { read = it } > 0) {
                output.appendRange(buffer, 0, read)
            }
            process.waitFor()
            output.toString()
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } finally {
            try {
                outputStream?.close()
                inputStream?.close()
                reader?.close()
                process?.destroy()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}