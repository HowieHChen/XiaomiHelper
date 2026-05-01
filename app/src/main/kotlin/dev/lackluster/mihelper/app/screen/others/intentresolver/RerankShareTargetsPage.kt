package dev.lackluster.mihelper.app.screen.others.intentresolver

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
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
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.delay
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

private sealed interface RerankShareTargetsAction {
    object Close : RerankShareTargetsAction
    object Save : RerankShareTargetsAction
    object Reset : RerankShareTargetsAction
    object AddApp : RerankShareTargetsAction
    data class ModifyApp(val pkg: String, val index: Int) : RerankShareTargetsAction
    data class RemoveApp(val pkg: String) : RerankShareTargetsAction
    data class ShowToast(val message: UiText, val long: Boolean = false) : RerankShareTargetsAction
}

@Composable
fun RerankShareTargetsPage(
    viewModel: RerankShareTargetsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val addedApps by viewModel.uiItems.collectAsState()
    val addDialogState by viewModel.addDialogState.collectAsState()

    val addHoldDown = remember { mutableStateOf(false) }

    val onAction: (RerankShareTargetsAction) -> Unit = { action ->
        when (action) {
            RerankShareTargetsAction.Close -> {
                viewModel.discard()
                navigator.pop()
            }
            RerankShareTargetsAction.Reset -> viewModel.reset()
            RerankShareTargetsAction.Save -> {
                viewModel.save()
                navigator.pop()
            }
            RerankShareTargetsAction.AddApp -> {
                viewModel.openAddDialog()
                addHoldDown.value = true
            }
            is RerankShareTargetsAction.ModifyApp -> {
                viewModel.openAddDialog(action.pkg, action.index.toString())
            }
            is RerankShareTargetsAction.RemoveApp -> {
                viewModel.removeApp(action.pkg)
            }
            is RerankShareTargetsAction.ShowToast -> {
                context.showToast(action.message.asString(context), action.long)
            }
        }
    }

    addDialogState.error?.let {
        onAction(RerankShareTargetsAction.ShowToast(it))
    }

    RerankShareTargetsPageContent(
        addedApps = addedApps,
        addHoldDown = addHoldDown.value,
        onAction = onAction,
    )

    AddAppDialog(
        state = addDialogState,
        onInputPkgChanged = viewModel::updateInputPkg,
        onInputIndexChanged = viewModel::updateInputIndex,
        onConfirm = viewModel::submitNewApp,
        onDismissRequest = { viewModel.closeAddDialog() },
        onDismissFinished = { addHoldDown.value = false },
    )
}

