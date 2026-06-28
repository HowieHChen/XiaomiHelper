package dev.lackluster.mihelper.app.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.ui.component.IconSize
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.utils.SystemCommander
import dev.lackluster.mihelper.app.utils.compose.rememberAppInfo
import dev.lackluster.mihelper.utils.MLog
import kotlinx.coroutines.launch

private const val TAG = "AppRestartPreferenceItem"

private fun String.shellSingleQuoted(): String = "'${replace("'", "'\\''")}'"

@Composable
fun AppRestartPreferenceItem(
    packageName: String,
    title: String,
    verifiedVersion: String,
    onFallbackAction: () -> Unit
) {
    val appInfo by rememberAppInfo(packageName)
    val coroutineScope = rememberCoroutineScope()

    TextPreference(
        icon = appInfo.iconSource?.let {
            ImageIcon(source = it, size = IconSize.App)
        },
        title = title,
        summary = stringResource(
            id = R.string.cleaner_common_version_info,
            verifiedVersion,
            appInfo.versionText
        ),
        onClick = {
            coroutineScope.launch {
                val launcherComponentName = appInfo.launcherComponentName
                if (launcherComponentName == null) {
                    MLog.e(TAG) { "Launcher component not found for $packageName" }
                    onFallbackAction()
                    return@launch
                }

                val killCmd = "am force-stop ${packageName.shellSingleQuoted()}"
                val startCmd = "am start --user current -a android.intent.action.MAIN " +
                        "-c android.intent.category.LAUNCHER -n ${launcherComponentName.shellSingleQuoted()}"

                val result = SystemCommander.execAsync(
                    command = "$killCmd && $startCmd",
                    useRoot = true,
                    silent = true
                )

                if (!result.isSuccess) {
                    MLog.e(TAG) { result.err }
                    onFallbackAction()
                }
            }
        }
    )
}
