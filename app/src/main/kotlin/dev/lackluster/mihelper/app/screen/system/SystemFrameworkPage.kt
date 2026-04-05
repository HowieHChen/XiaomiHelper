package dev.lackluster.mihelper.app.screen.system

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.lackluster.hyperx.core.utils.toDecimalString
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.EditTextPreference
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.component.RebootActionItem
import dev.lackluster.mihelper.app.screen.system.SystemFrameworkAction.*
import dev.lackluster.hyperx.ui.preference.core.LocalPreferenceActions
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.showToast
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.Scope
import dev.lackluster.mihelper.data.preference.Preferences
import org.koin.androidx.compose.koinViewModel

sealed interface SystemFrameworkUIAction {
    data class ShowToast(val message: UiText, val long: Boolean = false) : SystemFrameworkUIAction
    data class UpdateFontScale(val newValue: String) : SystemFrameworkUIAction
    object OpenFontScaleSheet : SystemFrameworkUIAction
    object OpenFontSetting : SystemFrameworkUIAction
}

@Composable
fun SystemFrameworkPage(
    viewModel: SystemFrameworkViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val appSettingsActions = LocalPreferenceActions.current
    val context = LocalContext.current

    val fontScaleSheetVisibility = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf<UiText?>(null) }

    val isFontScaleOn = remember {
        mutableStateOf(appSettingsActions.get(Preferences.System.ENABLE_FONT_SCALE))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            errorMsg.value = event
        }
    }

    val onAction: (SystemFrameworkUIAction) -> Unit = { action ->
        when (action) {
            is SystemFrameworkUIAction.ShowToast -> {
                context.showToast(action.message.asString(context), action.long)
            }
            is SystemFrameworkUIAction.UpdateFontScale -> {
                val newScale = action.newValue.toFloatOrNull()
                if (newScale != null && newScale in 0.5f..2.5f) {
                    viewModel.handleAction(UpdateFontScale(newScale))
                } else {
                    errorMsg.value = R.string.android_display_temp_font_scale_fail_msg.toUiText()
                }
            }
            SystemFrameworkUIAction.OpenFontScaleSheet -> {
                fontScaleSheetVisibility.value = true
            }
            SystemFrameworkUIAction.OpenFontSetting -> {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        setClassName(
                            "com.android.settings",
                            $$"com.android.settings.Settings$PageLayoutActivity"
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        }
    }

    SystemFrameworkPageContent(
        state = state,
        isFontScaleOn = isFontScaleOn.value,
        onAction = onAction
    )

    AlertDialog(
        visible = errorMsg.value != null,
        onDismissRequest = { errorMsg.value = null },
        title = stringResource(R.string.dialog_error),
        message = errorMsg.value?.asString(),
        mode = AlertDialogMode.Positive
    )

    FontScaleSheet(
        show = fontScaleSheetVisibility.value,
        onAction = onAction,
        onDismissRequest = {
            fontScaleSheetVisibility.value = false
            isFontScaleOn.value = appSettingsActions.get(Preferences.System.ENABLE_FONT_SCALE)
        }
    )
}

@Composable
private fun SystemFrameworkPageContent(
    state: SystemFrameworkState,
    isFontScaleOn: Boolean,
    onAction: (SystemFrameworkUIAction) -> Unit
) {
    HyperXPage(
        title = stringResource(R.string.page_android),
        actions = {
            RebootActionItem(
                appName = stringResource(R.string.scope_android),
                appPkg = arrayOf(Scope.ANDROID),
            )
        }
    ) {
        itemPreferenceGroup(
            titleRes = R.string.ui_title_android_display,
            position = ItemPosition.First
        ) {
            EditTextPreference(
                title = stringResource(R.string.android_display_temp_font_scale),
                summary = stringResource(R.string.android_display_temp_font_scale_tips),
                text = state.currentFontScale.toDecimalString(),
                dialogMessage = stringResource(R.string.android_display_temp_font_scale_msg),
                onTextChange = { onAction(SystemFrameworkUIAction.UpdateFontScale(it)) }
            )
            TextPreference(
                title = stringResource(R.string.android_display_font_scale),
                summary = stringResource(R.string.android_display_font_scale_tips),
                value = stringResource(if (isFontScaleOn) R.string.common_on else R.string.common_off),
                onClick = { onAction(SystemFrameworkUIAction.OpenFontScaleSheet) }
            )
        }

        itemPreferenceGroup(
            titleRes = R.string.ui_title_android_freeform,
            position = ItemPosition.Middle
        ) {
            SwitchPreference(
                key = Preferences.System.DISABLE_FREEFORM_RESTRICT,
                title = stringResource(R.string.android_freeform_restriction),
                summary = stringResource(R.string.android_freeform_restriction_tips),
            )
            SwitchPreference(
                key = Preferences.System.ALLOW_MORE_FREEFORM,
                title = stringResource(R.string.android_freeform_allow_more),
                summary = stringResource(R.string.android_freeform_allow_more_tips),
            )
        }

        itemPreferenceGroup(
            titleRes = R.string.ui_title_android_others,
            position = ItemPosition.Last
        ) {
            SwitchPreference(
                key = Preferences.System.DISABLE_FORCE_DARK_WHITELIST,
                title = stringResource(R.string.android_others_force_dark),
                summary = stringResource(R.string.android_others_force_dark_tips),
            )
        }
    }
}