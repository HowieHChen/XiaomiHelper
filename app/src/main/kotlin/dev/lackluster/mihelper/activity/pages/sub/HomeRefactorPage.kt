package dev.lackluster.mihelper.activity.pages.sub

import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
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
                textId = R.string.home_behavior_refactor,
                tipsId = R.string.home_behavior_refactor_tips
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
                        setMessage("${activity.getString(R.string.dialog_default_value)}: ${PrefDefValue.HOME_REFACTOR_APPS_BLUR_RADIUS}, ${activity.getString(R.string.dialog_current_value)}: ${
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
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
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
                        setMessage("${activity.getString(R.string.dialog_default_value)}: ${PrefDefValue.HOME_REFACTOR_APPS_DIM_MAX}, ${activity.getString(R.string.dialog_current_value)}: ${
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
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                })
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear
            ),
            SwitchV(PrefKey.HOME_REFACTOR_APPS_NONLINEAR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_factor,
                tipsId = R.string.home_refactor_nonlinear_factor_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_factor)
                        setMessage("${activity.getString(R.string.dialog_default_value)}: 1.0, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_APPS_NONLINEAR_FACTOR, 1.0f)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.1-10.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_APPS_NONLINEAR_FACTOR,
                                        getEditText().toFloat().coerceIn(0.1f, 10.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                })
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
                        setMessage("${activity.getString(R.string.dialog_default_value)}: ${PrefDefValue.HOME_REFACTOR_WALL_BLUR_RADIUS}, ${activity.getString(R.string.dialog_current_value)}: ${
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
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
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
                        setMessage("${activity.getString(R.string.dialog_default_value)}: ${PrefDefValue.HOME_REFACTOR_WALL_DIM_MAX}, ${activity.getString(R.string.dialog_current_value)}: ${
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
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                })
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear
            ),
            SwitchV(PrefKey.HOME_REFACTOR_WALL_NONLINEAR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_factor,
                tipsId = R.string.home_refactor_nonlinear_factor_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_factor)
                        setMessage("${activity.getString(R.string.dialog_default_value)}: 1.0, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_REFACTOR_WALL_NONLINEAR_FACTOR, 1.0f)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.1-10.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_REFACTOR_WALL_NONLINEAR_FACTOR,
                                        getEditText().toFloat().coerceIn(0.1f, 10.0f)
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                })
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
                        setMessage("${activity.getString(R.string.dialog_default_value)}: 0.95, ${activity.getString(R.string.dialog_current_value)}: ${
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
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
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
    }
}