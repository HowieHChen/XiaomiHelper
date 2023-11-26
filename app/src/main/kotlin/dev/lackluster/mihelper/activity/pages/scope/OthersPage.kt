package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("scope_others")
class OthersPage : BasePage() {
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_others)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_scope_browser)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.browser_debug_mode, tipsId = R.string.browser_debug_mode_tips),
            SwitchV(PrefKey.BROWSER_DEBUG)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.browser_disable_update),
            SwitchV(PrefKey.BROWSER_NO_UPDATE)
        )
        Line()
        TitleText(textId = R.string.ui_scope_settings)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.settings_show_google),
            SwitchV(PrefKey.SETTINGS_SHOW_GOOGLE)
        )
        Line()
        TitleText(textId = R.string.ui_scope_taplus)
        val taplusUseBrowserBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(
                PrefKey.TAPLUS_USE_BROWSER, false
            )
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        val customTaplusSearch: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.taplus_search_engine_default)
            it[1] = getString(R.string.taplus_search_engine_baidu)
            it[2] = getString(R.string.taplus_search_engine_sogou)
            it[3] = getString(R.string.taplus_search_engine_bing)
            it[4] = getString(R.string.taplus_search_engine_google)
            it[5] = getString(R.string.taplus_search_engine_custom)
        }
        val taplusSearchBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.TAPLUS_USE_BROWSER, false)
                    && (MIUIActivity.safeSP.getInt(PrefKey.TAPLUS_SEARCH_ENGINE, 0) == 5)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.taplus_use_browser),
            SwitchV(PrefKey.TAPLUS_USE_BROWSER) {
                taplusUseBrowserBinding.binding.Send().send(it)
                taplusSearchBinding.binding.Send().send(it
                        && (MIUIActivity.safeSP.getInt(PrefKey.TAPLUS_SEARCH_ENGINE, 0) == 5))
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.taplus_search_engine), SpinnerV(
            customTaplusSearch[MIUIActivity.safeSP.getInt(
                PrefKey.TAPLUS_SEARCH_ENGINE, 0
            )].toString()
        ) {
            add(customTaplusSearch[0].toString()) {
                MIUIActivity.safeSP.putAny(PrefKey.TAPLUS_SEARCH_ENGINE, 0)
                taplusSearchBinding.binding.Send().send(false)
            }
            add(customTaplusSearch[1].toString()) {
                MIUIActivity.safeSP.putAny(PrefKey.TAPLUS_SEARCH_ENGINE, 1)
                taplusSearchBinding.binding.Send().send(false)
            }
            add(customTaplusSearch[2].toString()) {
                MIUIActivity.safeSP.putAny(PrefKey.TAPLUS_SEARCH_ENGINE, 2)
                taplusSearchBinding.binding.Send().send(false)
            }
            add(customTaplusSearch[3].toString()) {
                MIUIActivity.safeSP.putAny(PrefKey.TAPLUS_SEARCH_ENGINE, 3)
                taplusSearchBinding.binding.Send().send(false)
            }
            add(customTaplusSearch[4].toString()) {
                MIUIActivity.safeSP.putAny(PrefKey.TAPLUS_SEARCH_ENGINE, 4)
                taplusSearchBinding.binding.Send().send(false)
            }
            add(customTaplusSearch[5].toString()) {
                MIUIActivity.safeSP.putAny(PrefKey.TAPLUS_SEARCH_ENGINE, 5)
                taplusSearchBinding.binding.Send().send(true)
            }
        }, dataBindingRecv = taplusUseBrowserBinding.binding.getRecv(1))
        TextSummaryWithArrow(
            TextSummaryV(textId = R.string.taplus_search_custom, onClickListener = {
                MIUIDialog(activity) {
                    setTitle(R.string.taplus_search_custom)
                    setEditText(
                        "", "https://example.com/s?q=%s"
                    )
                    setLButton(textId = R.string.button_cancel) {
                        dismiss()
                    }
                    setRButton(textId = R.string.button_ok) {
                        if (getEditText() != "") {
                            MIUIActivity.safeSP.putAny(
                                PrefKey.TAPLUS_SEARCH_URL, getEditText()
                            )
                        }
                        dismiss()
                    }
                }.show()
            }), dataBindingRecv = taplusSearchBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.taplus_landscape),
            SwitchV(PrefKey.TAPLUS_LANDSCAPE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.taplus_hide_shop),
            SwitchV(PrefKey.TAPLUS_HIDE_SHOP)
        )
        val isPadBinding = GetDataBinding({
            try {
                Class.forName("miui.os.Build").getDeclaredField("IS_TABLET").get(null) as Boolean
            }
            catch (e: Exception) {
                false
            }
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.taplus_unlock_pad),
            SwitchV(PrefKey.TAPLUS_UNLOCK_PAD),
            dataBindingRecv = isPadBinding.binding.getRecv(1)
        )
    }
}