@Composable
private fun RerankShareTargetsPageContent(
    addedApps: List<ShareTargetUiItem>,
    addHoldDown: Boolean,
    onAction: (RerankShareTargetsAction) -> Unit,
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val targetToDelete = remember { mutableStateOf("") }

    val resetCardColor = CardDefaults.cardColors(containerColor = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_bg))

    BackHandler(enabled = true) { onAction(RerankShareTargetsAction.Close) }

    FullScreenDialog(
        title = stringResource(R.string.others_intent_resolver_rerank_index),
        onNegativeButton = { onAction(RerankShareTargetsAction.Close) },
        onPositiveButton = { onAction(RerankShareTargetsAction.Save) },
    ) {
        itemAnimatedColumn(
            key = "SHARE_RERANK_HINT",
        ) {
            Hint(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
                text = stringResource(R.string.others_intent_resolver_rerank_index_hint),
            )
        }
        itemPreferenceGroup(
            key = "SHARE_RERANK_ADD",
        ) {
            TextPreference(
                title = stringResource(R.string.others_intent_resolver_rerank_add),
                holdDownState = addHoldDown
            ) {
                onAction(RerankShareTargetsAction.AddApp)
            }
        }
        itemPreferenceGroup(
            key = "SHARE_RERANK_MODE"
        ) {
            SwitchPreference(
                title = stringResource(R.string.others_intent_resolver_rerank_strict),
                summary = stringResource(R.string.others_intent_resolver_rerank_strict_tips),
                key = Preferences.MiIntentResolver.RERANK_TARGETS_STRICT
            )
        }
        itemPreferenceGroup(
            key = "SHARE_RERANK_RESET",
            cardColors = resetCardColor
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(RerankShareTargetsAction.Reset) }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(R.string.icon_tuner_position_reset_default),
                    fontSize = MiuixTheme.textStyles.headline1.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_fg)
                )
            }
        }
        if (addedApps.isNotEmpty()) {
            itemPreferenceGroup(
                titleRes = R.string.others_intent_resolver_rerank_apps,
                key = "SHARE_RERANK_ADDED",
                position = ItemPosition.Last,
            ) {
                addedApps.forEach { entry ->
                    CombinedClickPreference(
                        entry = entry,
                        onClick = {
                            onAction(RerankShareTargetsAction.ModifyApp(entry.pkgName, entry.index))
                        },
                        onLongClick = {
                            targetToDelete.value = entry.pkgName
                            showDeleteDialog.value = true
                        }
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
        message = stringResource(R.string.others_intent_resolver_rerank_delete_warning, targetToDelete.value),
        cancelable = false,
        mode = AlertDialogMode.NegativeAndPositive,
    ) {
        val pkg = targetToDelete.value
        if (pkg.isNotBlank()) {
            onAction(RerankShareTargetsAction.RemoveApp(pkg))
        }
        showDeleteDialog.value = false
        targetToDelete.value = ""
    }
}

@Composable
private fun CombinedClickPreference(
    entry: ShareTargetUiItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)

    val value = if (entry.index == -1) stringResource(R.string.others_intent_resolver_rerank_hidden) else entry.index.toString()
    BasicComponent(
        modifier = Modifier.combinedClickable(
            interactionSource = interactionSource,
            onClick = { currentOnClick.invoke() },
            onLongClick = { currentOnLongClick.invoke() }
        ),
        title = entry.appName,
        titleColor = BasicComponentDefaults.titleColor(),
        summary = entry.pkgName,
        summaryColor = BasicComponentDefaults.summaryColor(),
        startAction = entry.icon?.let { imageIcon ->
            { PreferenceIconSlot(icon = imageIcon) }
        },
        endActions = {
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
            ) {
                Text(
                    modifier = Modifier.widthIn(max = 130.dp),
                    text = value,
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            val actionColor = MiuixTheme.colorScheme.onSurfaceVariantActions
            val tintFilter = remember(actionColor) {
                ColorFilter.tint(actionColor)
            }
            val layoutDirection = LocalLayoutDirection.current
            Image(
                modifier = Modifier
                    .size(width = 10.dp, height = 16.dp)
                    .graphicsLayer {
                        scaleX = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
                    }
                    .align(Alignment.CenterVertically),
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = null,
                colorFilter = tintFilter,
            )
        }
    )
}

@Composable
private fun AddAppDialog(
    state: AddDialogState,
    onInputPkgChanged: (String) -> Unit,
    onInputIndexChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit = {},
) {
    val visible = state.isVisible
    val focusRequester = remember { FocusRequester() }
    val hapticFeedback = LocalHapticFeedback.current

    var inputPkgValue by remember { mutableStateOf(TextFieldValue("")) }
    var inputIndexValue by remember { mutableStateOf(TextFieldValue("")) }

    OverlayDialog(
        show = visible,
        title = stringResource(R.string.others_intent_resolver_rerank_rule),
        summary = stringResource(R.string.others_intent_resolver_rerank_rule_hint),
        onDismissRequest = {},
        onDismissFinished = onDismissFinished,
        content = {
            BackHandler(enabled = true) {}
            LaunchedEffect(visible) {
                if (visible) {
                    inputPkgValue = TextFieldValue(
                        text = state.inputPkg,
                        selection = TextRange(state.inputPkg.length)
                    )
                    inputIndexValue = TextFieldValue(
                        text = state.inputIndex,
                        selection = TextRange(state.inputIndex.length)
                    )
                    delay(100)
                    focusRequester.requestFocus()
                }
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()), // 防止横屏崩溃，体验不好待改
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = inputPkgValue,
                    onValueChange = {
                        inputPkgValue = it
                        onInputPkgChanged(it.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (state.isNew) Modifier.focusRequester(focusRequester) else Modifier
                        ),
                    label = "com.example.app",
                    useLabelAsPlaceholder = true,
                    textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onBackground),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    enabled = state.isNew && !state.isLoading
                )
                TextField(
                    value = inputIndexValue,
                    onValueChange = {
                        inputIndexValue = it
                        onInputIndexChanged(it.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (state.isNew) Modifier else Modifier.focusRequester(focusRequester)
                        ),
                    label = "0",
                    useLabelAsPlaceholder = true,
                    textStyle = MiuixTheme.textStyles.main.copy(color = MiuixTheme.colorScheme.onBackground),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    visualTransformation = VisualTransformation.None,
                    enabled = !state.isLoading
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(dev.lackluster.hyperx.R.string.button_cancel),
                        minHeight = 50.dp,
                        enabled = !state.isLoading,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            onDismissRequest()
                        }
                    )

                    TextButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(dev.lackluster.hyperx.R.string.button_ok),
                        minHeight = 50.dp,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        enabled = !state.isLoading,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            onConfirm()
                        }
                    )
                }
            }
        }
    )
}