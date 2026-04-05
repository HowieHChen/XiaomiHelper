package dev.lackluster.mihelper.app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lackluster.hyperx.navigation.LocalNavigator
import dev.lackluster.hyperx.ui.component.Card
import dev.lackluster.hyperx.ui.component.CardDefaults
import dev.lackluster.hyperx.ui.component.IconSize
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.hyperx.ui.layout.HyperXPage
import dev.lackluster.hyperx.ui.preference.ItemPosition
import dev.lackluster.hyperx.ui.preference.TextPreference
import dev.lackluster.hyperx.ui.preference.itemAnimated
import dev.lackluster.hyperx.ui.preference.itemPreferenceGroup
import dev.lackluster.mihelper.BuildConfig.BUILD_TYPE
import dev.lackluster.mihelper.BuildConfig.VERSION_CODE
import dev.lackluster.mihelper.BuildConfig.VERSION_NAME
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.showToast
import dev.lackluster.mihelper.app.utils.openUrl
import dev.lackluster.mihelper.app.utils.toImageSource
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.Contributors
import dev.lackluster.mihelper.data.References
import dev.lackluster.mihelper.data.Route
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme

private sealed interface AboutPageAction {
    data class NavigateTo(val route: Route) : AboutPageAction
    data class OpenUrl(val url: UiText) : AboutPageAction
    data class ShowToast(val message: UiText, val long: Boolean = false) : AboutPageAction
}

@Composable
fun AboutPage() {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val onAction: (AboutPageAction) -> Unit = { action ->
        when (action) {
            is AboutPageAction.NavigateTo -> {
                navigator.push(action.route)
            }
            is AboutPageAction.OpenUrl -> {
                context.openUrl(action.url.asString(context))
            }
            is AboutPageAction.ShowToast -> {
                context.showToast(action.message.asString(context), action.long)
            }
        }
    }

    AboutPageContent(
        onAction = onAction
    )
}

