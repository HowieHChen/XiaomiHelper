package dev.lackluster.mihelper.ui.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavController
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.mihelper.R
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownMode
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.hyperx.compose.base.IconSize
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.ValuePosition
import dev.lackluster.mihelper.data.Pref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.icon.icons.useful.ImmersionMore
import top.yukonga.miuix.kmp.icon.icons.useful.Info
import top.yukonga.miuix.kmp.icon.icons.useful.More
import top.yukonga.miuix.kmp.icon.icons.useful.NavigatorSwitch
import top.yukonga.miuix.kmp.icon.icons.useful.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun UITestPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current

    val blurTintColor = MiuixTheme.colorScheme.background
    val configBlurTopBar = remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.App.HAZE_BLUR, true)) }
    val configBlurBottomBar = remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.App.HAZE_BLUR, true)) }
    val configBlurAlpha = remember { mutableIntStateOf(
        if (blurTintColor.luminance() >= 0.5)
            SafeSP.getInt(Pref.Key.App.HAZE_TINT_ALPHA_LIGHT, 60)
        else
            SafeSP.getInt(Pref.Key.App.HAZE_TINT_ALPHA_DARK, 50)
    ) }

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val topBarBlurState by remember {
        derivedStateOf {
            configBlurTopBar.value &&
                    scrollBehavior.state.collapsedFraction >= 1.0f &&
                    (listState.isScrollInProgress || listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 10)
        }
    }
    val showBottomBar = remember { mutableStateOf(false) }
    val showFPSMonitor = remember { mutableStateOf(true) }
    var targetPage by remember { mutableIntStateOf(0) }

    val isTopPopupExpanded = remember { mutableStateOf(false) }
    val showTopPopup = remember { mutableStateOf(false) }
    val isBottomPopupExpanded = remember { mutableStateOf(false) }
    val showBottomPopup = remember { mutableStateOf(false) }
    val items = listOf(
        NavigationItem("HomePage", MiuixIcons.Useful.NavigatorSwitch),
        NavigationItem("DropDown", MiuixIcons.Useful.Info),
        NavigationItem("Settings", MiuixIcons.Useful.Settings),
        NavigationItem("More", MiuixIcons.Useful.More)
    )

    val dropdownEntries = listOf(
        DropDownEntry(stringResource(R.string.module_settings_icon_color_gray), "123", R.drawable.ic_color_gray),
        DropDownEntry(stringResource(R.string.module_settings_icon_color_red), "456", R.drawable.ic_color_red),
        DropDownEntry(stringResource(R.string.module_settings_icon_color_green), "789", R.drawable.ic_color_green),
        DropDownEntry(stringResource(R.string.module_settings_icon_color_blue), "123", R.drawable.ic_color_blue),
        DropDownEntry(stringResource(R.string.module_settings_icon_color_purple), "456", R.drawable.ic_color_purple),
        DropDownEntry(stringResource(R.string.module_settings_icon_color_yellow), "789", R.drawable.ic_color_yellow)
    )
    val hazeStyle = HazeStyle(
        blurRadius = 66.dp,
        backgroundColor = blurTintColor,
        tint = HazeTint(
            blurTintColor.copy(alpha = configBlurAlpha.intValue / 100f),
        ),
    )
    val hapticFeedback = LocalHapticFeedback.current
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
                    .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                    .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Top)),
                color = MiuixTheme.colorScheme.background.copy(
                    if (topBarBlurState) 0f else 1f
                ),
                title = stringResource(R.string.page_dev_ui_test),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(navigationIconPadding).padding(start = 21.dp).size(40.dp),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = MiuixIcons.Useful.Back,
                            contentDescription = "Back",
                            tint = MiuixTheme.colorScheme.onSurfaceSecondary
                        )
                    }
                },
                actions = {
                    if (isTopPopupExpanded.value) {
                        ListPopup(
                            show = showTopPopup,
                            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                            alignment = PopupPositionProvider.Align.TopRight,
                            onDismissRequest = {
                                isTopPopupExpanded.value = false
                            }
                        ) {
                            ListPopupColumn {
                                items.take(3).forEachIndexed { index, navigationItem ->
                                    DropdownImpl(
                                        text = navigationItem.label,
                                        optionSize = 3,
                                        isSelected = false,
                                        onSelectedIndexChange = {
                                            targetPage = index
                                            Toast.makeText(context, "$it clicked", Toast.LENGTH_SHORT).show()
                                            showTopPopup.value = false
                                            isTopPopupExpanded.value = false
                                        },
                                        index = index
                                    )
                                }
                            }
                        }
                        showTopPopup.value = true
                    }
                    IconButton(
                        modifier = Modifier.padding(actionsPadding).padding(end = 21.dp).size(40.dp),
                        onClick = {
                            isTopPopupExpanded.value = true
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.ImmersionMore,
                            contentDescription = "Menu"
                        )
                    }
                },
                defaultWindowInsetsPadding = false,
                horizontalPadding = 28.dp + contentPadding.calculateLeftPadding(LocalLayoutDirection.current)
            )
        },
        bottomBar = { _ ->
            AnimatedVisibility(
                visible = showBottomBar.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (isBottomPopupExpanded.value) {
                    ListPopup(
                        show = showBottomPopup,
                        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                        alignment = PopupPositionProvider.Align.BottomRight,
                        onDismissRequest = {
                            showBottomPopup.value = false
                            isBottomPopupExpanded.value = false
                        }
                    ) {
                        ListPopupColumn {
                            items.take(3).fastForEachIndexed { index, navigationItem ->
                                DropdownImpl(
                                    text = navigationItem.label,
                                    optionSize = 3,
                                    isSelected = items[index] == items[targetPage],
                                    onSelectedIndexChange = {
                                        targetPage = index
                                        Toast.makeText(context, "$it clicked", Toast.LENGTH_SHORT).show()
                                        showBottomPopup.value = false
                                        isBottomPopupExpanded.value = false
                                    },
                                    index = index
                                )
                            }
                        }
                    }
                    showBottomPopup.value = true
                }
                NavigationBar(
                    color = MiuixTheme.colorScheme.background.copy(
                        if (configBlurBottomBar.value) 0f else 1f
                    ),
                    items = items,
                    selected = targetPage,
                    onClick = { index ->
                        if (index in 0..2) {
                            targetPage = index
                            Toast.makeText(context, "Page $index clicked", Toast.LENGTH_SHORT).show()
                        } else {
                            showBottomPopup.value = true
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                )
            }
        },
        adjustPadding = adjustPadding,
        blurTopBar = configBlurTopBar.value,
        blurBottomBar = configBlurBottomBar.value,
        hazeStyle = hazeStyle
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
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
                    title = "UI",
                    first = true
                ) {
                    SwitchPreference(
                        title = "Show FPSMonitor",
                        defValue = showFPSMonitor.value
                    ) {
                        showFPSMonitor.value = it
                    }
                    SwitchPreference(
                        title = "Show BottomAppBar",
                        defValue = showBottomBar.value
                    ) {
                        showBottomBar.value = it
                    }
                    SwitchPreference(
                        title = "Enable TopAppBar blur",
                        summary = "Blur state: $topBarBlurState",
                        defValue = configBlurTopBar.value
                    ) {
                        configBlurTopBar.value = it
                    }
                    AnimatedVisibility(
                        visible = showBottomBar.value
                    ) {
                        SwitchPreference(
                            title = "Enable BottomAppBar blur",
                            summary = "Blur state: ${configBlurBottomBar.value}",
                            defValue = configBlurBottomBar.value
                        ) {
                            configBlurBottomBar.value = it
                        }
                    }
                    AnimatedVisibility(
                        visible = configBlurTopBar.value || configBlurBottomBar.value
                    ) {
                        SeekBarPreference(
                            title = "Tint alpha",
                            defValue = configBlurAlpha.intValue,
                            min = 0,
                            max = 100,
                            format = "%d%%"
                        ) {
                            configBlurAlpha.intValue = it
                        }
                    }
                }
            }
            item {
                val switchSummary = remember { mutableStateOf(false.toString()) }
                PreferenceGroup(
                    "Icon"
                ) {
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                            iconSize = IconSize.Large,
                            cornerRadius = 16.dp,
                        ),
                        title = "Large",
                        summary = "Rounded rectangle",

                        value = "Value"
                    ) {
                        Toast.makeText(context, "[HeaderPreference] Large clicked", Toast.LENGTH_SHORT).show()
                    }
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                            iconSize = IconSize.Medium,
                            cornerRadius = 38.dp,
                        ),
                        title = "Medium",
                        summary = "Round",
                        value = "Value"
                    ) {
                        Toast.makeText(context, "[HeaderPreference] Medium clicked", Toast.LENGTH_SHORT).show()
                    }
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                        ),
                        title = "Small",
                        summary = "Rectangle",
                        value = "Value"
                    ) {
                        Toast.makeText(context, "[HeaderPreference] Small clicked", Toast.LENGTH_SHORT).show()
                    }
                    TextPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_header_others),
                        title = "TextPreference",
                        summary = "Summary",
                        value = "Value"
                    ) {
                        Toast.makeText(context, "[HeaderPreference] Others clicked", Toast.LENGTH_SHORT).show()
                    }
                    SwitchPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_header_about),
                        title = "SwitchPreference",
                        summary = switchSummary.value
                    ) {
                        switchSummary.value = it.toString()
                    }
                    DropDownPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_header_home),
                        title = "DropdownPreference",
                        summary = "Summary",
                        entries = dropdownEntries,
                        mode = DropDownMode.AlwaysOnRight
                    )
                    SeekBarPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                            iconSize = IconSize.SeekBar,
                            cornerRadius = 8.dp
                        ),
                        title = "SeekBarPreference",
                        defValue = 10,
                        min = 0,
                        max = 20,
                        format = "%dUnit"
                    )
                    EditTextPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_header_security_center),
                        title = "EditTextPreference 5",
                        summary = "STRING",
                        defValue = "string",
                        dataType = EditTextDataType.STRING,
                        dialogMessage = "STRING",
                    )
                }
            }
            item {
                val textValue = remember { mutableIntStateOf(0) }
                val switchSummary = remember { mutableStateOf(false.toString()) }
                PreferenceGroup(
                    "App"
                ) {
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                            iconSize = IconSize.App,
                        ),
                        title = "TextPreference",
                        summary = "com.example.package",
                        value = textValue.intValue.toString()
                    ) {
                        textValue.intValue++
                    }
                    SwitchPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                            iconSize = IconSize.App,
                        ),
                        title = "SwitchPreference",
                        summary = switchSummary.value,
                        defValue = false
                    ) {
                        switchSummary.value = it.toString()
                    }
                    DropDownPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.ic_launcher_background,
                            iconSize = IconSize.App,
                        ),
                        title = "DropdownPreference",
                        summary = "Summary",
                        entries = dropdownEntries,
                        mode = DropDownMode.AlwaysOnRight
                    )
                }
            }
            item {
                val summary = remember { mutableStateOf(false.toString()) }
                PreferenceGroup(
                    title = "Preference",
                    last = false
                ) {
                    SwitchPreference(
                        title = "SwitchPreference",
                        summary = summary.value,
                        defValue = false
                    ) {
                        summary.value = it.toString()
                    }
                    DropDownPreference(
                        title = "DropdownPreference 1",
                        summary = "Normal mode",
                        entries = dropdownEntries,
                        mode =  DropDownMode.Normal
                    )
                    DropDownPreference(
                        title = "DropdownPreference 2",
                        summary = "AlwaysOnRight mode",
                        entries = dropdownEntries,
                        mode =  DropDownMode.AlwaysOnRight,
                        showValue = false
                    )
                    DropDownPreference(
                        title = "DropdownPreference 3",
                        summary = "Dialog mode",
                        entries = dropdownEntries,
                        mode = DropDownMode.Dialog
                    )
                    SeekBarPreference(
                        title = "SeekBarPreference 1",
                        defValue = 0,
                        min = 0,
                        max = 15
                    )
                    EditTextPreference(
                        title = "EditTextPreference 1",
                        summary = "BOOLEAN",
                        defValue = true,
                        dataType = EditTextDataType.BOOLEAN,
                        dialogMessage = "true/false"
                    )
                    EditTextPreference(
                        title = "EditTextPreference 2",
                        summary = "INT",
                        defValue = 10,
                        dataType = EditTextDataType.INT,
                        dialogMessage = "10 <= value <= 20",
                        isValueValid = { value: Any ->
                            (value as? Int) in 10..20
                        }
                    )
                    EditTextPreference(
                        title = "EditTextPreference 3",
                        summary = "LONG",
                        defValue = 100L,
                        dataType = EditTextDataType.LONG,
                        dialogMessage = "100 <= value <= 200",
                        isValueValid = { value: Any ->
                            (value as? Long) in 100L..200L
                        }
                    )
                    EditTextPreference(
                        title = "EditTextPreference 4",
                        summary = "FLOAT",
                        defValue = 1.0f,
                        dataType = EditTextDataType.FLOAT,
                        dialogMessage = "-1.0f <= value <= 1.0f",
                        isValueValid = { value: Any ->
                            (value as? Float ?: return@EditTextPreference false) in -1.0f..1.0f
                        }
                    )
                    EditTextPreference(
                        title = "EditTextPreference 5",
                        summary = "STRING",
                        defValue = "string",
                        dataType = EditTextDataType.STRING,
                        dialogMessage = "STRING",
                    )
                    EditTextPreference(
                        title = "EditTextPreference 6",
                        defValue = "string",
                        dataType = EditTextDataType.STRING,
                        dialogMessage = "STRING",
                        valuePosition = ValuePosition.SUMMARY_VIEW
                    )
                }
            }
        }
    }
    AnimatedVisibility(
        visible = showFPSMonitor.value,
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally()
    ) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .captionBarPadding()
                .padding(horizontal = 4.dp)
        )
    }
}

