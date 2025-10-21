package dev.lackluster.mihelper.ui.dialog

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.component.FullScreenDialog
import dev.lackluster.hyperx.compose.component.Hint
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.ui.MainActivity
import top.yukonga.miuix.kmp.basic.SmallTitle

@Composable
fun FontScaleDialog(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    var spValueFontScale by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Android.FONT_SCALE, false)) }
    var fontScaleSmall by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_SMALL, 0.9f)) }
    var fontScaleMedium by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_MEDIUM, 1.0f)) }
    var fontScaleLarge by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_LARGE, 1.1f)) }
    var fontScaleHuge by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_HUGE, 1.25f)) }
    var fontScaleGodzilla by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_GODZILLA, 1.45f)) }
    var fontScale170 by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_170, 1.7f)) }
    var fontScale200 by remember { mutableFloatStateOf(SafeSP.getFloat(Pref.Key.Android.FONT_SCALE_200, 2.0f)) }

    FullScreenDialog(
        navController,
        adjustPadding,
        stringResource(R.string.android_display_font_scale),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        onPositiveButton = {
            val values = listOf(
                fontScaleSmall,
                fontScaleMedium,
                fontScaleLarge,
                fontScaleHuge,
                fontScaleGodzilla,
                fontScale170,
                fontScale200,
            )
            val haveSameValue = values.toSet().size < 7
            if (haveSameValue) {
                Toast.makeText(
                    context,
                    context.getString(R.string.android_display_font_scale_warn_same),
                    Toast.LENGTH_SHORT
                ).show()
                return@FullScreenDialog
            }
            var disorder = false
            values.forEachIndexed { index, f ->
                if (index > 0 && values[index - 1] > f) {
                    disorder = true
                    return@forEachIndexed
                }
            }
            if (disorder) {
                Toast.makeText(
                    context,
                    context.getString(R.string.android_display_font_scale_warn_disorder),
                    Toast.LENGTH_SHORT
                ).show()
                return@FullScreenDialog
            }
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE, spValueFontScale)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_SMALL, fontScaleSmall)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_MEDIUM, fontScaleMedium)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_LARGE, fontScaleLarge)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_HUGE, fontScaleHuge)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_GODZILLA, fontScaleGodzilla)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_170, fontScale170)
            SafeSP.putAny(Pref.Key.Android.FONT_SCALE_200, fontScale200)
            navController.popBackStack()
        }
    ) {
        item {
            PreferenceGroup(
                first = true,
                title = stringResource(R.string.common_general)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.android_display_font_scale),
                    summary = stringResource(R.string.android_display_font_scale_reboot),
                    defValue = spValueFontScale
                ) {
                    spValueFontScale = it
                }
                TextPreference(
                    title = stringResource(R.string.android_display_font_settings),
                    summary = stringResource(R.string.android_display_font_settings_tips)
                ) {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            setClassName("com.android.settings", "com.android.settings.Settings\$PageLayoutActivity")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            }
        }
        item {
            SmallTitle(
                text = stringResource(R.string.android_display_font_scale_value),
                modifier = Modifier.padding(top = 6.dp),
            )
            Hint(
                modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 6.dp),
                text = stringResource(R.string.android_display_font_scale_hint)
            )
            PreferenceGroup(
                last = true
            ) {
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_small),
                    summary = "[0.50f, 1.00f)",
                    defValue = fontScaleSmall,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 0.5f, 1.0f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 0.5f..<1.0f
                    }
                ) { str, value ->
                    if (value is Float) fontScaleSmall = value
                }
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_medium),
                    summary = "[0.90f, 1.10f)",
                    defValue = fontScaleMedium,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 0.9f, 1.1f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 0.9f..<1.1f
                    }
                ) { str, value ->
                    if (value is Float) fontScaleMedium = value
                }
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_large),
                    summary = "[1.00f, 1.25f)",
                    defValue = fontScaleLarge,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 1.0f, 1.25f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 1.0f..<1.25f
                    }
                ) { str, value ->
                    if (value is Float) fontScaleLarge= value
                }
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_huge),
                    summary = "[1.10f, 1.45f)",
                    defValue = fontScaleHuge,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 1.1f, 1.45f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 1.1f..<1.45f
                    }
                ) { str, value ->
                    if (value is Float) fontScaleHuge = value
                }
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_godzilla),
                    summary = "[1.25f, 1.70f)",
                    defValue = fontScaleGodzilla,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 1.25f, 1.7f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 1.25f..<1.7f
                    }
                ) { str, value ->
                    if (value is Float) fontScaleGodzilla = value
                }
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_170),
                    summary = "[1.45f, 2.00f)",
                    defValue = fontScale170,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 1.45f, 2.0f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 1.45f..<2.0f
                    }
                ) { str, value ->
                    if (value is Float) fontScale170 = value
                }
                EditTextPreference(
                    title = stringResource(R.string.android_display_font_scale_200),
                    summary = "[1.70f, 2.50f)",
                    defValue = fontScale200,
                    dataType = EditTextDataType.FLOAT,
                    dialogMessage = stringResource(R.string.android_display_font_scale_msg, 1.7f, 2.5f),
                    isValueValid = {
                        (it as? Float ?: 0.0f) in 1.7f..<2.5f
                    }
                ) { str, value ->
                    if (value is Float) fontScale200 = value
                }
            }
        }
    }
}