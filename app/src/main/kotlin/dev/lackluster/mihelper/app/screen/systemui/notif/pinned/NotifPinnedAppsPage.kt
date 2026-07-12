package dev.lackluster.mihelper.app.screen.systemui.notif.pinned

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.component.FullScreenDialog
import dev.lackluster.hyperx.ui.component.Hint
import dev.lackluster.hyperx.ui.component.PreferenceIconSlot
import dev.lackluster.hyperx.ui.dialog.AlertDialog
import dev.lackluster.hyperx.ui.dialog.AlertDialogMode
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.SwitchPreference
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemAnimatedColumn
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.showToast
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

private sealed interface NotifPinnedAppsAction {
    object Close : NotifPinnedAppsAction
    object Save : NotifPinnedAppsAction
    object Reset : NotifPinnedAppsAction
    object AddApp : NotifPinnedAppsAction
    data class SwitchStatus(val enabled: Boolean) : NotifPinnedAppsAction
    data class ModifyApp(val pkg: String, val index: Int) : NotifPinnedAppsAction
    data class RemoveApp(val pkg: String) : NotifPinnedAppsAction
    data class ShowToast(val message: UiText, val long: Boolean = false) : NotifPinnedAppsAction
}

@Composable
fun NotifPinnedAppsPage(
    viewModel: NotifPinnedAppsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val enabled by viewModel.draftEnabled.collectAsStateWithLifecycle()
    val apps by viewModel.uiItems.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    val addHoldDown = remember { mutableStateOf(false) }

    val onAction: (NotifPinnedAppsAction) -> Unit = { action ->
        when (action) {
            NotifPinnedAppsAction.Close -> {
                viewModel.discard()
                navigator.pop()
            }
            NotifPinnedAppsAction.Reset -> viewModel.reset()
            NotifPinnedAppsAction.Save -> {
                viewModel.save()
                navigator.pop()
            }
            NotifPinnedAppsAction.AddApp -> {
                viewModel.openDialog()
                addHoldDown.value = true
            }
            is NotifPinnedAppsAction.ModifyApp -> {
                viewModel.openDialog(action.pkg, action.index.toString())
            }
            is NotifPinnedAppsAction.RemoveApp -> {
                viewModel.removeApp(action.pkg)
            }
            is NotifPinnedAppsAction.ShowToast -> {
                context.showToast(action.message.asString(context), action.long)
            }
            is NotifPinnedAppsAction.SwitchStatus -> {
                viewModel.updateEnabled(action.enabled)
            }
        }
    }

    dialogState.error?.let {
        onAction(NotifPinnedAppsAction.ShowToast(it))
    }

    NotifPinnedAppsPageContent(
        enabled = enabled,
        addedApps = apps,
        addHoldDown = addHoldDown.value,
        onAction = onAction
    )

    PinnedAppDialog(
        state = dialogState,
        onInputPkgChanged = viewModel::updateInputPkg,
        onInputIndexChanged = viewModel::updateInputIndex,
        onConfirm = viewModel::submitApp,
        onDismissRequest = viewModel::closeDialog,
        onDismissFinished = { addHoldDown.value = false },
    )
}

@Composable
private fun NotifPinnedAppsPageContent(
    enabled: Boolean,
    addedApps: List<PinnedAppUiItem>,
    addHoldDown: Boolean,
    onAction: (NotifPinnedAppsAction) -> Unit,
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val targetToDelete = remember { mutableStateOf("") }

    val resetCardColor = CardDefaults.cardColors(containerColor = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_bg))

    BackHandler(enabled = true) { onAction(NotifPinnedAppsAction.Close) }

    FullScreenDialog(
        title = stringResource(R.string.systemui_notif_lr_pinned_apps),
        onNegativeButton = { onAction(NotifPinnedAppsAction.Close) },
        onPositiveButton = { onAction(NotifPinnedAppsAction.Save) },
    ) {
        itemAnimatedColumn(
            key = "NOTIF_PINNED_HINT"
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 6.dp),
                text = stringResource(R.string.systemui_notif_lr_pinned_apps_hint),
            )
        }
        itemPreferenceGroup(
            key = "NOTIF_PINNED_ENABLE"
        ) {
            SwitchPreference(
                title = stringResource(R.string.systemui_notif_lr_pinned_apps_enable),
                checked = enabled,
                onCheckedChange = { onAction(NotifPinnedAppsAction.SwitchStatus(it)) },
            )
        }
        itemPreferenceGroup(
            key = "NOTIF_PINNED_ADD"
        ) {
            TextPreference(
                title = stringResource(R.string.systemui_notif_lr_pinned_apps_add),
                holdDownState = addHoldDown,
                onClick = {
                    onAction(NotifPinnedAppsAction.AddApp)
                },
            )
        }
        itemPreferenceGroup(
            key = "NOTIF_PINNED_RESET",
            cardColors = resetCardColor,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable { onAction(NotifPinnedAppsAction.Reset) }
                    .padding(16.dp),
                text = stringResource(R.string.icon_tuner_position_reset_default),
                fontSize = MiuixTheme.textStyles.headline1.fontSize,
                fontWeight = FontWeight.Medium,
                color = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_fg),
            )
        }
        if (addedApps.isNotEmpty()) {
            itemPreferenceGroup(
                titleRes = R.string.systemui_notif_lr_pinned_apps_list,
                key = "NOTIF_PINNED_LIST",
                position = ItemPosition.Last,
            ) {
                addedApps.forEach { app ->
                    PinnedAppPreference(
                        entry = app,
                        onClick = {
                            onAction(NotifPinnedAppsAction.ModifyApp(app.pkgName, app.index))
                        },
                        onLongClick = {
                            targetToDelete.value = app.pkgName
                            showDeleteDialog.value = true
                        },
                    )
                }
            }
        }
    }

    AlertDialog(
        visible = showDeleteDialog.value,
        onDismissRequest = {
            showDeleteDialog.value = false
            targetToDelete.value = ""
        },
        title = stringResource(R.string.dialog_warning),
        message = stringResource(R.string.systemui_notif_lr_pinned_apps_delete_warning, targetToDelete.value),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
    ) {
        val pkg = targetToDelete.value
        if (pkg.isNotBlank()) {
            onAction(NotifPinnedAppsAction.RemoveApp(pkg))
        }
        showDeleteDialog.value = false
        targetToDelete.value = ""
    }
}

