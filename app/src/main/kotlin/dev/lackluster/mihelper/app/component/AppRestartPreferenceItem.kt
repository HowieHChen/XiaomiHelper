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
import kotlinx.coroutines.launch

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
                val killCmd = "am force-stop $packageName"
                val startCmd = "monkey -p $packageName -c android.intent.category.LAUNCHER 1"

                val isSuccess = SystemCommander.execAsync(
                    command = "$killCmd && $startCmd",
                    useRoot = true,
                    silent = true
                ).isSuccess

                if (!isSuccess) {
                    onFallbackAction()
                }
            }
        }
    )
}