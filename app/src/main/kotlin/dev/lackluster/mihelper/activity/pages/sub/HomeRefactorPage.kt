package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefDefValue
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("home_refactor")
class HomeRefactorPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_home_refactor)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_home_refactor_main)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_exclusive_refactor,
                tipsId = R.string.home_exclusive_refactor_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_REFACTOR)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_extra_compatibility,
                tipsId = R.string.home_refactor_extra_compatibility_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_EXTRA_COMPATIBILITY)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_walppaper_scale_sync,
                tipsId = R.string.home_refactor_walppaper_scale_sync_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_WALLPAPER_SCALE_SYNC)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_extra_fix,
                tipsId = R.string.home_refactor_extra_fix_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_EXTRA_FIX)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_fix_small_window,
                tipsId = R.string.home_refactor_fix_small_window_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_FIX_SMALL_WINDOW)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_apps)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_blur,
                tipsId = R.string.home_refactor_apps_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_APPS_BLUR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_blur_radius,
                tipsId = R.string.home_refactor_blur_radius_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_blur_radius)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_BLUR_RADIUS}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_APPS_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_APPS_BLUR_RADIUS)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-200")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_BLUR_RADIUS,
                                        getEditText().toInt().coerceIn(0, 200)
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
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_dim,
                tipsId = R.string.home_refactor_dim_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_APPS_DIM)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_dim_alpha,
                tipsId = R.string.home_refactor_dim_alpha_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_dim_alpha)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_DIM_MAX}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_APPS_DIM_MAX, PrefDefValue.HOME_REFACTOR_APPS_DIM_MAX)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-255")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_DIM_MAX,
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
        val nonlinearType: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.home_refactor_nonlinear_type_non)
            it[1] = getString(R.string.home_refactor_nonlinear_type_dece)
            it[2] = getString(R.string.home_refactor_nonlinear_type_path)
        }
        val appsNonlinearTypeBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_TYPE, 0)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if ((data as Int) == 0) View.VISIBLE else View.GONE
                1 -> view.visibility = if ((data as Int) == 1) View.VISIBLE else View.GONE
                2 -> view.visibility = if ((data as Int) == 2) View.VISIBLE else View.GONE
            }
        }
        TextWithSpinner(
            TextV(textId = R.string.home_refactor_nonlinear),
            SpinnerV(
                nonlinearType[MIUIActivity.safeSP.getInt(
                    PrefKey.HOME_REFACTOR_APPS_NONLINEAR_TYPE,
                    0
                )].toString()
            ) {
                add(nonlinearType[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_TYPE, 0)
                    appsNonlinearTypeBinding.bindingSend.send(0)
                }
                add(nonlinearType[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_TYPE, 1)
                    appsNonlinearTypeBinding.bindingSend.send(1)
                }
                add(nonlinearType[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_TYPE, 2)
                    appsNonlinearTypeBinding.bindingSend.send(2)
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
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_DECE_FACTOR}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_DECE_FACTOR, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_DECE_FACTOR)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.1-10.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_NONLINEAR_DECE_FACTOR,
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
            dataBindingRecv = appsNonlinearTypeBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_x1,
                tipsId = R.string.home_refactor_nonlinear_path_x1_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_x1)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_X1}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_X1, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_X1)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_X1,
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
            dataBindingRecv = appsNonlinearTypeBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_y1,
                tipsId = R.string.home_refactor_nonlinear_path_y1_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_y1)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y1}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y1, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y1)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y1,
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
            dataBindingRecv = appsNonlinearTypeBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_x2,
                tipsId = R.string.home_refactor_nonlinear_path_x2_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_x2)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_X2}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_X2, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_X2)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_X2,
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
            dataBindingRecv = appsNonlinearTypeBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_y2,
                tipsId = R.string.home_refactor_nonlinear_path_y2_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_y2)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y2}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y2, PrefDefValue.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y2)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_NONLINEAR_PATH_Y2,
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
            dataBindingRecv = appsNonlinearTypeBinding.binding.getRecv(2)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_wallpaper)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_blur,
                tipsId = R.string.home_refactor_wallpaper_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_WALL_BLUR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_blur_radius,
                tipsId = R.string.home_refactor_blur_radius_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_blur_radius)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_BLUR_RADIUS}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_WALL_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_WALL_BLUR_RADIUS)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-200")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_BLUR_RADIUS,
                                        getEditText().toInt().coerceIn(0, 200)
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
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_dim,
                tipsId = R.string.home_refactor_dim_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_WALL_DIM)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_dim_alpha,
                tipsId = R.string.home_refactor_dim_alpha_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_dim_alpha)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_DIM_MAX}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_WALL_DIM_MAX, PrefDefValue.HOME_REFACTOR_WALL_DIM_MAX)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-255")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_DIM_MAX,
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
        val wallNonlinearTypeBinding = GetDataBinding({
            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_TYPE, 0)
        }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if ((data as Int) == 0) View.VISIBLE else View.GONE
                1 -> view.visibility = if ((data as Int) == 1) View.VISIBLE else View.GONE
                2 -> view.visibility = if ((data as Int) == 2) View.VISIBLE else View.GONE
            }
        }
        TextWithSpinner(
            TextV(textId = R.string.home_refactor_nonlinear),
            SpinnerV(
                nonlinearType[MIUIActivity.safeSP.getInt(
                    PrefKey.HOME_REFACTOR_WALL_NONLINEAR_TYPE,
                    0
                )].toString()
            ) {
                add(nonlinearType[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_TYPE, 0)
                    wallNonlinearTypeBinding.bindingSend.send(0)
                }
                add(nonlinearType[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_TYPE, 1)
                    wallNonlinearTypeBinding.bindingSend.send(1)
                }
                add(nonlinearType[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_TYPE, 2)
                    wallNonlinearTypeBinding.bindingSend.send(2)
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
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_DECE_FACTOR}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_DECE_FACTOR, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_DECE_FACTOR)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.1-10.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_NONLINEAR_DECE_FACTOR,
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
            dataBindingRecv = wallNonlinearTypeBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_x1,
                tipsId = R.string.home_refactor_nonlinear_path_x1_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_x1)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_X1}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_X1, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_X1)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_X1,
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
            dataBindingRecv = wallNonlinearTypeBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_y1,
                tipsId = R.string.home_refactor_nonlinear_path_y1_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_y1)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y1}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y1, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y1)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y1,
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
            dataBindingRecv = wallNonlinearTypeBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_x2,
                tipsId = R.string.home_refactor_nonlinear_path_x2_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_x2)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_X2}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_X2, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_X2)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_X2,
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
            dataBindingRecv = wallNonlinearTypeBinding.binding.getRecv(2)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_path_y2,
                tipsId = R.string.home_refactor_nonlinear_path_y2_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_path_y2)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y2}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y2, PrefDefValue.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y2)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_NONLINEAR_PATH_Y2,
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
            dataBindingRecv = wallNonlinearTypeBinding.binding.getRecv(2)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_launch)
        TitleText(textId = R.string.home_refactor_launch_tips)
        if (!Device.isPad) {
            TextSummaryWithSwitch(
                TextSummaryV(
                    textId = R.string.home_refactor_launch_show,
                    tipsId = R.string.home_refactor_launch_show_tips
                ),
                SwitchV(PrefKey.HOME_REFACTOR_LAUNCH_SHOW)
            )
        }
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_launch_scale,
                tipsId = R.string.home_refactor_launch_scale_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_launch_scale)
                        setMessage("${activity.getString(R.string.common_default)}: 0.95, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_LAUNCH_SCALE, 0.95f)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.6-1.2")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_LAUNCH_SCALE,
                                        getEditText().toFloat().coerceIn(0.6f, 1.2f)
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
//        TextSummaryWithSwitch(
//            TextSummaryV(
//                textId = R.string.home_refactor_launch_scale_nonlinear
//            ),
//            SwitchV(PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR)
//        )
//        TextSummaryWithArrow(
//            TextSummaryV(
//                textId = R.string.home_refactor_launch_scale_nonlinear_factor,
//                tipsId = R.string.home_refactor_launch_scale_nonlinear_factor_tips,
//                onClickListener = {
//                    MIUIDialog(activity) {
//                        setTitle(R.string.home_refactor_launch_scale_nonlinear_factor)
//                        setMessage("${activity.getString(R.string.dialog_default_value)}: 1.0, ${activity.getString(R.string.dialog_current_value)}: ${
//                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR_FACTOR, 1.0f)
//                        }")
//                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.1-10.0")
//                        setLButton(textId = R.string.button_cancel) {
//                            dismiss()
//                        }
//                        setRButton(textId = R.string.button_ok) {
//                            if (getEditText().isNotEmpty()) {
//                                runCatching {
//                                    MIUIActivity.safeSP.putAny(
//                                        PrefKey.HOME_REFACTOR_LAUNCH_NONLINEAR_FACTOR,
//                                        getEditText().toFloat().coerceIn(0.1f, 10.0f)
//                                    )
//                                }.onFailure {
//                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
//                                }
//                            }
//                            dismiss()
//                        }
//                    }.show()
//                })
//        )
        Line()
        TitleText(textId = R.string.ui_title_home_refactor_minus)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_blur,
                tipsId = R.string.home_refactor_minus_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_MINUS_BLUR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_blur_radius,
                tipsId = R.string.home_refactor_blur_radius_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_blur_radius)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_MINUS_BLUR_RADIUS}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_MINUS_BLUR_RADIUS, PrefDefValue.HOME_REFACTOR_MINUS_BLUR_RADIUS)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-200")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_MINUS_BLUR_RADIUS,
                                        getEditText().toInt().coerceIn(0, 200)
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
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_dim,
                tipsId = R.string.home_refactor_dim_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_MINUS_DIM)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_dim_alpha,
                tipsId = R.string.home_refactor_dim_alpha_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_dim_alpha)
                        setMessage("${activity.getString(R.string.common_default)}: ${PrefDefValue.HOME_REFACTOR_MINUS_DIM_MAX}, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getInt(PrefKey.HOME_REFACTOR_MINUS_DIM_MAX, PrefDefValue.HOME_REFACTOR_MINUS_DIM_MAX)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-255")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_MINUS_DIM_MAX,
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
        val overlapBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.HOME_REFACTOR_MINUS_OVERLAP, false)
        }) { view, reverse, data ->
            when (reverse) {
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_minus_overlap_mode,
                tipsId = R.string.home_refactor_minus_overlap_mode_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_MINUS_OVERLAP, dataBindingSend = overlapBinding.bindingSend)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_launch_show,
                tipsId = R.string.home_refactor_launch_show_minus_tips
            ),
            SwitchV(PrefKey.HOME_REFACTOR_MINUS_LAUNCH),
            dataBindingRecv = overlapBinding.binding.getRecv(1)
        )
    }
}