package dev.lackluster.mihelper.app.screen.systemui.icon.position

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.component.Card
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.component.FullScreenDialog
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.PreferenceGroup
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.utils.compose.DraggableItem
import dev.lackluster.mihelper.app.utils.compose.dragContainer
import dev.lackluster.mihelper.app.utils.compose.rememberDragDropState
import dev.lackluster.mihelper.data.model.StatusBarIconSlotWrap
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.DropdownArrowEndAction
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.miuixShape
import top.yukonga.miuix.kmp.theme.miuixUnevenShape

private const val LIST_INDEX_OFFSET = 1 // For reset button

private sealed interface IconPositionAction {
    object Close : IconPositionAction
    object Save : IconPositionAction
    object Reset : IconPositionAction
}

@Composable
fun IconPositionPage(
    viewModel: IconPositionViewModel = koinViewModel(),
) {
    val navigator = LocalNavigator.current
    val initialSlots by viewModel.uiState.collectAsState()

    val localSlots = remember { mutableStateListOf<StatusBarIconSlotWrap>() }

    LaunchedEffect(initialSlots) {
        if (initialSlots.isNotEmpty()) {
            localSlots.clear()
            localSlots.addAll(initialSlots)
        }
    }

    val onAction: (IconPositionAction) -> Unit = { action ->
        when (action) {
            IconPositionAction.Close -> navigator.pop()
            IconPositionAction.Reset -> {
                val defaultData = viewModel.getDefaultSlots()
                localSlots.clear()
                localSlots.addAll(defaultData)
            }
            IconPositionAction.Save -> {
                viewModel.savePositions(localSlots)
                navigator.pop()
            }
        }
    }

    IconPositionPageContent(
        slots = localSlots,
        onAction = onAction,
    )
}

@Composable
private fun IconPositionPageContent(
    slots: MutableList<StatusBarIconSlotWrap>,
    onAction: (IconPositionAction) -> Unit,
) {
    val listState = rememberLazyListState()

    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        val fromI = (fromIndex - LIST_INDEX_OFFSET).coerceAtLeast(0)
        val toI = (toIndex - LIST_INDEX_OFFSET).coerceAtLeast(0)
        if (fromI < slots.size && toI < slots.size) {
            slots.apply { add(toI, removeAt(fromI)) }
        }
    }

    val firstCardShape = miuixUnevenShape(16.dp, 16.dp, 0.dp, 0.dp)
    val middleCardShape = remember { RectangleShape }
    val lastCardShape = miuixUnevenShape(0.dp, 0.dp, 16.dp, 16.dp)
    val elevatedShape = miuixShape(16.dp)

    val resetCardColor = CardDefaults.cardColors(containerColor = colorResource(dev.lackluster.hyperx.R.color.hyperx_error_bg))

    BackHandler(enabled = true) { onAction(IconPositionAction.Close) }

    FullScreenDialog(
        title = stringResource(R.string.page_status_bar_icon_order),
        contentModifier = Modifier.dragContainer(dragDropState),
        listState = listState,
        onNegativeButton = { onAction(IconPositionAction.Close) },
        onPositiveButton = { onAction(IconPositionAction.Save) },
    ) {
        item {
            PreferenceGroup(
                position = ItemPosition.First,
                cardColors = resetCardColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(IconPositionAction.Reset) }
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
            SmallTitle(
                text = stringResource(R.string.ui_title_icon_tuner_order),
                textColor = MiuixTheme.colorScheme.onBackgroundVariant,
            )
        }
        itemsIndexed(slots, key = { _, item -> item.slot }) { index, item ->
            val isFirst = index == 0
            val isLast = index == slots.size - 1
            val cardBottomPadding = if (isLast) 12.dp else 0.dp

            DraggableItem(dragDropState, index + LIST_INDEX_OFFSET) { isDragging ->
                val elevation by animateFloatAsState(if (isDragging) 4.0f else 1.0f)
                val shape =
                    if (isDragging) elevatedShape
                    else if (isFirst) firstCardShape
                    else if (isLast) lastCardShape
                    else middleCardShape

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = cardBottomPadding)
                        .zIndex(elevation),
                    shape = shape
                ) {
                    DraggableIcon(item)
                }
            }
        }
    }
}

@Composable
private fun DraggableIcon(data: StatusBarIconSlotWrap) {
    val enabled = data.labelResId != 0
    val actionColor = if (enabled) {
        MiuixTheme.colorScheme.onSurfaceVariantActions
    } else {
        MiuixTheme.colorScheme.disabledOnSecondaryVariant
    }

    BasicComponent(
        insideMargin = PaddingValues(16.dp),
        title = data.labelResId.takeIf { it > 0 }?.let { stringResource(it) } ?: "<${data.slot}>",
        startAction = {
            Image(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(28.dp),
                painter = painterResource(data.iconResId),
                contentDescription = null
            )
        },
        endActions = {
            DropdownArrowEndAction(
                actionColor = actionColor,
            )
        },
        enabled = enabled
    )
}