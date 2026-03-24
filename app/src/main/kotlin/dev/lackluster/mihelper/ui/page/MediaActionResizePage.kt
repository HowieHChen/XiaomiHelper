package dev.lackluster.mihelper.ui.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.HazeScaffold
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.viewmodel.AppListViewModel
import dev.lackluster.mihelper.data.Pref
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun MediaActionResizePage(
    navController: NavController,
    adjustPadding: PaddingValues,
    title: String,
    mode: BasePageDefaults.Mode = BasePageDefaults.Mode.FULL,
    navigationIcon: @Composable (padding: PaddingValues) -> Unit = { padding ->
        IconButton(
            modifier = Modifier
                .padding(padding)
                .padding(start = 21.dp)
                .size(40.dp),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                imageVector = MiuixIcons.Back,
                contentDescription = "Back",
                tint = MiuixTheme.colorScheme.onSurfaceSecondary
            )
        }
    },
    actions: @Composable RowScope.(padding: PaddingValues) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = viewModel<AppListViewModel>()
    val scope = rememberCoroutineScope()

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()

    val layoutDirection = LocalLayoutDirection.current
    val systemBarInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal).asPaddingValues()
    val navigationIconPadding = PaddingValues.Absolute(
        left = if (mode != BasePageDefaults.Mode.SPLIT_RIGHT) systemBarInsets.calculateLeftPadding(layoutDirection) else 0.dp
    )
    val actionsPadding = PaddingValues.Absolute(
        right = if (mode != BasePageDefaults.Mode.SPLIT_LEFT) systemBarInsets.calculateRightPadding(layoutDirection) else 0.dp
    )
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    val configBlurTopBar = remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.App.HAZE_BLUR, true)) }

    LaunchedEffect(key1 = navController) {
        viewModel.search = ""
        if (viewModel.appList.isEmpty()) {
            viewModel.fetchAppList(context)
        }
    }

    LaunchedEffect(viewModel.isRefreshing) {
        isRefreshing = viewModel.isRefreshing
    }

    LaunchedEffect(viewModel.search) {
        if (viewModel.search.isEmpty()) {
            listState.scrollToItem(0)
        }
    }

    HazeScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { contentPadding ->
            TopAppBar(
                color = if (configBlurTopBar.value) Color.Transparent else MiuixTheme.colorScheme.surface,
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = { navigationIcon.invoke(navigationIconPadding) },
                actions = { actions(this, actionsPadding) },
                defaultWindowInsetsPadding = false,
                horizontalPadding = 28.dp + contentPadding.calculateLeftPadding(LocalLayoutDirection.current)
            )
        },
        blurTopBar = configBlurTopBar.value,
        adjustPadding = adjustPadding,
    ) { paddingValues ->
        PullToRefresh(
            isRefreshing = isRefreshing,
            pullToRefreshState = pullToRefreshState,
            onRefresh = {
                scope.launch { viewModel.fetchAppList(context) }
            },
            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .scrollEndHaptic()
                    .height(with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }),
                state = listState,
                contentPadding = paddingValues,
                overscrollEffect = null,
            ) {
                items(viewModel.appList, key = { it.packageName + it.uid }) { app ->
                    SwitchPreference(
                        title = app.label,
                        summary = app.packageName
                    )
                }
            }
        }
    }
}