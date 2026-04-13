package dev.lackluster.mihelper.app.utils

import android.graphics.Typeface
import dev.lackluster.mihelper.app.manager.XposedServiceManager
import dev.lackluster.mihelper.utils.MLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream

private const val TAG = "RemoteFileStore"

class RemoteFileStore(
    private val xposedManager: XposedServiceManager
) {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            xposedManager.serviceFlow.collect { service ->
                val ready = service != null
                _isReady.update { ready }
                if (ready) {
                    MLog.w(TAG) { "RemoteFiles: [${listFiles().joinToString(", ")}]" }
                }
            }
        }
    }

    suspend fun readText(fileName: String): String? = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext null
        runCatching {
            service.openRemoteFile(fileName).use { pfd ->
                FileInputStream(pfd.fileDescriptor).bufferedReader().use { reader ->
                    reader.readText()
                }
            }
        }.onFailure {
            MLog.e(TAG, it) { "Exception during readText: $fileName" }
            it.printStackTrace()
        }.getOrNull()
    }

    suspend fun readBytes(fileName: String): ByteArray? = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext null
        runCatching {
            service.openRemoteFile(fileName).use { pfd ->
                FileInputStream(pfd.fileDescriptor).use { fis ->
                    fis.readBytes()
                }
            }
        }.onFailure {
            MLog.e(TAG, it) { "Exception during readBytes: $fileName" }
            it.printStackTrace()
        }.getOrNull()
    }

    suspend fun writeText(fileName: String, content: String): Boolean = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext false
        runCatching {
            service.openRemoteFile(fileName).use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { fos ->
                    fos.channel.truncate(0)
                    fos.bufferedWriter().use { writer ->
                        writer.write(content)
                    }
                }
            }
            true
        }.onFailure {
            MLog.e(TAG, it) { "Exception during writeText: $fileName" }
            it.printStackTrace()
        }.getOrDefault(false)
    }

    suspend fun writeBytes(fileName: String, data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext false
        runCatching {
            service.openRemoteFile(fileName).use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { fos ->
                    fos.channel.truncate(0)
                    fos.write(data)
                }
            }
            true
        }.onFailure {
            MLog.e(TAG, it) { "Exception during writeBytes: $fileName" }
            it.printStackTrace()
        }.getOrDefault(false)
    }

    suspend fun delete(fileName: String): Boolean = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext false
        runCatching {
            service.deleteRemoteFile(fileName)
        }.onFailure {
            MLog.e(TAG, it) { "Exception during delete file: $fileName" }
        }.getOrDefault(false)
    }

    suspend fun listFiles(): Array<String> = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext emptyArray()
        runCatching {
            service.listRemoteFiles()
        }.onFailure {
            MLog.e(TAG, it) { "Exception during listFiles" }
        }.getOrDefault(emptyArray())
    }

    suspend fun buildTypeface(fileName: String): Typeface = withContext(Dispatchers.IO) {
        val service = xposedManager.currentService ?: return@withContext Typeface.DEFAULT_BOLD
        runCatching {
            service.openRemoteFile(fileName).use { pfd ->
                Typeface.Builder(pfd.fileDescriptor).build() ?: Typeface.DEFAULT_BOLD
            }
        }.onFailure {
            MLog.e(TAG, it) { "Exception during buildTypeface: $fileName" }
        }.getOrNull() ?: Typeface.DEFAULT_BOLD
    }
}