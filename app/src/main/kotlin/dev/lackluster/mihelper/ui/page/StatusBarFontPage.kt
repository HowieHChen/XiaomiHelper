package dev.lackluster.mihelper.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.highcapable.yukihookapi.hook.log.YLog
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.component.Hint
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_REAL_FILE_NAME
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.ui.component.FontFamilyCache
import dev.lackluster.mihelper.utils.ShellUtils
import top.yukonga.miuix.kmp.basic.SmallTitle
import java.io.File

@Composable
fun StatusBarFontPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    var spValueCarrierFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.LOCK_SCREEN_CARRIER)) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_font),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            SmallTitle(
                text = stringResource(R.string.ui_title_font_general),
                modifier = Modifier.padding(top = 6.dp),
            )
            Hint(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp),
                text = stringResource(R.string.font_hint_path)
            )
            PreferenceGroup(
                first = true
            ) {
                EditTextPreference(
                    title = stringResource(R.string.font_general_path),
                    key = Pref.Key.SystemUI.FontWeight.FONT_PATH_REAL,
                    defValue = VARIABLE_FONT_DEFAULT_PATH,
                    dataType = EditTextDataType.STRING,
                    dialogMessage = stringResource(R.string.font_general_path_tips),
                    isValueValid = { path ->
                        (path as? String)?.let {
                            val file = File(it)
                            file.exists() && file.isFile
                        } ?: false
                    },
                    valuePosition = ValuePosition.SUMMARY_VIEW,
                    onValueChange = { path, _ ->
                        try {
                            val file = File(context.filesDir, VARIABLE_FONT_REAL_FILE_NAME)
                            if (file.exists() && file.isFile) {
                                file.delete()
                            }
                        } catch (_: Throwable) { }
                        if (path == VARIABLE_FONT_DEFAULT_PATH) {
                            SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_REAL, VARIABLE_FONT_DEFAULT_PATH)
                            SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_APP, VARIABLE_FONT_DEFAULT_PATH)
                        } else {
                            try {
                                val oriFile = File(path)
                                val newFile = File(context.filesDir, VARIABLE_FONT_REAL_FILE_NAME)
                                if (newFile.exists()) {
                                    newFile.delete()
                                }
                                val owner = ShellUtils.tryExec(
                                    "ls -ld /data/data/dev.lackluster.mihelper/files | awk '{print $3}'",
                                    useRoot = true,
                                    checkSuccess = true
                                ).successMsg
                                val group = ShellUtils.tryExec(
                                    "ls -ld /data/data/dev.lackluster.mihelper/files | awk '{print $4}'",
                                    useRoot = true,
                                    checkSuccess = true
                                ).successMsg
                                ShellUtils.tryExec(
                                    "cp ${oriFile.absolutePath} ${newFile.absolutePath}",
                                    useRoot = true,
                                    checkSuccess = true
                                )
                                ShellUtils.tryExec(
                                    "chown ${owner}:${group} ${newFile.absolutePath} && chmod 644 ${newFile.absolutePath}",
                                    useRoot = true,
                                    checkSuccess = true
                                )
                                SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_APP, newFile.absolutePath)
                                FontFamilyCache.updateVfCustomPath()
                            } catch (t: Throwable) {
                                YLog.error("error", t)
                                SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_APP, VARIABLE_FONT_DEFAULT_PATH)
                            }
                        }
                    }
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_font_weight),
                last = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.font_weight_lockscreen_carrier),
                    key = Pref.Key.SystemUI.FontWeight.LOCK_SCREEN_CARRIER
                ) {
                    spValueCarrierFont = it
                }
                AnimatedVisibility(
                    spValueCarrierFont
                ) {
                    SeekBarPreference(
                        title = stringResource(R.string.font_weight_lockscreen_carrier_weight),
                        key = Pref.Key.SystemUI.FontWeight.LOCK_SCREEN_CARRIER_WEIGHT,
                        defValue = 430,
                        min = 1,
                        max = 1000
                    )
                }
            }
        }
    }
}