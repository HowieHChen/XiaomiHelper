package dev.lackluster.mihelper.app.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import dev.lackluster.mihelper.app.utils.toComposeImageBitmap
import dev.lackluster.mihelper.utils.MLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

data class AppDetail(
    val packageName: String,
    val appName: String,
    val icon: ImageBitmap?
)

class AppInfoRepository(
    context: Context
) {
    private val packageManager: PackageManager = context.packageManager

    private val _appDetailCache = MutableStateFlow<Map<String, AppDetail>>(emptyMap())
    val appDetailCacheFlow = _appDetailCache.asStateFlow()

    private val fetchingPackages = ConcurrentHashMap.newKeySet<String>()

    suspend fun fetchAppNameIfNeeded(pkg: String) = withContext(Dispatchers.IO) {
        if (_appDetailCache.value.containsKey(pkg) || !fetchingPackages.add(pkg)) {
            return@withContext
        }

        var appName: String
        var appIcon: ImageBitmap? = null

        try {
            val appInfo = packageManager.getApplicationInfo(pkg, 0)
            appName = packageManager.getApplicationLabel(appInfo).toString()

            val nativeDrawable = appInfo.loadIcon(packageManager)
            appIcon = nativeDrawable.toComposeImageBitmap()

        } catch (e: Exception) {
            MLog.e(e)
            appName = pkg
            try {
                appIcon = packageManager.defaultActivityIcon.toComposeImageBitmap()
            } catch (e2: Exception) {
                MLog.e(e2)
            }
        }

        _appDetailCache.update { it + (pkg to AppDetail(pkg, appName, appIcon)) }
        fetchingPackages.remove(pkg)
    }
}