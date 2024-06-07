package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.utils.factory.dp
import dev.lackluster.mihelper.utils.factory.px

@BMPage("page_home_refactor")
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

    private val nonlinearType: HashMap<Int, String> by lazy {
        hashMapOf<Int, String>().also {
            it[0] = getString(R.string.home_refactor_nonlinear_type_non)
            it[1] = getString(R.string.home_refactor_nonlinear_type_dece)
            it[2] = getString(R.string.home_refactor_nonlinear_type_path)
        }
    }
    private val blurRadiusRangeStr by lazy {
        "0-500px / 0-${500.px(activity)}dp"
    }

    override fun getTitle(): String {
        return activity.getString(R.string.page_home_refactor)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_home_refactor_main)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_exclusive_refactor,
                tipsId = R.string.home_exclusive_refactor_tips
            ),
            SwitchV(Pref.Key.MiuiHome.REFACTOR)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_general)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_all_apps_bg,
                tipsId = R.string.home_refactor_all_apps_bg_tips
            ),
            SwitchV(Refactor.ALL_APPS_BLUR_BG)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_extra_compatibility,
                tipsId = R.string.home_refactor_extra_compatibility_tips
            ),
            SwitchV(Refactor.EXTRA_COMPATIBILITY)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_walppaper_scale_sync,
                tipsId = R.string.home_refactor_walppaper_scale_sync_tips
            ),
            SwitchV(Refactor.SYNC_WALLPAPER_SCALE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_extra_fix,
                tipsId = R.string.home_refactor_extra_fix_tips
            ),
            SwitchV(Refactor.EXTRA_FIX)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_fix_small_window,
                tipsId = R.string.home_refactor_fix_small_window_tips
            ),
            SwitchV(Refactor.FIX_SMALL_WINDOW_ANIM)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_launch)
        val recentsLaunchViewBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Refactor.SHOW_LAUNCH_IN_RECENTS, HomeRefactor.SHOW_LAUNCH_IN_RECENTS)
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        addLaunchViewSetting("recents", getString(R.string.home_refactor_launch_recents_tips), recentsLaunchViewBinding)
        val folderLaunchViewBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Refactor.SHOW_LAUNCH_IN_FOLDER, HomeRefactor.SHOW_LAUNCH_IN_FOLDER)
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        addLaunchViewSetting("folder", getString(R.string.home_refactor_launch_folder_tips), folderLaunchViewBinding)
        val minusLaunchViewBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(Refactor.MINUS_OVERLAP, HomeRefactor.MINUS_OVERLAP)
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_minus_overlap_mode,
                tipsId = R.string.home_refactor_minus_overlap_mode_tips
            ),
            SwitchV(
                key = "minus_overlap".toParamKey(),
                defValue = "minus_overlap".toParamValue() as Boolean? == true,
                dataBindingSend = minusLaunchViewBinding.bindingSend
            )
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_launch_scale,
                tipsId = R.string.home_refactor_launch_minus_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_launch_scale)
                        setMessage(generateMessage("minus_launch_scale", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.6-1.2")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "minus_launch_scale".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.6f, 1.2f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = minusLaunchViewBinding.binding.getRecv(0)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_launch_show,
                tipsId = R.string.home_refactor_launch_show_minus_tips
            ),
            SwitchV(Refactor.SHOW_LAUNCH_IN_MINUS),
            dataBindingRecv = minusLaunchViewBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_apps)
        val appsNonlinearTypeBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(Refactor.APPS_NONLINEAR_TYPE, HomeRefactor.APPS_NONLINEAR_TYPE)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if ((data as Int) == 0) View.VISIBLE else View.GONE
                1 -> view.visibility = if ((data as Int) == 1) View.VISIBLE else View.GONE
                2 -> view.visibility = if ((data as Int) == 2) View.VISIBLE else View.GONE
            }
        }
        addBlurLayerSetting("apps", getString(R.string.home_refactor_apps_tips), appsNonlinearTypeBinding, true)
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_folder)
        val folderNonlinearTypeBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(Refactor.FOLDER_NONLINEAR_TYPE, HomeRefactor.FOLDER_NONLINEAR_TYPE)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if ((data as Int) == 0) View.VISIBLE else View.GONE
                1 -> view.visibility = if ((data as Int) == 1) View.VISIBLE else View.GONE
                2 -> view.visibility = if ((data as Int) == 2) View.VISIBLE else View.GONE
            }
        }
        addBlurLayerSetting("folder", getString(R.string.home_refactor_folder_tips), folderNonlinearTypeBinding, true)
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_wallpaper)
        val wallNonlinearTypeBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(Refactor.WALLPAPER_NONLINEAR_TYPE, HomeRefactor.WALLPAPER_NONLINEAR_TYPE)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if ((data as Int) == 0) View.VISIBLE else View.GONE
                1 -> view.visibility = if ((data as Int) == 1) View.VISIBLE else View.GONE
                2 -> view.visibility = if ((data as Int) == 2) View.VISIBLE else View.GONE
            }
        }
        addBlurLayerSetting("wallpaper", getString(R.string.home_refactor_wallpaper_tips), wallNonlinearTypeBinding, true)
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_minus)
        addBlurLayerSetting("minus", getString(R.string.home_refactor_minus_tips), null, false)
    }

    private fun addBlurLayerSetting(prefix: String, descStr: String, dataBinding: DataBinding.BindingData?, addNonlinear: Boolean = false) {
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_blur,
                tips = descStr
            ),
            paramSwitch("${prefix}_blur")
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_blur_radius,
                tipsId = R.string.home_refactor_blur_radius_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_blur_radius)
                        setMessage(generateMessage("${prefix}_blur_radius", 0) + "\nDensity: ${activity.resources.displayMetrics.density}")
                        setEditText("", "${activity.getString(R.string.common_range)}: $blurRadiusRangeStr")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                if (!setPixelByStr("${prefix}_blur_radius".toParamKey(), getEditText())) {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            )
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_dim,
                tipsId = R.string.home_refactor_dim_tips
            ),
            paramSwitch("${prefix}_dim")
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_dim_alpha,
                tipsId = R.string.home_refactor_dim_alpha_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_dim_alpha)
                        setMessage(generateMessage("${prefix}_dim_alpha", 1))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0-255")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_dim_alpha".toParamKey(),
                                        getEditText().toInt().coerceIn(0, 255)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                })
        )
        if (dataBinding == null || !addNonlinear) {
            return
        }
        TextWithSpinner(
            TextV(textId = R.string.home_refactor_nonlinear),
            SpinnerV(
                nonlinearType[MIUIActivity.safeSP.getInt(
                    "${prefix}_nonlinear_type".toParamKey(),
                    "${prefix}_nonlinear_type".toParamValue() as Int? ?: 0
                )].toString()
            ) {
                add(nonlinearType[0].toString()) {
                    MIUIActivity.safeSP.putAny("${prefix}_nonlinear_type".toParamKey(), 0)
                    dataBinding.bindingSend.send(0)
                }
                add(nonlinearType[1].toString()) {
                    MIUIActivity.safeSP.putAny("${prefix}_nonlinear_type".toParamKey(), 1)
                    dataBinding.bindingSend.send(1)
                }
                add(nonlinearType[2].toString()) {
                    MIUIActivity.safeSP.putAny("${prefix}_nonlinear_type".toParamKey(), 2)
                    dataBinding.bindingSend.send(2)
                }
            }
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_factor,
                tipsId = R.string.home_refactor_nonlinear_factor_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_factor)
                        setMessage(generateMessage("${prefix}_nonlinear_factor", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.1-10.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_nonlinear_factor".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.1f, 10.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = dataBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_x1,
                tipsId = R.string.home_refactor_nonlinear_path_x1_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_x1)
                        setMessage(generateMessage("${prefix}_nonlinear_x1", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_nonlinear_x1".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.0f, 1.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = dataBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_y1,
                tipsId = R.string.home_refactor_nonlinear_path_y1_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_y1)
                        setMessage(generateMessage("${prefix}_nonlinear_y1", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_nonlinear_y1".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.0f, 1.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = dataBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_x2,
                tipsId = R.string.home_refactor_nonlinear_path_x2_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_x2)
                        setMessage(generateMessage("${prefix}_nonlinear_x2", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_nonlinear_x2".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.0f, 1.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = dataBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_y2,
                tipsId = R.string.home_refactor_nonlinear_path_y2_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_y2)
                        setMessage(generateMessage("${prefix}_nonlinear_y2", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_nonlinear_y2".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.0f, 1.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = dataBinding.binding.getRecv(2)
        )
    }

    private fun addLaunchViewSetting(prefix: String, descStr: String, dataBinding: DataBinding.BindingData) {
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_launch_show,
                tips = descStr
            ),
            SwitchV(
                key = "${prefix}_launch_view".toParamKey(),
                defValue = "${prefix}_launch_view".toParamValue() as Boolean? == true,
                dataBindingSend = dataBinding.bindingSend
            )
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_launch_scale,
                tips = descStr,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_launch_scale)
                        setMessage(generateMessage("${prefix}_launch_scale", 2))
                        setEditText("", "${activity.getString(R.string.common_range)}: 0.6-1.2")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        "${prefix}_launch_scale".toParamKey(),
                                        getEditText().toFloat().coerceIn(0.6f, 1.2f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.common_invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                }
            ),
            dataBindingRecv = dataBinding.binding.getRecv(0)
        )
    }

    // dataType: 0 -> String, 1 -> Int, 2 -> Float
    private fun generateMessage(key: String, dataType: Int): String {
        return when (dataType) {
            1 -> {
                "${activity.getString(R.string.common_default)}: ${key.toParamValue()}, ${activity.getString(R.string.common_current)}: ${
                    MIUIActivity.safeSP.getInt(key.toParamKey(), key.toParamValue() as Int? ?: 0)
                }"
            }
            2 -> {
                "${activity.getString(R.string.common_default)}: ${key.toParamValue()}, ${activity.getString(R.string.common_current)}: ${
                    MIUIActivity.safeSP.getFloat(key.toParamKey(), key.toParamValue() as Float? ?: 0.0f)
                }"
            }
            else -> {
                "${activity.getString(R.string.common_default)}: ${key.toParamValue()}, ${activity.getString(R.string.common_current)}: ${
                    MIUIActivity.safeSP.getString(key.toParamKey(), key.toParamValue().toString())
                }"
            }
        }
    }
    private fun paramSwitch(key: String): SwitchV {
        return SwitchV(key.toParamKey(), (key.toParamValue() as Boolean?) == true)
    }

    private fun String.toParamKey(): String {
        return paramsHashMap[this]?.first ?: throw NoSuchElementException("key=${this}")
    }

    private fun String.toParamValue(): Any {
        return paramsHashMap[this]?.second ?: throw NoSuchElementException("key=${this}")
    }

    private fun getPixelByStr(key: String, defStr: String = "0px"): Int {
        val value = MIUIActivity.safeSP.getString(key, defStr)
        runCatching {
            if (value.endsWith("dp")) {
                return value.replace("dp", "").toInt().dp(MIUIActivity.context)
            } else {
                return value.replace("px", "").toInt()
            }
        }
        return 0
    }

    private fun setPixelByStr(key: String, value: String): Boolean {
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
        if (valid) {
            MIUIActivity.safeSP.putAny(key, value)
        }
        return valid
    }
}