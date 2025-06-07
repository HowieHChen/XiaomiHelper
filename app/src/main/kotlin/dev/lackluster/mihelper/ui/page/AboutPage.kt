package dev.lackluster.mihelper.ui.page

import android.content.Context
import android.content.Intent
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.IconSize
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference
import dev.lackluster.mihelper.BuildConfig.BUILD_TYPE
import dev.lackluster.mihelper.BuildConfig.VERSION_CODE
import dev.lackluster.mihelper.BuildConfig.VERSION_NAME
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Contributors
import dev.lackluster.mihelper.data.References
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.core.net.toUri
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.mihelper.data.Pages
import kotlin.random.Random

@Composable
fun AboutPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current
    
    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.page_about),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            AdaptiveHeaderCard(
                colorCardContent = {
                    Box(
                        modifier = Modifier.fillMaxSize().clickable {
                            when ((0..2).random()) {
                                0 -> {
                                    makeText(context, R.string.about_easter_egg_toast, LENGTH_SHORT).show()
                                }
                                1 -> {
                                    makeText(context, R.string.xposed_desc, LENGTH_SHORT).show()
                                }
                                2 -> {
                                    context.openUrl(R.string.about_easter_egg_url)
                                }
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
                            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
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
                        icon = ImageIcon(
                            iconRes = R.mipmap.hyperx,
                            iconSize = IconSize.App
                        ),
                        title = stringResource(R.string.about_hyperx_compose),
                        summary = stringResource(R.string.about_hyperx_compose_tips)
                    ) {
                        context.openUrl(R.string.about_hyperx_compose_url)
                    }
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.mipmap.miuix,
                            iconSize = IconSize.App
                        ),
                        title = stringResource(R.string.about_miuix),
                        summary = stringResource(R.string.about_miuix_tips)
                    ) {
                        context.openUrl(R.string.about_miuix_url)
                    }
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.mipmap.ic_yukihookapi,
                            iconSize = IconSize.App
                        ),
                        title = stringResource(R.string.about_yuki),
                        summary = stringResource(R.string.about_yuki_tips)
                    ) {
                        context.openUrl(R.string.about_yuki_url)
                    }
                }
            )
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_about_developer)
            ) {
                TextPreference(
                    icon = ImageIcon(
                        iconRes = R.mipmap.developer_howie,
                        iconSize = IconSize.App,
                        cornerRadius = 30.dp
                    ),
                    title = stringResource(R.string.about_author),
                    summary = stringResource(R.string.about_author_tips)
                ) {
                    context.openUrl("https://github.com/HowieHChen")
                }
                for (contributor in Contributors.list) {
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = contributor.avatarResId,
                            iconSize = IconSize.App,
                            cornerRadius = 30.dp
                        ),
                        title = contributor.name,
                        summary = contributor.bio
                    ) {
                        context.openUrl(contributor.link)
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_about_others)
            ) {
                TextPreference(
                    title = stringResource(R.string.about_donate),
                    summary = stringResource(R.string.about_donate_tips)
                ) {
                    context.openUrl(R.string.about_donate_url)
                }
                TextPreference(
                    title = stringResource(R.string.about_repository),
                    summary = stringResource(R.string.about_repository_tips)
                ) {
                    context.openUrl(R.string.about_repository_url)
                }
                TextPreference(
                    title = stringResource(R.string.about_telegram),
                    summary = stringResource(R.string.about_telegram_tips)
                ) {
                    context.openUrl(R.string.about_telegram_url)
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_about_reference),
                last = true
            ) {
                for (project in References.list) {
                    TextPreference(
                        title = project.name,
                        summary = project.license
                    ) {
                        context.openUrl(project.link)
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.ui_title_about_dev_options),
                last = true
            ) {
                TextPreference(
                    title = stringResource(R.string.page_dev_ui_test)
                ) {
                    navController.navigateTo(
                        if (Random(System.currentTimeMillis()).nextInt(100) < 50)
                            Pages.DEV_UI_TEST
                        else
                            Pages.DEV_UI_TEST2
                    )
                }
            }
        }
    }
}

fun Context.openUrl(urlResId: Int) {
    openUrl(getString(urlResId))
}

fun Context.openUrl(url: String) {
    try {
        val uri = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    } catch (_: Exception) {
        makeText(this, R.string.about_jump_error_toast, LENGTH_SHORT).show()
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
                color = Color("#1F83FE".toColorInt())
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