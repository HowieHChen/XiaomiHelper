package dev.lackluster.mihelper.ui.dialog

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.icon.ImmersionClose
import dev.lackluster.hyperx.compose.icon.ImmersionConfirm
import dev.lackluster.hyperx.compose.preference.EditableTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.ui.MainActivity
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.hook.rules.search.SearchEngineItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SearchCustomEngineDialog(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val context = LocalContext.current
    var customSearchEngine by remember { mutableStateOf(SafeSP.getBoolean(Pref.Key.Search.CUSTOM_SEARCH_ENGINE)) }
    val channelNo = remember { mutableStateOf("") }
    val showIcon = remember { mutableStateOf(true) }
    val searchUrl = remember { mutableStateOf("") }
    val iconUrl = remember { mutableStateOf("") }
    val titleLzhCN = remember { mutableStateOf("") }
    val titleLzhTW = remember { mutableStateOf("") }
    val titleLenUS = remember { mutableStateOf("") }
    val titleLboCN = remember { mutableStateOf("") }
    val titleLugCN = remember { mutableStateOf("") }

    SafeSP.getString(Pref.Key.Search.CUSTOM_SEARCH_ENGINE_ENTITY, "").takeIf {
        it.isNotEmpty()
    }?.let {
        SearchEngineItem.decodeFromString(it)
    }?.let {
        channelNo.value = it.channelNo
        showIcon.value = it.showIcon
        searchUrl.value = it.searchUrl
        iconUrl.value = it.iconUrl
        titleLzhCN.value = it.titleLzhCN
        titleLzhTW.value = it.titleLzhTW
        titleLenUS.value = it.titleLenUS
        titleLboCN.value = it.titleLboCN
        titleLugCN.value = it.titleLugCN
    }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.others_search_custom_search_engine),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode,
        navigationIcon = { padding ->
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
                    imageVector = MiuixIcons.ImmersionClose,
                    contentDescription = "Close",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        },
        actions = { padding ->
            IconButton(
                modifier = Modifier
                    .padding(padding)
                    .padding(end = 21.dp)
                    .size(40.dp),
                onClick = {
                    if (customSearchEngine) {
                        var valid = false
                        val errorMSg = StringBuilder()
                        with(context) {
                            errorMSg.append(getString((R.string.common_invalid_input)))
                            errorMSg.append("\n")
                            if (!searchUrl.value.contains("{searchTerms}")) {
                                errorMSg.append(getString(R.string.others_search_custom_search_url))
                            } else if (showIcon.value && iconUrl.value.isEmpty()) {
                                errorMSg.append(getString(R.string.others_search_custom_icon_url))
                            } else if (
                                titleLzhCN.value.isEmpty() && titleLzhTW.value.isEmpty() &&
                                titleLenUS.value.isEmpty() && titleLboCN.value.isEmpty() && titleLugCN.value.isEmpty()
                            ) {
                                errorMSg.append(getString(R.string.others_search_custom_title))
                            } else {
                                valid = true
                            }
                        }
                        if (valid) {
                            SearchEngineItem(
                                "custom",
                                channelNo.value,
                                showIcon.value,
                                searchUrl.value,
                                iconUrl.value,
                                titleLzhCN.value,
                                titleLzhTW.value,
                                titleLenUS.value,
                                titleLboCN.value,
                                titleLugCN.value,
                            ).let {
                                SearchEngineItem.encodeToString(it)
                            }.let {
                                SafeSP.putAny(Pref.Key.Search.CUSTOM_SEARCH_ENGINE_ENTITY, it)
                            }
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, errorMSg.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = MiuixIcons.ImmersionConfirm,
                    contentDescription = "Confirm",
                    tint = MiuixTheme.colorScheme.onSurfaceSecondary
                )
            }
        }
    ) {
        item {
            PreferenceGroup(
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_search_custom_search_engine),
                    key = Pref.Key.Search.CUSTOM_SEARCH_ENGINE
                ) {
                    customSearchEngine = it
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.others_search_custom_url)
            ) {
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_search_url),
                    summary = stringResource(R.string.others_search_custom_search_url_tips),
                    textValue = searchUrl,
                    textHint = stringResource(R.string.others_search_custom_search_url_hint)
                )
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_channel),
                    summary = stringResource(R.string.others_search_custom_channel_tips),
                    textValue = channelNo
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.others_search_custom_icon)
            ) {
                SwitchPreference(
                    title = stringResource(R.string.others_search_custom_show_icon),
                    defValue = showIcon.value
                ) {
                    showIcon.value = it
                }
                AnimatedVisibility(
                    showIcon.value
                ) {
                    EditableTextPreference(
                        title = stringResource(R.string.others_search_custom_icon_url),
                        summary = stringResource(R.string.others_search_custom_icon_url_tips),
                        textValue = iconUrl,
                        textHint = stringResource(R.string.others_search_custom_icon_url_hint)
                    )
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.others_search_custom_title)
            ) {
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_title_zh_cn),
                    textValue = titleLzhCN,
                    textHint = stringResource(R.string.others_search_custom_title_hint)
                )
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_title_zh_tw),
                    textValue = titleLzhTW,
                    textHint = stringResource(R.string.others_search_custom_title_hint)
                )
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_title_en_us),
                    textValue = titleLenUS,
                    textHint = stringResource(R.string.others_search_custom_title_hint)
                )
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_title_bo_cn),
                    textValue = titleLboCN,
                    textHint = stringResource(R.string.others_search_custom_title_hint)
                )
                EditableTextPreference(
                    title = stringResource(R.string.others_search_custom_title_ug_cn),
                    textValue = titleLugCN,
                    textHint = stringResource(R.string.others_search_custom_title_hint)
                )
            }
        }
        item {
            PreferenceGroup(
                first = true,
                last = true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            channelNo.value = ""
                            showIcon.value = true
                            searchUrl.value = ""
                            iconUrl.value = ""
                            titleLzhCN.value = ""
                            titleLzhTW.value = ""
                            titleLenUS.value = ""
                            titleLboCN.value = ""
                            titleLugCN.value = ""
                            SafeSP.mSP?.edit {
                                remove(Pref.Key.Search.CUSTOM_SEARCH_ENGINE_ENTITY)
                                commit()
                            }
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(R.string.module_reset),
                        fontSize = MiuixTheme.textStyles.headline1.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFCC0000)
                    )
                }
            }
        }
    }
}