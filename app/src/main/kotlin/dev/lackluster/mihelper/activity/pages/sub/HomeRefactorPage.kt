package dev.lackluster.mihelper.activity.pages.sub

import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("home_refactor")
class HomeRefactorPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_home_refactor)
    }
    override fun onCreate() {
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_dim,
                tipsId = R.string.home_refactor_dim_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_REFACTOR_DIM)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_dim_alpha,
                tipsId = R.string.home_refactor_dim_alpha_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_dim_alpha)
                        setMessage("${activity.getString(R.string.dialog_default_value)}: 0.2, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_BLUR_REFACTOR_DIM_ALPHA, 0.2f)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_BLUR_REFACTOR_DIM_ALPHA,
                                        getEditText().toFloat().coerceIn(0.0f, 1.0f)
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
                textId = R.string.home_refactor_nonlinear,
                tipsId = R.string.home_refactor_nonlinear_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_REFACTOR_NONLINEAR)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_nonlinear_factor,
                tipsId = R.string.home_refactor_nonlinear_factor_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_nonlinear_factor)
                        setMessage("${activity.getString(R.string.dialog_default_value)}: 1.0, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_BLUR_REFACTOR_NONLINEAR_FACTOR, 1.0f)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.1-10.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_BLUR_REFACTOR_NONLINEAR_FACTOR,
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
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_refactor_recent_show,
                tipsId = R.string.home_refactor_recent_show_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_REFACTOR_RECENT_SHOW)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.home_refactor_recent_scale,
                tipsId = R.string.home_refactor_recent_scale_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.home_refactor_recent_scale)
                        setMessage("${activity.getString(R.string.dialog_default_value)}: 0.95, ${activity.getString(R.string.dialog_current_value)}: ${
                            MIUIActivity.safeSP.getFloat(PrefKey.HOME_BLUR_REFACTOR_RECENT_SCALE, 0.95f)
                        }")
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 0.01-1.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.HOME_BLUR_REFACTOR_RECENT_SCALE,
                                        getEditText().toFloat().coerceIn(0.01f, 1.0f)
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
    }
}