package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.MIUIActivity.Companion.safeSP
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("scope_android")
class AndroidPage :BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_android)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_android_ui)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.android_dark_for_all,
                tipsId = R.string.android_dark_for_all_tips
            ),
            SwitchV(PrefKey.ANDROID_DARK_MODE_FOR_ALL)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.android_wallpaper_scale,
                tipsId = R.string.android_wallpaper_scale_tips,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.android_wallpaper_scale)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: 1.2, ${activity.getString(R.string.dialog_current_value)}: ${
                                safeSP.getFloat(PrefKey.ANDROID_WALLPAPER_SCALE_RATIO, 1.2f)
                            }"
                        )
                        setEditText("", "${activity.getString(R.string.dialog_value_range)}: 1.0-2.0")
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    safeSP.putAny(
                                        PrefKey.ANDROID_WALLPAPER_SCALE_RATIO,
                                        getEditText().toFloat()
                                    )
                                }.onFailure {
                                    makeText(activity, activity.getString(R.string.invalid_input), LENGTH_LONG)
                                        .show()
                                }
                            }
                            dismiss()
                        }
                    }.show()
                })
        )
        Line()
        TitleText(textId = R.string.ui_title_android_behavior)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.android_freeform_restriction,
                tipsId = R.string.android_freeform_restriction_tips
            ),
            SwitchV(PrefKey.ANDROID_FREEFORM_RESTRICTION)
        )
        val bindingDisableFixedOrientation =
            GetDataBinding({
                safeSP.getBoolean(PrefKey.ANDROID_NO_FIXED_ORIENTATION, false)
            }) { view, flags, data ->
                when (flags) {
                    1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                }
            }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.android_disable_fixed_orientation,
                tipsId = R.string.android_disable_fixed_orientation_tips
            ),
            SwitchV(
                key = PrefKey.ANDROID_NO_FIXED_ORIENTATION,
                dataBindingSend = bindingDisableFixedOrientation.bindingSend
            )
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.android_disable_fixed_orientation_scope,
                tipsId = R.string.android_disable_fixed_orientation_scope_tips
            ) {
                showFragment("disable_fixed_orientation")
            },
            dataBindingRecv = bindingDisableFixedOrientation.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_scope_package_installer)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.package_ad_block),
            SwitchV(PrefKey.PACKAGE_AD_BLOCK)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.package_safe_mode_tip),
            SwitchV(PrefKey.PACKAGE_SAFE_MODE_TIP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.package_skip_risk_check),
            SwitchV(PrefKey.PACKAGE_SKIP_RISK_CHECK)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.package_no_count_check),
            SwitchV(PrefKey.PACKAGE_NO_COUNT_CHECK)
        )
    }

}