package dev.lackluster.mihelper.ui.page

import android.widget.Toast
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
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_DEFAULT_PATH
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_REAL_FILE_NAME
import dev.lackluster.mihelper.data.Constants.VARIABLE_FONT_REAL_FILE_PATH
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.ui.component.FontFamilyCache
import dev.lackluster.mihelper.ui.component.itemAnimated
import dev.lackluster.mihelper.ui.component.itemPreferenceGroup
import dev.lackluster.mihelper.utils.ShellUtils
import java.io.File

@Composable
fun StatusBarFontPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    var spValueCarrierFont by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.SystemUI.FontWeight.LOCK_SCREEN_CARRIER)) }
    var hintCloseFontRoot by remember { mutableStateOf(
        SafeSP.getBoolean(Pref.Key.Hints.CLOSE_STATUS_BAR_FONT_ROOT, false)
    ) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_status_bar_font),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        itemPreferenceGroup(
            key = "FONT_GENERAL",
            titleResId = R.string.ui_title_font_general,
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
                    if (path == VARIABLE_FONT_DEFAULT_PATH) {
                        SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_REAL, VARIABLE_FONT_DEFAULT_PATH)
                        SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_INTERNAL, VARIABLE_FONT_DEFAULT_PATH)
                    } else {
                        val oriFilePath = File(path).absolutePath
                        try {
                            val newFilePath = "${VARIABLE_FONT_REAL_FILE_PATH}/${VARIABLE_FONT_REAL_FILE_NAME}"
                            ShellUtils.tryExec(
                                "cp -f $oriFilePath $newFilePath",
                                useRoot = true,
                                throwIfError = true
                            )
                            ShellUtils.tryExec(
                                "chmod 755 $newFilePath",
                                useRoot = true,
                                throwIfError = true
                            )
                            SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_INTERNAL, newFilePath)
                            FontFamilyCache.updateVfCustomPath()
                        } catch (t: Throwable) {
                            YLog.error("error", t)
                            if (t.message?.trim()?.endsWith("Permission denied") == true) {
                                SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_INTERNAL, oriFilePath)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.font_hint_general_root),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                SafeSP.putAny(Pref.Key.SystemUI.FontWeight.FONT_PATH_INTERNAL, VARIABLE_FONT_DEFAULT_PATH)
                            }
                        }
                    }
                }
            )
        }
        itemAnimated(
            key = "FONT_GENERAL_HINT"
        ) {
            Hint(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 6.dp),
                text = stringResource(R.string.font_hint_path)
            )
        }
        itemAnimated(
            key = "FONT_ROOT_HINT",
            visible = !hintCloseFontRoot
        ) {
            Hint(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 6.dp),
                text = stringResource(R.string.font_hint_general_root),
                closeable = true
            ) {
                hintCloseFontRoot = true
                SafeSP.putAny(Pref.Key.Hints.CLOSE_STATUS_BAR_FONT_ROOT, true)
            }
        }
        itemPreferenceGroup(
            key = "FONT_WEIGHT",
            titleResId = R.string.ui_title_font_weight,
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