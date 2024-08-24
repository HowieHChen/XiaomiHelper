package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DialogData
import cn.fkj233.ui.activity.data.EditTextData
import cn.fkj233.ui.activity.data.SwitchData
import cn.fkj233.ui.activity.data.TextData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.px

@BMPage(Pages.HOME_REFACTOR, hideMenu = false)
class HomeRefactorPage : BasePage() {
    private val paramsHashMap = mapOf(
        "recents_launch_view" to Pair(Refactor.SHOW_LAUNCH_IN_RECENTS, HomeRefactor.SHOW_LAUNCH_IN_RECENTS),
        "recents_launch_scale" to Pair(Refactor.SHOW_LAUNCH_IN_RECENTS_SCALE, HomeRefactor.SHOW_LAUNCH_IN_RECENTS_SCALE),
        "folder_launch_view" to Pair(Refactor.SHOW_LAUNCH_IN_FOLDER, HomeRefactor.SHOW_LAUNCH_IN_FOLDER),
        "folder_launch_scale" to Pair(Refactor.SHOW_LAUNCH_IN_FOLDER_SCALE, HomeRefactor.SHOW_LAUNCH_IN_FOLDER_SCALE),
        "minus_launch_view" to Pair(Refactor.SHOW_LAUNCH_IN_MINUS, HomeRefactor.SHOW_LAUNCH_IN_MINUS),
        "minus_launch_scale" to Pair(Refactor.SHOW_LAUNCH_IN_MINUS_SCALE, HomeRefactor.SHOW_LAUNCH_IN_MINUS_SCALE),
        "minus_overlap" to Pair(Refactor.MINUS_OVERLAP, HomeRefactor.MINUS_OVERLAP),

        "apps_blur" to Pair(Refactor.APPS_BLUR, HomeRefactor.APPS_BLUR),
        "apps_blur_radius" to Pair(Refactor.APPS_BLUR_RADIUS_STR, HomeRefactor.APPS_BLUR_RADIUS_STR),
        "apps_dim" to Pair(Refactor.APPS_DIM, HomeRefactor.APPS_DIM),
        "apps_dim_alpha" to Pair(Refactor.APPS_DIM_MAX, HomeRefactor.APPS_DIM_MAX),
        "apps_nonlinear_type" to Pair(Refactor.APPS_NONLINEAR_TYPE, HomeRefactor.APPS_NONLINEAR_TYPE),
        "apps_nonlinear_factor" to Pair(Refactor.APPS_NONLINEAR_DECE_FACTOR, HomeRefactor.APPS_NONLINEAR_DECE_FACTOR),
        "apps_nonlinear_x1" to Pair(Refactor.APPS_NONLINEAR_PATH_X1, HomeRefactor.APPS_NONLINEAR_PATH_X1),
        "apps_nonlinear_y1" to Pair(Refactor.APPS_NONLINEAR_PATH_Y1, HomeRefactor.APPS_NONLINEAR_PATH_Y1),
        "apps_nonlinear_x2" to Pair(Refactor.APPS_NONLINEAR_PATH_X2, HomeRefactor.APPS_NONLINEAR_PATH_X2),
        "apps_nonlinear_y2" to Pair(Refactor.APPS_NONLINEAR_PATH_Y2, HomeRefactor.APPS_NONLINEAR_PATH_Y2),

        "folder_blur" to Pair(Refactor.FOLDER_BLUR, HomeRefactor.FOLDER_BLUR),
        "folder_blur_radius" to Pair(Refactor.FOLDER_BLUR_RADIUS_STR, HomeRefactor.FOLDER_BLUR_RADIUS_STR),
        "folder_dim" to Pair(Refactor.FOLDER_DIM, HomeRefactor.FOLDER_DIM),
        "folder_dim_alpha" to Pair(Refactor.FOLDER_DIM_MAX, HomeRefactor.FOLDER_DIM_MAX),
        "folder_nonlinear_type" to Pair(Refactor.FOLDER_NONLINEAR_TYPE, HomeRefactor.FOLDER_NONLINEAR_TYPE),
        "folder_nonlinear_factor" to Pair(Refactor.FOLDER_NONLINEAR_DECE_FACTOR, HomeRefactor.FOLDER_NONLINEAR_DECE_FACTOR),
        "folder_nonlinear_x1" to Pair(Refactor.FOLDER_NONLINEAR_PATH_X1, HomeRefactor.FOLDER_NONLINEAR_PATH_X1),
        "folder_nonlinear_y1" to Pair(Refactor.FOLDER_NONLINEAR_PATH_Y1, HomeRefactor.FOLDER_NONLINEAR_PATH_Y1),
        "folder_nonlinear_x2" to Pair(Refactor.FOLDER_NONLINEAR_PATH_X2, HomeRefactor.FOLDER_NONLINEAR_PATH_X2),
        "folder_nonlinear_y2" to Pair(Refactor.FOLDER_NONLINEAR_PATH_Y2, HomeRefactor.FOLDER_NONLINEAR_PATH_Y2),

        "wallpaper_blur" to Pair(Refactor.WALLPAPER_BLUR, HomeRefactor.WALLPAPER_BLUR),
        "wallpaper_blur_radius" to Pair(Refactor.WALLPAPER_BLUR_RADIUS_STR, HomeRefactor.WALLPAPER_BLUR_RADIUS_STR),
        "wallpaper_dim" to Pair(Refactor.WALLPAPER_DIM, HomeRefactor.WALLPAPER_DIM),
        "wallpaper_dim_alpha" to Pair(Refactor.WALLPAPER_DIM_MAX, HomeRefactor.WALLPAPER_DIM_MAX),
        "wallpaper_nonlinear_type" to Pair(Refactor.WALLPAPER_NONLINEAR_TYPE, HomeRefactor.WALLPAPER_NONLINEAR_TYPE),
        "wallpaper_nonlinear_factor" to Pair(Refactor.WALLPAPER_NONLINEAR_DECE_FACTOR, HomeRefactor.WALLPAPER_NONLINEAR_DECE_FACTOR),
        "wallpaper_nonlinear_x1" to Pair(Refactor.WALLPAPER_NONLINEAR_PATH_X1, HomeRefactor.WALLPAPER_NONLINEAR_PATH_X1),
        "wallpaper_nonlinear_y1" to Pair(Refactor.WALLPAPER_NONLINEAR_PATH_Y1, HomeRefactor.WALLPAPER_NONLINEAR_PATH_Y1),
        "wallpaper_nonlinear_x2" to Pair(Refactor.WALLPAPER_NONLINEAR_PATH_X2, HomeRefactor.WALLPAPER_NONLINEAR_PATH_X2),
        "wallpaper_nonlinear_y2" to Pair(Refactor.WALLPAPER_NONLINEAR_PATH_Y2, HomeRefactor.WALLPAPER_NONLINEAR_PATH_Y2),

        "minus_blur" to Pair(Refactor.MINUS_BLUR, HomeRefactor.MINUS_BLUR),
        "minus_blur_radius" to Pair(Refactor.MINUS_BLUR_RADIUS_STR, HomeRefactor.MINUS_BLUR_RADIUS_STR),
        "minus_dim" to Pair(Refactor.MINUS_DIM, HomeRefactor.MINUS_DIM),
        "minus_dim_alpha" to Pair(Refactor.MINUS_DIM_MAX, HomeRefactor.MINUS_DIM_MAX),
    )

