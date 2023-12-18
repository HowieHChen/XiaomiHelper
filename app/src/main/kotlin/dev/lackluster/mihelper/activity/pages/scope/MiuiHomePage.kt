package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SeekBarWithTextV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device

@BMPage("scope_miui_home", hideMenu = false)
class MiuiHomePage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_miui_home)
    }
    override fun onCreate() {
        val isPadBinding = GetDataBinding({
            Device.isPad
        }) { view, reverse, data ->
            when (reverse) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
            }
        }
        TitleText(textId = R.string.ui_title_home_behavior)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_always_show_clock,
            ),
            SwitchV(PrefKey.HOME_ALWAYS_SHOW_TIME)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_double_tap,
            ),
            SwitchV(PrefKey.HOME_DOUBLE_TAP_TO_SLEEP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_enable4pad,
            ),
            SwitchV(PrefKey.HOME_PAD_ALL_FEATURE),
            dataBindingRecv = isPadBinding.binding.getRecv(0)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_fake_premium,
                tipsId = R.string.home_behavior_fake_premium_tips
            ),
            SwitchV(PrefKey.HOME_FAKE_PREMIUM)
        )
        val blurBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.HOME_BLUR_ALL, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_blur_advance,
                tipsId = R.string.home_behavior_blur_advance_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_ADVANCE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_all_blur,
                tipsId = R.string.home_behavior_all_blur_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_ALL, dataBindingSend = blurBinding.bindingSend)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_behavior_all_blur_enhance,
                tipsId = R.string.home_behavior_all_blur_enhance_tips
            ),
            SwitchV(PrefKey.HOME_BLUR_ENHANCE),
            dataBindingRecv = blurBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_remove_report,
            ),
            SwitchV(PrefKey.HOME_REMOVE_REPORT)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_anim)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_anim_unlock,
                tipsId = R.string.home_anim_unlock_tips
            ),
            SwitchV(PrefKey.HOME_ANIM_UNLOCK)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_icon)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_unblock_google,
            ),
            SwitchV(PrefKey.HOME_ICON_UNBLOCK_GOOGLE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_corner4large,
                tipsId = R.string.home_icon_corner4large_tips
            ),
            SwitchV(PrefKey.HOME_ICON_CORNER4LARGE),
            dataBindingRecv = isPadBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_icon_perfect_icon,
                tipsId = R.string.home_icon_perfect_icon_tips
            ),
            SwitchV(PrefKey.HOME_ICON_PERFECT_ICON)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_recent)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_pad_show_memory,
            ),
            SwitchV(PrefKey.HOME_PAD_SHOW_MEMORY),
            dataBindingRecv = isPadBinding.binding.getRecv(0)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_show_real_memory,
            ),
            SwitchV(PrefKey.HOME_SHOW_REAL_MEMORY)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_wallpaper_darken,
                tipsId = R.string.home_recent_wallpaper_darken_tips
            ),
            SwitchV(PrefKey.HOME_WALLPAPER_DARKEN)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_recent_dismiss_anim,
            ),
            SwitchV(PrefKey.HOME_RECENT_ANIM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_disable_fake_navbar,
                tipsId = R.string.home_disable_fake_navbar_tips
            ),
            SwitchV(PrefKey.HOME_DISABLE_FAKE_NAVBAR)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_widget)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_widget_anim,
            ),
            SwitchV(PrefKey.HOME_WIDGET_ANIM)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_widget_resizable,
            ),
            SwitchV(PrefKey.HOME_WIDGET_RESIZABLE)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_personal_asist)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_minus_restore_setting,
            ),
            SwitchV(PrefKey.HOME_MINUS_RESTORE_SETTING)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_minus_fold_style,
            ),
            SwitchV(PrefKey.HOME_MINUS_FOLD_STYLE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.personal_assist_blur,
            ),
            SwitchV(PrefKey.PERSON_ASSIST_BLUR)
        )
        Line()
        TitleText(textId = R.string.ui_title_home_folder)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_folder_adapt_icon_size,
                tipsId = R.string.home_folder_adapt_icon_size_tips
            ),
            SwitchV(PrefKey.HOME_FOLDER_ADAPT_SIZE),
            dataBindingRecv = isPadBinding.binding.getRecv(1)
        )
        val folderColumnsDef = if (Device.isPad) 4 else 3
        TextWithSeekBar(
            TextV(textId = R.string.home_folder_layout_size),
            SeekBarWithTextV(PrefKey.HOME_FOLDER_COLUMNS, 2, 7, folderColumnsDef)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.home_folder_layout_fix,
            ),
            SwitchV(PrefKey.HOME_FOLDER_NO_PADDING)
        )
    }
}