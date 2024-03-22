package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
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
        return activity.getString(R.string.page_others)
    }
    override fun onCreate() {
        TitleText(textId = R.string.scope_browser)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_browser_debug_mode, tipsId = R.string.others_browser_debug_mode_tips),
            SwitchV(PrefKey.BROWSER_DEBUG)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_browser_switch_env, tipsId = R.string.others_browser_switch_env_tips),
            SwitchV(PrefKey.BROWSER_SWITCH_ENV)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_browser_disable_update),
            SwitchV(PrefKey.BROWSER_NO_UPDATE)
        )
        Line()
        TitleText(textId = R.string.scope_download)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_download_fuck_xl),
            SwitchV(PrefKey.DOWNLOAD_REMOVE_XL)
        )
        Line()
        TitleText(textId = R.string.scope_gallery)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_gallery_unlimited_crop),
            SwitchV(PrefKey.SCREENSHOT_UNLIMITED_CROP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_gallery_path_optim,
                tipsId = R.string.others_gallery_path_optim_tips
            ),
            SwitchV(PrefKey.GALLERY_PATH_OPTIM)
        )
        Line()
        TitleText(textId = R.string.scope_in_call_ui)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.cleaner_incallui_hide_crbt),
            SwitchV(PrefKey.INCALLUI_HIDE_CRBT)
        )
        Line()
        TitleText(textId = R.string.scope_joyose)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_joyose_no_cloud_control),
            SwitchV(PrefKey.JOYOSE_NO_CLOUD_CONTROL)
        )
        Line()
        TitleText(textId = R.string.scope_market)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.market_ad_block),
            SwitchV(PrefKey.MARKET_AD_BLOCK)
        )
        Line()
        TitleText(textId = R.string.scope_mms)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.mms_ad_block),
            SwitchV(PrefKey.MMS_AD_BLOCK)
        )
        Line()
        TitleText(textId = R.string.scope_music)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.music_ad_block),
            SwitchV(PrefKey.MUSIC_AD_BLOCK)
        )
        Line()
        TitleText(textId = R.string.ui_scope_phone)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_settings_unlock_net_mode),
            SwitchV(PrefKey.PHONE_NETWORK_MODE_SETTINGS)
        )
        Line()
        TitleText(textId = R.string.scope_screen_recorder)
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_screen_recorder_save_to_movies,
                tipsId = R.string.others_screen_recorder_save_to_movies_tips
            ),
            SwitchV(PrefKey.SCREEN_RECORDER_SAVE_TO_MOVIES)
        )
        Line()
        TitleText(textId = R.string.scope_screenshot)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_screenshot_save_as_png),
            SwitchV(PrefKey.SCREENSHOT_SAVE_AS_PNG)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.others_screenshot_save_to_picture,
                tipsId = R.string.others_screenshot_save_to_picture_tips
            ),
            SwitchV(PrefKey.SCREENSHOT_SAVE_TO_PICTURE)
        )
        Line()
        TitleText(textId = R.string.scope_guard_provider)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.cleaner_privacy_block_upload_app),
            SwitchV(PrefKey.GUARD_FORBID_UPLOAD_APP)
        )
        Line()
        TitleText(textId = R.string.scope_settings)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_settings_show_google),
            SwitchV(PrefKey.SETTINGS_SHOW_GOOGLE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_settings_unlock_voip_assistant),
            SwitchV(PrefKey.SETTINGS_UNLOCK_VOIP_ASSISTANT)
        )
        Line()
        TitleText(textId = R.string.scope_taplus)
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
            it[0] = getString(R.string.search_engine_default)
            it[1] = getString(R.string.search_engine_baidu)
            it[2] = getString(R.string.search_engine_sogou)
            it[3] = getString(R.string.search_engine_bing)
            it[4] = getString(R.string.search_engine_google)
            it[5] = getString(R.string.search_engine_custom)
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
            TextSummaryV(textId = R.string.search_use_browser),
            SwitchV(PrefKey.TAPLUS_USE_BROWSER) {
                taplusUseBrowserBinding.binding.Send().send(it)
                taplusSearchBinding.binding.Send().send(it
                        && (MIUIActivity.safeSP.getInt(PrefKey.TAPLUS_SEARCH_ENGINE, 0) == 5))
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.search_engine), SpinnerV(
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
            TextSummaryV(textId = R.string.search_engine_custom_url, onClickListener = {
                MIUIDialog(activity) {
                    setTitle(R.string.search_engine_custom_url)
                    setEditText(
                        MIUIActivity.safeSP.getString(PrefKey.TAPLUS_SEARCH_URL, ""),
                        "https://example.com/s?q=%s"
                    )
                    setLButton(textId = R.string.button_cancel) {
                        dismiss()
                    }
                    setRButton(textId = R.string.button_ok) {
                        if (getEditText().isBlank()) {
                            MIUIActivity.safeSP.putAny(
                                PrefKey.TAPLUS_SEARCH_URL, ""
                            )
                            dismiss()
                        }
                        else if (getEditText().contains("%s")) {
                            MIUIActivity.safeSP.putAny(
                                PrefKey.TAPLUS_SEARCH_URL, getEditText()
                            )
                            dismiss()
                        }
                        else {
                            makeText(activity, getString(R.string.search_engine_custom_url_toast), LENGTH_SHORT).show()
                        }
                    }
                }.show()
            }), dataBindingRecv = taplusSearchBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.taplus_landscape),
            SwitchV(PrefKey.TAPLUS_LANDSCAPE)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.cleaner_taplus_hide_shop),
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
            TextSummaryV(textId = R.string.others_settings_unlock_taplus_for_pad),
            SwitchV(PrefKey.TAPLUS_UNLOCK_PAD),
            dataBindingRecv = isPadBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.scope_updater)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_updater_disable_validation, tipsId = R.string.others_updater_disable_validation_tips),
            SwitchV(PrefKey.UPDATER_DISABLE_VALIDATION)
        )
        Line()
        TitleText(textId = R.string.scope_mi_ai)
        val xiaoaiUseBrowserBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(
                PrefKey.XIAOAI_USE_BROWSER, false
            )
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        val customXiaoaiSearch: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.search_engine_default)
            it[1] = getString(R.string.search_engine_baidu)
            it[2] = getString(R.string.search_engine_sogou)
            it[3] = getString(R.string.search_engine_bing)
            it[4] = getString(R.string.search_engine_google)
            it[5] = getString(R.string.search_engine_custom)
        }
        val xiaoaiSearchBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.XIAOAI_USE_BROWSER, false)
                    && (MIUIActivity.safeSP.getInt(PrefKey.XIAOAI_SEARCH_ENGINE, 0) == 5)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.xiaoai_use_browser),
            SwitchV(PrefKey.XIAOAI_USE_BROWSER) {
                xiaoaiUseBrowserBinding.binding.Send().send(it)
                xiaoaiSearchBinding.binding.Send().send(it
                        && (MIUIActivity.safeSP.getInt(PrefKey.XIAOAI_SEARCH_ENGINE, 0) == 5))
            }
        )
        TextWithSpinner(
            TextV(textId = R.string.xiaoai_search_engine), SpinnerV(
                customXiaoaiSearch[MIUIActivity.safeSP.getInt(
                    PrefKey.XIAOAI_SEARCH_ENGINE, 0
                )].toString()
            ) {
                add(customXiaoaiSearch[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.XIAOAI_SEARCH_ENGINE, 0)
                    xiaoaiSearchBinding.binding.Send().send(false)
                }
                add(customXiaoaiSearch[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.XIAOAI_SEARCH_ENGINE, 1)
                    xiaoaiSearchBinding.binding.Send().send(false)
                }
                add(customXiaoaiSearch[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.XIAOAI_SEARCH_ENGINE, 2)
                    xiaoaiSearchBinding.binding.Send().send(false)
                }
                add(customXiaoaiSearch[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.XIAOAI_SEARCH_ENGINE, 3)
                    xiaoaiSearchBinding.binding.Send().send(false)
                }
                add(customXiaoaiSearch[4].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.XIAOAI_SEARCH_ENGINE, 4)
                    xiaoaiSearchBinding.binding.Send().send(false)
                }
                add(customXiaoaiSearch[5].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.XIAOAI_SEARCH_ENGINE, 5)
                    xiaoaiSearchBinding.binding.Send().send(true)
                }
            }, dataBindingRecv = xiaoaiUseBrowserBinding.binding.getRecv(1))
        TextSummaryWithArrow(
            TextSummaryV(textId = R.string.xiaoai_search_custom, onClickListener = {
                MIUIDialog(activity) {
                    setTitle(R.string.xiaoai_search_custom)
                    setEditText(
                        MIUIActivity.safeSP.getString(PrefKey.XIAOAI_SEARCH_URL, ""),
                        "https://example.com/s?q=%s"
                    )
                    setLButton(textId = R.string.button_cancel) {
                        dismiss()
                    }
                    setRButton(textId = R.string.button_ok) {
                        if (getEditText().isBlank()) {
                            MIUIActivity.safeSP.putAny(
                                PrefKey.XIAOAI_SEARCH_URL, ""
                            )
                            dismiss()
                        }
                        else if (getEditText().contains("%s")) {
                            MIUIActivity.safeSP.putAny(
                                PrefKey.XIAOAI_SEARCH_URL, getEditText()
                            )
                            dismiss()
                        }
                        else {
                            makeText(activity, getString(R.string.xiaoai_search_custom_toast), LENGTH_SHORT).show()
                        }
                    }
                }.show()
            }), dataBindingRecv = xiaoaiSearchBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.others_miai_hide_watermark),
            SwitchV(PrefKey.XIAOAI_HIDE_WATERMARK)
        )
    }
}