@Composable
private fun AboutPageContent(
    onAction: (AboutPageAction) -> Unit
) {
    val showTopPopup = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    val contextMenuItems = listOf(
        stringResource(R.string.page_dev_ui_test),
        stringResource(R.string.page_dev_ui_test)
    )

    HyperXPage(
        title = stringResource(R.string.page_about),
        actions = {
            OverlayListPopup(
                show = showTopPopup.value,
                popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                alignment = PopupPositionProvider.Align.TopEnd,
                onDismissRequest = { showTopPopup.value = false }
            ) {
                ListPopupColumn {
                    contextMenuItems.forEachIndexed { index, string ->
                        DropdownImpl(
                            text = string,
                            optionSize = contextMenuItems.size,
                            isSelected = false,
                            onSelectedIndexChange = {
                                when (it) {
                                    0 -> onAction(AboutPageAction.NavigateTo(Route.DevUITest))
                                    1 -> onAction(AboutPageAction.NavigateTo(Route.DevUITest2))
                                }
                                showTopPopup.value = false
                            },
                            index = index
                        )
                    }
                }
            }
            IconButton(
                modifier = Modifier
                    .padding(end = 21.dp)
                    .size(40.dp),
                onClick = {
                    showTopPopup.value = true
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                holdDownState = showTopPopup.value
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.More,
                    contentDescription = "Menu",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        }
    ) {
        itemAnimated(
            key = "ABOUT_BANNER"
        ) {
            AdaptiveHeaderCard(
                colorCardContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                when ((0..2).random()) {
                                    0 -> onAction(AboutPageAction.ShowToast(R.string.about_easter_egg_toast.toUiText()))
                                    1 -> onAction(AboutPageAction.ShowToast(R.string.xposed_desc.toUiText()))
                                    2 -> onAction(AboutPageAction.OpenUrl("https://www.bilibili.com/video/BV1Xa4y1D7G3".toUiText()))
                                }
                            }
                    ) {
                        val offset: Offset
                        val blurRadius: Float
                        with(LocalDensity.current) {
                            offset = Offset(0f, 3.dp.toPx())
                            blurRadius = 6.dp.toPx()
                        }
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painterResource(R.drawable.empty_page_background),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.64f)),
                            contentScale = ContentScale.Crop,
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                color = Color.White.copy(alpha = 0.7f),
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.1f),
                                        offset = offset,
                                        blurRadius = blurRadius
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.heightIn(min = 12.dp))
                            Text(
                                text = "$VERSION_NAME($VERSION_CODE)-$BUILD_TYPE",
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = Color.White.copy(alpha = 0.7f),
                            )
                        }
                    }
                },
                infoCardContent = {
                    TextPreference(
                        icon = ImageIcon(source = R.mipmap.hyperx.toImageSource(), size = IconSize.App),
                        title = stringResource(R.string.about_hyperx_compose),
                        summary = stringResource(R.string.about_hyperx_compose_tips),
                        onClick = { onAction(AboutPageAction.OpenUrl("https://github.com/HowieHChen/hyperx-compose".toUiText())) }
                    )
                    TextPreference(
                        icon = ImageIcon(source = R.mipmap.miuix.toImageSource(), size = IconSize.App),
                        title = stringResource(R.string.about_miuix),
                        summary = stringResource(R.string.about_miuix_tips),
                        onClick = { onAction(AboutPageAction.OpenUrl("https://github.com/miuix-kotlin-multiplatform/miuix".toUiText())) }
                    )
                    TextPreference(
                        icon = ImageIcon(source = R.mipmap.ic_yukihookapi.toImageSource(), size = IconSize.App),
                        title = stringResource(R.string.about_yuki),
                        summary = stringResource(R.string.about_yuki_tips),
                        onClick = { onAction(AboutPageAction.OpenUrl("https://github.com/HighCapable/YuKiHookAPI".toUiText())) }
                    )
                }
            )
        }
        itemPreferenceGroup(
            titleRes = R.string.ui_title_about_developer,
        ) {
            TextPreference(
                icon = ImageIcon(
                    source = R.mipmap.developer_howie.toImageSource(),
                    size = IconSize.App,
                    cornerRadius = 30.dp
                ),
                title = stringResource(R.string.about_author),
                summary = stringResource(R.string.about_author_tips),
                onClick = { onAction(AboutPageAction.OpenUrl("https://github.com/HowieHChen".toUiText())) }
            )
            for (contributor in Contributors.list) {
                TextPreference(
                    icon = ImageIcon(
                        source = contributor.avatarResId.toImageSource(),
                        size = IconSize.App,
                        cornerRadius = 30.dp
                    ),
                    title = contributor.name,
                    summary = contributor.bio,
                    onClick = { onAction(AboutPageAction.OpenUrl(contributor.link.toUiText())) }
                )
            }
        }

        itemPreferenceGroup(
            titleRes = R.string.ui_title_about_others,
        ) {
            TextPreference(
                title = stringResource(R.string.about_translate),
                summary = stringResource(R.string.about_translate_tips),
                onClick = { onAction(AboutPageAction.OpenUrl("https://hosted.weblate.org/engage/hyper-helper/".toUiText())) }
            )
            TextPreference(
                title = stringResource(R.string.about_donate),
                summary = stringResource(R.string.about_donate_tips),
                onClick = { onAction(AboutPageAction.OpenUrl("https://github.com/HowieHChen/XiaomiHelper/blob/master/DONATE.md".toUiText())) }
            )
            TextPreference(
                title = stringResource(R.string.about_repository),
                summary = stringResource(R.string.about_repository_tips),
                onClick = { onAction(AboutPageAction.OpenUrl("https://github.com/HowieHChen/XiaomiHelper".toUiText())) }
            )
            TextPreference(
                title = stringResource(R.string.about_telegram),
                summary = stringResource(R.string.about_telegram_tips),
                onClick = { onAction(AboutPageAction.OpenUrl("https://t.me/lackluster_stuff".toUiText())) }
            )
        }

        itemPreferenceGroup(
            titleRes = R.string.ui_title_about_reference,
            position = ItemPosition.Last
        ) {
            for (project in References.list) {
                TextPreference(
                    title = project.name,
                    summary = project.license,
                    onClick = { onAction(AboutPageAction.OpenUrl(project.link.toUiText())) }
                )
            }
        }
    }
}

@Composable
private fun AdaptiveHeaderCard(
    colorCardContent: @Composable () -> Unit,
    infoCardContent: @Composable () -> Unit
) {
    Layout(
        content = {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F83FE))
            ) {
                colorCardContent()
            }
            Card {
                infoCardContent()
            }
        }
    ) { measurables, constraints ->
        if (measurables.size != 2) {
            layout(0, 0) { }
        }
        if (constraints.maxWidth >= 768.dp.roundToPx()) {
            val cardWidthPx = (constraints.maxWidth - 48.dp.roundToPx()) / 2
            val infoCard = measurables[1].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx
            ))
            val colorCard = measurables[0].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx,
                minHeight = infoCard.height, maxHeight = infoCard.height
            ))
            val layoutHeight = infoCard.height + 18.dp.roundToPx()
            layout(constraints.maxWidth, layoutHeight) {
                colorCard.place(12.dp.roundToPx(), 12.dp.roundToPx())
                infoCard.place(constraints.maxWidth - cardWidthPx - 12.dp.roundToPx(), 12.dp.roundToPx())
            }
        } else {
            val cardWidthPx = constraints.maxWidth - 24.dp.roundToPx()
            val infoCard = measurables[1].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx
            ))
            val colorCardHeight = cardWidthPx / 2
            val colorCard = measurables[0].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx,
                minHeight = colorCardHeight, maxHeight = colorCardHeight
            ))
            val layoutHeight = colorCard.height + infoCard.height + 30.dp.roundToPx()
            layout(constraints.maxWidth, layoutHeight) {
                colorCard.place(12.dp.roundToPx(), 12.dp.roundToPx())
                infoCard.place(12.dp.roundToPx(), colorCard.height + 24.dp.roundToPx())
            }
        }
    }
}