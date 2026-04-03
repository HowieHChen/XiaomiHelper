package dev.lackluster.mihelper.app.screen.system

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.lackluster.hyperx.core.utils.toDecimalString
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.component.Hint
import dev.lackluster.hyperx.ui.layout.HyperXSheet
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemAnimatedColumn
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.hyperx.ui.preference.core.PreferenceActions
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.hyperx.ui.preference.core.rememberPreferenceState
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.preference.Preferences
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class FontScaleDraftState(
    val enabled: Boolean = false,
    val small: Float = 0.9f,
    val medium: Float = 1.0f,
    val large: Float = 1.1f,
    val huge: Float = 1.25f,
    val godzilla: Float = 1.45f,
    val f170: Float = 1.7f,
    val f200: Float = 2.0f
) {
    val scaleValues: List<Float> get() = listOf(small, medium, large, huge, godzilla, f170, f200)

    fun validate(): Int? {
        if (scaleValues.toSet().size < scaleValues.size) {
            return R.string.android_display_font_scale_warn_same
        }
        val isSortedAscending = scaleValues.zipWithNext { a, b -> a < b }.all { it }
        if (!isSortedAscending) {
            return R.string.android_display_font_scale_warn_disorder
        }
        return null
    }

    fun commit(actions: PreferenceActions) {
        actions.update(Preferences.System.ENABLE_FONT_SCALE, enabled)
        actions.update(Preferences.System.FONT_SCALE_SMALL, small)
        actions.update(Preferences.System.FONT_SCALE_MEDIUM, medium)
        actions.update(Preferences.System.FONT_SCALE_LARGE, large)
        actions.update(Preferences.System.FONT_SCALE_HUGE, huge)
        actions.update(Preferences.System.FONT_SCALE_GODZILLA, godzilla)
        actions.update(Preferences.System.FONT_SCALE_170, f170)
        actions.update(Preferences.System.FONT_SCALE_200, f200)
    }
}

@Composable
fun FontScaleSheet(
    show: Boolean,
    onAction: (SystemFrameworkUIAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val appSettingsActions = LocalPreferenceActions.current

    var draftState by remember(show) {
        mutableStateOf(
            FontScaleDraftState(
                enabled = appSettingsActions.get(Preferences.System.ENABLE_FONT_SCALE),
                small = appSettingsActions.get(Preferences.System.FONT_SCALE_SMALL),
                medium = appSettingsActions.get(Preferences.System.FONT_SCALE_MEDIUM),
                large = appSettingsActions.get(Preferences.System.FONT_SCALE_LARGE),
                huge = appSettingsActions.get(Preferences.System.FONT_SCALE_HUGE),
                godzilla = appSettingsActions.get(Preferences.System.FONT_SCALE_GODZILLA),
                f170 = appSettingsActions.get(Preferences.System.FONT_SCALE_170),
                f200 = appSettingsActions.get(Preferences.System.FONT_SCALE_200)
            )
        )
    }

    val cardColor = CardDefaults.cardColors(containerColor = MiuixTheme.colorScheme.secondaryContainer)
    val showHint = rememberPreferenceState(Preferences.HintState.SYSTEM_FONT_SCALE)

    HyperXSheet(
        show = show,
        title = stringResource(R.string.android_display_font_scale),
        onDismissRequest = onDismissRequest,
        allowDismiss = false,
        onNegativeButton = { dismiss -> dismiss() },
        onPositiveButton = { dismiss ->
            val errorResId = draftState.validate()
            if (errorResId != null) {
                onAction(SystemFrameworkUIAction.ShowToast(errorResId.toUiText(), true))
                return@HyperXSheet
            }
            draftState.commit(appSettingsActions)
            dismiss()
        },
    ) {
        itemPreferenceGroup(
            key = "FONT_GENERAL",
            cardColors = cardColor,
        ) {
            SwitchPreference(
                title = stringResource(R.string.android_display_font_scale),
                summary = stringResource(R.string.android_display_font_scale_reboot),
                checked = draftState.enabled,
                onCheckedChange = { draftState = draftState.copy(enabled = it) }
            )
            TextPreference(
                title = stringResource(R.string.android_display_font_settings),
                summary = stringResource(R.string.android_display_font_settings_tips),
                onClick = { onAction(SystemFrameworkUIAction.OpenFontSetting) }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.android_display_font_scale_value,
            cardColors = cardColor,
            position = ItemPosition.Last
        ) {
            ScaleInputItem(R.string.android_display_font_scale_small, 0.5f, 1.0f, draftState.small) { draftState = draftState.copy(small = it) }
            ScaleInputItem(R.string.android_display_font_scale_medium, 0.9f, 1.1f, draftState.medium) { draftState = draftState.copy(medium = it) }
            ScaleInputItem(R.string.android_display_font_scale_large, 1.0f, 1.25f, draftState.large) { draftState = draftState.copy(large = it) }
            ScaleInputItem(R.string.android_display_font_scale_huge, 1.1f, 1.45f, draftState.huge) { draftState = draftState.copy(huge = it) }
            ScaleInputItem(R.string.android_display_font_scale_godzilla, 1.25f, 1.7f, draftState.godzilla) { draftState = draftState.copy(godzilla = it) }
            ScaleInputItem(R.string.android_display_font_scale_170, 1.45f, 2.0f, draftState.f170) { draftState = draftState.copy(f170 = it) }
            ScaleInputItem(R.string.android_display_font_scale_200, 1.7f, 2.5f, draftState.f200) { draftState = draftState.copy(f200 = it) }
        }
        itemAnimatedColumn(
            key = "FONT_SCALE_HINT",
            visible = showHint.value
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
                text = stringResource(R.string.android_display_font_scale_hint),
                closeable = true,
                onClose = { showHint.value = false }
            )
        }
    }
}

@Composable
private fun ScaleInputItem(
    titleResId: Int,
    min: Float,
    max: Float,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val summaryText = remember(min, max) {
        "[${min.toDecimalString()}f, ${max.toDecimalString()}f)"
    }
    EditTextPreference(
        title = stringResource(titleResId),
        summary = summaryText,
        text = value.toDecimalString(),
        dialogMessage = stringResource(R.string.android_display_font_scale_msg, min, max),
        onTextChange = {
            it.toFloatOrNull()?.let { parsedValue ->
                if (parsedValue in min..< max) onValueChange(parsedValue)
            }
        }
    )
}