    private val blurRadiusRangeStr by lazy {
        "0-500px / 0-${500.px(activity)}dp"
    }

    override fun getTitle(): String {
        return activity.getString(R.string.page_home_refactor)
    }
    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_main),
            CategoryData(hideLine = true)
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_exclusive_refactor,
                    summaryId = R.string.home_exclusive_refactor_tips
                ),
                SwitchData(Pref.Key.MiuiHome.REFACTOR)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_general),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_all_apps_bg,
                    summaryId = R.string.home_refactor_all_apps_bg_tips
                ),
                SwitchData(Refactor.ALL_APPS_BLUR_BG)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_extra_compatibility,
                    summaryId = R.string.home_refactor_extra_compatibility_tips
                ),
                SwitchData(Refactor.EXTRA_COMPATIBILITY)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_walppaper_scale_sync,
                    summaryId = R.string.home_refactor_walppaper_scale_sync_tips
                ),
                SwitchData(Refactor.SYNC_WALLPAPER_SCALE)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_extra_fix,
                    summaryId = R.string.home_refactor_extra_fix_tips
                ),
                SwitchData(Refactor.EXTRA_FIX)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_fix_small_window,
                    summaryId = R.string.home_refactor_fix_small_window_tips
                ),
                SwitchData(Refactor.FIX_SMALL_WINDOW_ANIM)
            )
        }
        val recentsLaunchViewBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Refactor.SHOW_LAUNCH_IN_RECENTS, HomeRefactor.SHOW_LAUNCH_IN_RECENTS)
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val folderLaunchViewBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Refactor.SHOW_LAUNCH_IN_FOLDER, HomeRefactor.SHOW_LAUNCH_IN_FOLDER)
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        val minusLaunchViewBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Refactor.MINUS_OVERLAP, HomeRefactor.MINUS_OVERLAP)
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_launch),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_show,
                    summaryId = R.string.home_refactor_launch_recents_tips
                ),
                SwitchData(
                    key = "recents_launch_view".toParamKey(),
                    defValue = "recents_launch_view".toParamValue() as Boolean? == true,
                    dataBindingSend = recentsLaunchViewBinding.bindingSend
                )
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_scale,
                    summaryId = R.string.home_refactor_launch_recents_tips
                ),
                EditTextData(
                    key = "recents_launch_scale".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "recents_launch_scale".toParamValue() as Float? ?: 1.0f,
                    hintText = ("recents_launch_scale".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_launch_scale,
                            summary = "${getString(R.string.home_refactor_launch_recents_tips)}\n${getString(R.string.common_range)}: 0.6-1.2"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 0.0f) in 0.6f..1.2f
                    }
                ),
                dataBindingRecv = recentsLaunchViewBinding.binding.getRecv(0)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_show,
                    summaryId = R.string.home_refactor_launch_folder_tips
                ),
                SwitchData(
                    key = "folder_launch_view".toParamKey(),
                    defValue = "folder_launch_view".toParamValue() as Boolean? == true,
                    dataBindingSend = folderLaunchViewBinding.bindingSend
                )
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_scale,
                    summaryId = R.string.home_refactor_launch_folder_tips
                ),
                EditTextData(
                    key = "folder_launch_scale".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "folder_launch_scale".toParamValue() as Float? ?: 1.0f,
                    hintText = ("folder_launch_scale".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_launch_scale,
                            summary = "${getString(R.string.home_refactor_launch_folder_tips)}\n${getString(R.string.common_range)}: 0.6-1.2"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 0.0f) in 0.6f..1.2f
                    }
                ),
                dataBindingRecv = folderLaunchViewBinding.binding.getRecv(0)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_minus_overlap_mode,
                    summaryId = R.string.home_refactor_minus_overlap_mode_tips
                ),
                SwitchData(
                    key = "minus_overlap".toParamKey(),
                    defValue = "minus_overlap".toParamValue() as Boolean? == true,
                    dataBindingSend = minusLaunchViewBinding.bindingSend
                )
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_scale,
                    summaryId = R.string.home_refactor_launch_minus_tips
                ),
                EditTextData(
                    key = "minus_launch_scale".toParamKey(),
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = "minus_launch_scale".toParamValue() as Float? ?: 1.0f,
                    hintText = ("minus_launch_scale".toParamValue() as Float? ?: 1.0f).toString(),
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_launch_scale,
                            summary = "${getString(R.string.home_refactor_launch_minus_tips)}\n${getString(R.string.common_range)}: 0.6-1.2"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData (value as Float? ?: 0.0f) in 0.6f..1.2f
                    }
                ),
                dataBindingRecv = minusLaunchViewBinding.binding.getRecv(0)
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_show,
                    summaryId = R.string.home_refactor_launch_show_minus_tips
                ),
                SwitchData(Refactor.SHOW_LAUNCH_IN_MINUS),
                dataBindingRecv = minusLaunchViewBinding.binding.getRecv(1)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_apps),
            CategoryData()
        ) {
            addBlurLayerSetting("apps", activity.getString(R.string.home_refactor_apps_tips), true)
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_folder),
            CategoryData()
        ) {
            addBlurLayerSetting("folder", activity.getString(R.string.home_refactor_folder_tips), true)
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_wallpaper),
            CategoryData()
        ) {
            addBlurLayerSetting("wallpaper", activity.getString(R.string.home_refactor_wallpaper_tips), true)
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_minus),
            CategoryData()
        ) {
            addBlurLayerSetting("minus", activity.getString(R.string.home_refactor_minus_tips), false)
        }
    }

    private fun addBlurLayerSetting(prefix: String, descStr: String, addNonlinear: Boolean = false) {
        SwitchPreference(
            DescData(
                titleId = R.string.home_refactor_blur,
                summary = descStr
            ),
            SwitchData(
                key = "${prefix}_blur".toParamKey(),
                defValue = ("${prefix}_blur".toParamValue() as Boolean?) == true
            )
        )
        EditTextPreference(
            DescData(
                titleId = R.string.home_refactor_blur_radius,
                summaryId = R.string.home_refactor_blur_radius_tips
            ),
            EditTextData(
                key = "${prefix}_blur_radius".toParamKey(),
                valueType = EditTextData.ValueType.STRING,
                defValue = "${prefix}_blur_radius".toParamValue() as String,
                hintText = "${prefix}_blur_radius".toParamValue() as String,
                dialogData = DialogData(
                    DescData(
                        titleId = R.string.home_refactor_blur_radius,
                        summary = "${activity.getString(R.string.common_range)}: ${blurRadiusRangeStr}\nDensity: ${activity.resources.displayMetrics.density}"
                    )
                ),
                isValueValid = { value ->
                    return@EditTextData isValueValid(value as String)
                }
            )
        )
        SwitchPreference(
            DescData(
                titleId = R.string.home_refactor_dim,
                summaryId = R.string.home_refactor_dim_tips
            ),
            SwitchData(
                key = "${prefix}_dim".toParamKey(),
                defValue = ("${prefix}_dim".toParamValue() as Boolean?) == true
            )
        )
        EditTextPreference(
            DescData(
                titleId = R.string.home_refactor_dim_alpha,
                summaryId = R.string.home_refactor_dim_alpha_tips
            ),
            EditTextData(
                key = "${prefix}_dim_alpha".toParamKey(),
                valueType = EditTextData.ValueType.INT,
                defValue = "${prefix}_dim_alpha".toParamValue() as Int? ?: 0,
                hintText = ("${prefix}_dim_alpha".toParamValue() as Int? ?: 0).toString(),
                dialogData = DialogData(
                    DescData(
                        titleId = R.string.home_refactor_dim_alpha,
                        summary = "${activity.getString(R.string.common_range)}: 0-255"
                    )
                ),
                isValueValid = { value ->
                    return@EditTextData (value as Int? ?: -1) in 0..255
                }
            )
        )
        if (!addNonlinear) {
            return
        }
        TextPreference(
            DescData(titleId = R.string.home_refactor_anim_curve),
            TextData(
                valueAdapter = {
                    val animType = MIUIActivity.safeSP.getInt("${prefix}_nonlinear_type".toParamKey(), "${prefix}_nonlinear_type".toParamValue() as Int? ?: 0)
                    when (animType) {
                        1 -> activity.getString(R.string.home_refactor_nonlinear_type_dece)
                        2 -> activity.getString(R.string.home_refactor_nonlinear_type_bezier)
                        else -> activity.getString(R.string.home_refactor_nonlinear_type_non)
                    }
                }
            ),
            onClickListener = {
                showFragment("${Pages.BEZIER_CURVE_BASE}_${prefix}")
            }
        )
    }

    private fun String.toParamKey(): String {
        return paramsHashMap[this]?.first ?: throw NoSuchElementException("key=${this}")
    }

    private fun String.toParamValue(): Any {
        return paramsHashMap[this]?.second ?: throw NoSuchElementException("key=${this}")
    }

    private fun isValueValid(value: String): Boolean {
        var valid = false
        runCatching {
            valid = if (value.endsWith("dp")) {
                value.replace("dp", "").toInt().dp(MIUIActivity.context) in 0..500
            } else if (value.endsWith("px")) {
                value.replace("px", "").toInt() in 0..500
            } else {
                value.toInt() in 0..500
            }
        }.onFailure {
            valid = false
        }
        return valid
    }
}