/**
 * @see <a href="https://github.com/miuix-kotlin-multiplatform/miuix/blob/main/composeApp/src/commonMain/kotlin/utils/FPSMonitor.kt">FPSMonitor.kt</a>
 */
@Composable
fun FPSMonitor(modifier: Modifier = Modifier) {
    var fps by remember { mutableIntStateOf(0) }
    var maxFps by remember { mutableIntStateOf(0) }
    var lastFrameTime by remember { mutableLongStateOf(0L) }
    var frameCount by remember { mutableIntStateOf(0) }
    var totalFrameTime by remember { mutableLongStateOf(0L) }

    if (fps > maxFps) {
        maxFps = fps
    }

    val color = when {
        fps >= maxFps - 2 -> Color.Green
        fps >= maxFps - 6 -> Color.Blue
        fps >= maxFps - 15 -> Color(0xFFFFD700)
        else -> Color.Red
    }

    Text(
        text = "FPS: $fps",
        modifier = modifier,
        color = color
    )

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (true) {
                withFrameMillis { frameTimeMillis ->
                    if (lastFrameTime != 0L) {
                        val frameDuration = frameTimeMillis - lastFrameTime
                        totalFrameTime += frameDuration
                        frameCount++
                        if (totalFrameTime >= 1000L) {
                            fps = frameCount
                            frameCount = 0
                            totalFrameTime = 0L
                        }
                    }
                    lastFrameTime = frameTimeMillis
                }
            }
        }
    }
}