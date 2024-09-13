package dev.lackluster.mihelper.activity.pages.sub

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.data.CategoryData
import cn.fkj233.ui.activity.data.DescData
import cn.fkj233.ui.activity.data.DialogData
import cn.fkj233.ui.activity.data.DropDownData
import cn.fkj233.ui.activity.data.EditTextData
import cn.fkj233.ui.activity.data.SwitchData
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pages
import dev.lackluster.mihelper.data.Pref.Key.MiuiHome.Refactor
import dev.lackluster.mihelper.data.Pref.DefValue.HomeRefactor
import dev.lackluster.mihelper.utils.Prefs.isPixelStrValid
import dev.lackluster.mihelper.utils.factory.px

@BMPage(Pages.MINUS_BLUR, hideMenu = false)
class MinusBlurPage : BasePage() {
    private val blurRadiusRangeStr by lazy {
        "0-500px / 0-${500.px(activity)}dp"
    }
    override fun getTitle(): String {
        return activity.getString(R.string.page_minus_blur)
    }
    override fun onCreate() {
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_launch),
            CategoryData(hideLine = true)
        ) {
            val minusLaunchViewBinding = GetDataBinding({
                MIUIActivity.safeSP.getInt(Refactor.MINUS_MODE, HomeRefactor.MINUS_MODE)
            }) { view, flag, data ->
                when (flag) {
                    2 -> view.visibility = if (data as Int == 2) View.VISIBLE else View.GONE
                }
            }
            DropDownPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_show,
                    summaryId = R.string.home_refactor_launch_minus_tips
                ),
                DropDownData(
                    key = Refactor.MINUS_MODE,
                    defValue = HomeRefactor.MINUS_MODE,
                    entries = arrayOf(
                        DropDownData.SpinnerItemData(getString(R.string.home_refactor_launch_minus_mode_def), 0),
                        DropDownData.SpinnerItemData(getString(R.string.home_refactor_launch_minus_mode_visible), 1),
                        DropDownData.SpinnerItemData(getString(R.string.home_refactor_launch_minus_mode_overlap), 2),
                    ),
                    dataBindingSend = minusLaunchViewBinding.bindingSend
                )
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_launch_scale,
                    summaryId = R.string.home_refactor_launch_minus_tips
                ),
                EditTextData(
                    key = Refactor.SHOW_LAUNCH_IN_MINUS_SCALE,
                    valueType = EditTextData.ValueType.FLOAT,
                    defValue = HomeRefactor.SHOW_LAUNCH_IN_MINUS_SCALE,
                    hintText = HomeRefactor.SHOW_LAUNCH_IN_MINUS_SCALE.toString(),
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
                dataBindingRecv = minusLaunchViewBinding.binding.getRecv(2)
            )
        }
        PreferenceCategory(
            DescData(titleId = R.string.ui_title_home_refactor_minus),
            CategoryData()
        ) {
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_blur,
                    summaryId = R.string.home_refactor_minus_tips
                ),
                SwitchData(
                    key = Refactor.MINUS_BLUR,
                    defValue = HomeRefactor.MINUS_BLUR
                )
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_blur_radius,
                    summaryId = R.string.home_refactor_blur_radius_tips
                ),
                EditTextData(
                    key = Refactor.MINUS_BLUR_RADIUS_STR,
                    valueType = EditTextData.ValueType.STRING,
                    defValue = HomeRefactor.MINUS_BLUR_RADIUS_STR,
                    hintText = HomeRefactor.MINUS_BLUR_RADIUS_STR,
                    dialogData = DialogData(
                        DescData(
                            titleId = R.string.home_refactor_blur_radius,
                            summary = "${activity.getString(R.string.common_range)}: ${blurRadiusRangeStr}\nDensity: ${activity.resources.displayMetrics.density}"
                        )
                    ),
                    isValueValid = { value ->
                        return@EditTextData isPixelStrValid(value as String)
                    }
                )
            )
            SwitchPreference(
                DescData(
                    titleId = R.string.home_refactor_dim,
                    summaryId = R.string.home_refactor_dim_tips
                ),
                SwitchData(
                    key = Refactor.MINUS_DIM,
                    defValue = HomeRefactor.MINUS_DIM
                )
            )
            EditTextPreference(
                DescData(
                    titleId = R.string.home_refactor_dim_alpha,
                    summaryId = R.string.home_refactor_dim_alpha_tips
                ),
                EditTextData(
                    key = Refactor.MINUS_DIM_MAX,
                    valueType = EditTextData.ValueType.INT,
                    defValue = HomeRefactor.MINUS_DIM_MAX,
                    hintText = HomeRefactor.MINUS_DIM_MAX.toString(),
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
        }
    }
}