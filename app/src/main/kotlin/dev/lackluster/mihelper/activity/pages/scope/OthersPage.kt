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
import dev.lackluster.mihelper.utils.Device

@BMPage("scope_others", hideMenu = false)
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
        TitleText(textId = R.string.ui_scope_download)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.download_remove_xl),
            SwitchV(PrefKey.DOWNLOAD_REMOVE_XL)
        )
        Line()
        TitleText(textId = R.string.ui_scope_gallery)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.screenshot_unlimited_crop),
            SwitchV(PrefKey.SCREENSHOT_UNLIMITED_CROP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.gallery_screen_path_optim,
                tipsId = R.string.gallery_screen_path_optim_tips
            ),
            SwitchV(PrefKey.GALLERY_PATH_OPTIM)
        )
        Line()
        TitleText(textId = R.string.ui_scope_incallui)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.incallui_hide_crbt),
            SwitchV(PrefKey.INCALLUI_HIDE_CRBT)
        )
        Line()
        TitleText(textId = R.string.ui_scope_joyose)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.joyose_no_cloud_control),
            SwitchV(PrefKey.JOYOSE_NO_CLOUD_CONTROL)
        )
        Line()
        TitleText(textId = R.string.ui_scope_market)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.market_ad_block),
            SwitchV(PrefKey.MARKET_AD_BLOCK)
        )
        Line()
        TitleText(textId = R.string.ui_scope_mms)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.mms_ad_block),
            SwitchV(PrefKey.MMS_AD_BLOCK)
        )
        Line()
        TitleText(textId = R.string.ui_scope_music)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.music_ad_block),
            SwitchV(PrefKey.MUSIC_AD_BLOCK)
        )
        Line()
        TitleText(textId = R.string.ui_scope_screen_recorder)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.screen_recorder_save_to_movies,
                tipsId = R.string.screen_recorder_save_to_movies_tips
            ),
            SwitchV(PrefKey.SCREEN_RECORDER_SAVE_TO_MOVIES)
        )
        Line()
        TitleText(textId = R.string.ui_scope_screenshot)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.screenshot_save_as_png),
            SwitchV(PrefKey.SCREENSHOT_SAVE_AS_PNG)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.screenshot_save_to_picture,
                tipsId = R.string.screenshot_save_to_picture_tips
            ),
            SwitchV(PrefKey.SCREENSHOT_SAVE_TO_PICTURE)
        )
        Line()
        TitleText(textId = R.string.ui_scope_guard_provider)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.guard_forbid_upload_app),
            SwitchV(PrefKey.GUARD_FORBID_UPLOAD_APP)
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
            Device.isPad
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