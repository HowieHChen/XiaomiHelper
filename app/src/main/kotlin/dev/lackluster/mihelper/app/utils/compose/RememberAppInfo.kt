package dev.lackluster.mihelper.app.utils.compose

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import dev.lackluster.hyperx.ui.component.ImageSource
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.utils.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfoState(
    val isLoading: Boolean = true,
    val versionText: String = "?",
    val iconSource: ImageSource? = null
)

@Composable
fun rememberAppInfo(packageName: String): State<AppInfoState> {
    if (LocalInspectionMode.current) {
        return remember {
            mutableStateOf(
                AppInfoState(
                    isLoading = false,
                    versionText = "v15.0.0 (150000)",
                    iconSource = ImageSource.Res(R.mipmap.ic_launcher)
                )
            )
        }
    }

    val context = LocalContext.current

    return produceState(initialValue = AppInfoState(), key1 = packageName) {
        value = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            try {
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
                val appInfo = pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0L))

                val versionCode = packageInfo.longVersionCode
                val versionText = if (packageInfo.versionName != null) {
                    "${packageInfo.versionName} ($versionCode)"
                } else {
                    versionCode.toString()
                }

                val nativeDrawable = appInfo.loadIcon(pm)
                val composeImageSource = ImageSource.Bitmap(nativeDrawable.toComposeImageBitmap())

                AppInfoState(
                    isLoading = false,
                    versionText = versionText,
                    iconSource = composeImageSource
                )
            } catch (_: Exception) {
                val fallbackDrawable = pm.defaultActivityIcon
                val fallbackSource = ImageSource.Bitmap(fallbackDrawable.toComposeImageBitmap())

                AppInfoState(
                    isLoading = false,
                    versionText = "?",
                    iconSource = fallbackSource
                )
            }
        }
    }
}