package dev.lackluster.mihelper.ui.dialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.Card
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.icon.ImmersionClose
import dev.lackluster.hyperx.compose.icon.ImmersionConfirm
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.RightActionDefaults
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Constants
import dev.lackluster.mihelper.data.Constants.IconSlots.COMPOUND_ICON_STUB
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.StatusBarIconSlotWrap
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.ui.util.DraggableItem
import dev.lackluster.mihelper.ui.util.dragContainer
import dev.lackluster.mihelper.ui.util.rememberDragDropState
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowUpDownIntegrated
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

private const val LIST_INDEX_OFFSET = 1

@Composable
fun StatusBarIconPositionDialog(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val addCompoundIcon = SafeSP.getInt(Pref.Key.SystemUI.IconTuner.COMPOUND_ICON, 0) in 1..3

    val slots = remember {
        val slotsOrderStr = SafeSP.getStringSet(
            Pref.Key.SystemUI.IconTuner.ICON_POSITION_VAL,
            mutableSetOf()
        ).mapNotNull { str ->
            str.split(":").takeIf { it.size == 2 }
        }.sortedBy {
            it[0].toInt()
        }.map { it[1] }.takeIf {
            it.isNotEmpty()
        } ?: Constants.STATUS_BAR_ICONS_DEFAULT
        slotsOrderStr.let {
            if (addCompoundIcon && !it.contains(COMPOUND_ICON_STUB)) {
                it.toMutableList().apply {
                    add(indexOf(Constants.IconSlots.ZEN), COMPOUND_ICON_STUB)
                }
            } else {
                it
            }
        }.mapNotNull {
            Constants.STATUS_BAR_ICON_SLOT_MAP[it]
        }.toMutableStateList()
    }
    val currentOnNegativeButton = remember {
        {
            navController.popBackStack()
        }
    }
    val currentOnPositiveButton = remember {
        {
            slots.mapIndexed { index, wrap ->
                "${index}:${wrap.slot}"
            }.toSet().let {
                SafeSP.putStringSet(Pref.Key.SystemUI.IconTuner.ICON_POSITION_VAL, it)
            }
            navController.popBackStack()
        }
    }

    val listState = rememberLazyListState()
    val dragDropState =
        rememberDragDropState(listState) { fromIndex, toIndex ->
            val fromI = (fromIndex - LIST_INDEX_OFFSET).coerceAtLeast(0)
            val toI = (toIndex - LIST_INDEX_OFFSET).coerceAtLeast(0)
            slots.apply { add(toI, removeAt(fromI)) }
        }
    val firstCardShape = remember {
        G2RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
    }
    val middleCardShape = remember {
        RectangleShape
    }
    val lastCardShape = remember {
        G2RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
    }
    val elevatedShape = remember {
        G2RoundedCornerShape(16.dp)
    }

    BackHandler(enabled = true) {
        currentOnNegativeButton.invoke()
    }
    val topAppBarBackground = MiuixTheme.colorScheme.background
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val topBarBlurState by remember {
        derivedStateOf {
            MainActivity.blurEnabled.value &&
                    scrollBehavior.state.collapsedFraction >= 1.0f &&
                    (listState.isScrollInProgress || listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 12)
        }
    }
    val topBarBlurTintAlpha = remember { mutableFloatStateOf(
        if (topAppBarBackground.luminance() >= 0.5f) MainActivity.blurTintAlphaLight.floatValue
        else MainActivity.blurTintAlphaDark.floatValue
    ) }
    val layoutDirection = LocalLayoutDirection.current
    val systemBarInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal).asPaddingValues()
    val navigationIconPadding = PaddingValues.Absolute(
        left = if (mode != BasePageDefaults.Mode.SPLIT_RIGHT) systemBarInsets.calculateLeftPadding(layoutDirection) else 0.dp
    )
    val actionsPadding = PaddingValues.Absolute(
        right = if (mode != BasePageDefaults.Mode.SPLIT_LEFT) systemBarInsets.calculateRightPadding(layoutDirection) else 0.dp
    )
    HazeScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { contentPadding ->
            TopAppBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                color = topAppBarBackground.copy(
                    if (topBarBlurState) 0f else 1f
                ),
                title = stringResource(R.string.page_status_bar_icon_order),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier
                            .padding(navigationIconPadding)
                            .padding(start = 21.dp)
                            .size(40.dp),
                        onClick = {
                            currentOnNegativeButton.invoke()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = MiuixIcons.ImmersionClose,
                            contentDescription = "Close",
                            tint = MiuixTheme.colorScheme.onSurfaceSecondary
                        )
                    }
                },
                actions = {
                    IconButton(
                        modifier = Modifier
                            .padding(actionsPadding)
                            .padding(end = 21.dp)
                            .size(40.dp),
                        onClick = {
                            currentOnPositiveButton.invoke()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = MiuixIcons.ImmersionConfirm,
                            contentDescription = "Confirm",
                            tint = MiuixTheme.colorScheme.onSurfaceSecondary
                        )
                    }
                },
                defaultWindowInsetsPadding = false,
                horizontalPadding = 28.dp + contentPadding.calculateLeftPadding(LocalLayoutDirection.current)
            )
        },
        blurTopBar = MainActivity.blurEnabled.value,
        hazeStyle = HazeStyle(
            blurRadius = 66.dp,
            backgroundColor = topAppBarBackground,
            tint = HazeTint(
                topAppBarBackground.copy(alpha = topBarBlurTintAlpha.floatValue),
            )
        ),
        adjustPadding = adjustPadding,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .dragContainer(dragDropState)
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .scrollEndHaptic()
                .height(getWindowSize().height.dp)
                .background(MiuixTheme.colorScheme.background),
            state = listState,
            contentPadding = paddingValues
        ) {
            item {
                PreferenceGroup(
                    first = true
                ) {
                    TextPreference(
                        title = stringResource(R.string.icon_tuner_position_reset_default)
                    ) {
                        slots.clear()
                        slots.addAll(
                            Constants.STATUS_BAR_ICONS_DEFAULT.toMutableList().let {
                                if (addCompoundIcon && !it.contains(COMPOUND_ICON_STUB)) {
                                    it.apply {
                                        add(indexOf(Constants.IconSlots.ZEN), COMPOUND_ICON_STUB)
                                    }
                                } else {
                                    it
                                }
                            }.mapNotNull {
                                Constants.STATUS_BAR_ICON_SLOT_MAP[it]
                            }
                        )
                    }
                }
                SmallTitle(
                    text = stringResource(R.string.ui_title_icon_tuner_order),
                    modifier = Modifier.padding(vertical = 6.dp),
                    textColor = MiuixTheme.colorScheme.onBackgroundVariant,
                )
            }
            itemsIndexed(slots, key = { _, item -> item.slot }) { index, item ->
                val isFirst = index == 0
                val isLast = index == slots.size - 1
                val cardBottomPadding = if (isLast) 12.dp else 0.dp
                DraggableItem(dragDropState, index + LIST_INDEX_OFFSET) { isDragging ->
                    val elevation by animateFloatAsState(if (isDragging) 4.0f else 1.0f)
                    val shape = if (isDragging) {
                        elevatedShape
                    } else if (isFirst) {
                        firstCardShape
                    } else if (isLast) {
                        lastCardShape
                    } else {
                        middleCardShape
                    }
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
}

@Composable
fun DraggableIcon(data: StatusBarIconSlotWrap) {
    val enabled = data.labelResId != 0
    BasicComponent(
        insideMargin = PaddingValues(16.dp),
        title = data.labelResId.takeIf { it > 0 }?.let { stringResource(it) } ?: "<${data.slot}>",
        leftAction = {
            Image(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(28.dp),
                painter = painterResource(data.iconResId),
                contentDescription = null
            )
        },
        rightActions = {
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(10.dp, 16.dp),
                imageVector = MiuixIcons.Basic.ArrowUpDownIntegrated,
                contentDescription = null,
                colorFilter = ColorFilter.tint(RightActionDefaults.rightActionColors().color(enabled)),
            )
        },
        enabled = enabled
    )
}