@Composable
private fun PinnedAppPreference(
    entry: PinnedAppUiItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    BasicComponent(
        modifier = Modifier.combinedClickable(
            interactionSource = interactionSource,
            onClick = { currentOnClick() },
            onLongClick = { currentOnLongClick() },
        ),
        title = entry.appName,
        titleColor = BasicComponentDefaults.titleColor(),
        summary = entry.pkgName,
        summaryColor = BasicComponentDefaults.summaryColor(),
        startAction = entry.icon?.let { icon -> { PreferenceIconSlot(icon = icon) } },
        endActions = {
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
            ) {
                Text(
                    modifier = Modifier.widthIn(max = 130.dp),
                    text = entry.index.toString(),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
            val actionColor = MiuixTheme.colorScheme.onSurfaceVariantActions
            val tintFilter = remember(actionColor) { ColorFilter.tint(actionColor) }
            val layoutDirection = LocalLayoutDirection.current
            Image(
                modifier = Modifier
                    .size(width = 10.dp, height = 16.dp)
                    .graphicsLayer { scaleX = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f }
                    .align(Alignment.CenterVertically),
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = null,
                colorFilter = tintFilter,
            )
        },
    )
}

@Composable
private fun PinnedAppDialog(
    state: PinnedAppDialogState,
    onInputPkgChanged: (String) -> Unit,
    onInputIndexChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val hapticFeedback = LocalHapticFeedback.current
    var inputPkg by remember { mutableStateOf(TextFieldValue("")) }
    var inputIndex by remember { mutableStateOf(TextFieldValue("0")) }

    OverlayDialog(
        show = state.isVisible,
        title = stringResource(R.string.systemui_notif_lr_pinned_apps_rule),
        summary = stringResource(R.string.systemui_notif_lr_pinned_apps_rule_hint),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        content = {
            LaunchedEffect(state.isVisible) {
                if (state.isVisible) {
                    inputPkg = TextFieldValue(state.inputPkg, TextRange(state.inputPkg.length))
                    inputIndex = TextFieldValue(state.inputIndex, TextRange(state.inputIndex.length))
                    focusRequester.requestFocus()
                }
            }
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextField(
                    value = inputPkg,
                    onValueChange = { inputPkg = it; onInputPkgChanged(it.text) },
                    modifier = Modifier.fillMaxWidth().then(
                        if (state.isNew) Modifier.focusRequester(focusRequester) else Modifier
                    ),
                    label = "com.example.app",
                    useLabelAsPlaceholder = true,
                    textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onBackground),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    enabled = state.isNew && !state.isLoading,
                )
                TextField(
                    value = inputIndex,
                    onValueChange = { inputIndex = it; onInputIndexChanged(it.text) },
                    modifier = Modifier.fillMaxWidth().then(
                        if (state.isNew) Modifier else Modifier.focusRequester(focusRequester)
                    ),
                    label = stringResource(R.string.systemui_notif_lr_pinned_apps_priority),
                    useLabelAsPlaceholder = true,
                    textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onBackground),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    visualTransformation = VisualTransformation.None,
                    enabled = !state.isLoading,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(dev.lackluster.hyperx.R.string.button_cancel),
                        minHeight = 50.dp,
                        enabled = !state.isLoading,
                        onClick = { hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm); onDismissRequest() },
                    )
                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(dev.lackluster.hyperx.R.string.button_ok),
                        minHeight = 50.dp,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        enabled = !state.isLoading,
                        onClick = { hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm); onConfirm() },
                    )
                }
            }
        },
    )
}
