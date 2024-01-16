package dev.lackluster.mihelper.activity.pages.scope

import android.view.View
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SeekBarWithTextV
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.PrefKey

@BMPage("scope_systemui", hideMenu = false)
class SystemUIPage : BasePage(){
    override fun getTitle(): String {
        return activity.getString(R.string.ui_page_systemui)
    }
    override fun onCreate() {
        TitleText(textId = R.string.ui_title_systemui_status_bar)
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.systemui_statusbar_icon_tuner,
                onClickListener = { showFragment("icon_tuner") })
        )
        val notificationMaxBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.STATUSBAR_NOTIF_MAX, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_notification_max),
            SwitchV(PrefKey.STATUSBAR_NOTIF_MAX, dataBindingSend = notificationMaxBinding.bindingSend)
        )
        TextWithSeekBar(
            TextV(textId = R.string.status_bar_notification_max_icon),
            SeekBarWithTextV(PrefKey.STATUSBAR_NOTIF_ICON_MAX, 0, 15, 3),
            dataBindingRecv = notificationMaxBinding.binding.getRecv(1)
        )
        TextWithSeekBar(
            TextV(textId = R.string.status_bar_notification_max_dot),
            SeekBarWithTextV(PrefKey.STATUSBAR_NOTIF_DOT_MAX, 0, 5, 3),
            dataBindingRecv = notificationMaxBinding.binding.getRecv(1)
        )
        TextWithSeekBar(
            TextV(textId = R.string.status_bar_notification_max_lockscreen),
            SeekBarWithTextV(PrefKey.STATUSBAR_NOTIF_LOCKSCREEN_MAX, 0, 15, 3),
            dataBindingRecv = notificationMaxBinding.binding.getRecv(1)
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_notification_center)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.status_bar_clock_color_fix, tipsId = R.string.status_bar_clock_color_fix_tips),
            SwitchV(PrefKey.STATUSBAR_CLOCK_COLOR_FIX)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_notif_disable_whitelist),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_NO_WHITELIST)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_freeform,
                tipsId = R.string.systemui_notif_freeform_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_FREEFORM)
        )
        val notificationRedirectBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        val notificationRedirectDialogBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS, false) &&
                    MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        val notificationRedirectCustomBinding = GetDataBinding({
            MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS, false) &&
                    MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG, false) &&
                    MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_CUSTOM, false)
        }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_settings,
                tipsId = R.string.systemui_notif_redirect_settings_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS) {
                val settingRedirect = it
                val settingRedirectDialog = MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG, false)
                val settingRedirectDialogCustom = MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_CUSTOM, false)
                notificationRedirectBinding.binding.Send().send(
                    settingRedirect
                )
                notificationRedirectDialogBinding.binding.Send().send(
                    settingRedirect && settingRedirectDialog
                )
                notificationRedirectCustomBinding.binding.Send().send(
                    settingRedirect && settingRedirectDialog && settingRedirectDialogCustom
                )
            }
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_dialog,
                tipsId = R.string.systemui_notif_redirect_dialog_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG) {
                val settingRedirect = MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS, false)
                val settingRedirectDialog = it
                val settingRedirectDialogCustom = MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_CUSTOM, false)
                notificationRedirectDialogBinding.binding.Send().send(
                    settingRedirect && settingRedirectDialog
                )
                notificationRedirectCustomBinding.binding.Send().send(
                    settingRedirect && settingRedirectDialog && settingRedirectDialogCustom
                )
                if (settingRedirectDialog) {
                    val negativeText = MIUIActivity.safeSP.getString(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE, "")
                    val positiveText = MIUIActivity.safeSP.getString(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE, "")
                    val negativeTextDefault = getString(R.string.systemui_notif_redirect_dialog_negative_default)
                    val positiveTextDefault = getString(R.string.systemui_notif_redirect_dialog_positive_default)
                    if (
                        negativeText.isBlank() || positiveText.isBlank() ||
                        (!settingRedirectDialogCustom && (negativeText != negativeTextDefault || positiveText != positiveTextDefault))
                    ) {
                        MIUIActivity.safeSP.putAny(
                            PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE,
                            negativeTextDefault
                        )
                        MIUIActivity.safeSP.putAny(
                            PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE,
                            positiveTextDefault
                        )
                    }
                }
            },
            dataBindingRecv = notificationRedirectBinding.binding.getRecv(1)
        )
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_dialog_custom,
                tipsId = R.string.systemui_notif_redirect_dialog_custom_tips
            ),
            SwitchV(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_CUSTOM) {
                val settingRedirect = MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_SETTINGS, false)
                val settingRedirectDialog = MIUIActivity.safeSP.getBoolean(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG, false)
                val settingRedirectDialogCustom = it
                notificationRedirectCustomBinding.binding.Send().send(
                    settingRedirect && settingRedirectDialog && settingRedirectDialogCustom
                )
                val negativeText = MIUIActivity.safeSP.getString(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE, "")
                val positiveText = MIUIActivity.safeSP.getString(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE, "")
                val negativeTextDefault = getString(R.string.systemui_notif_redirect_dialog_negative_default)
                val positiveTextDefault = getString(R.string.systemui_notif_redirect_dialog_positive_default)
                if (
                    negativeText.isBlank() || positiveText.isBlank() ||
                    (!settingRedirectDialogCustom && (negativeText != negativeTextDefault || positiveText != positiveTextDefault))
                ) {
                    MIUIActivity.safeSP.putAny(
                        PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE,
                        negativeTextDefault
                    )
                    MIUIActivity.safeSP.putAny(
                        PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE,
                        positiveTextDefault
                    )
                }
            },
            dataBindingRecv = notificationRedirectDialogBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_dialog_negative,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.systemui_notif_redirect_dialog_negative)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: ${getString(R.string.systemui_notif_redirect_dialog_negative_default)}, ${getString(R.string.systemui_notif_redirect_dialog_hint)}"
                        )
                        setEditText("",
                            "${activity.getString(R.string.dialog_current_value)}: ${
                                MIUIActivity.safeSP.getString(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE, getString(R.string.systemui_notif_redirect_dialog_negative_default))
                            }"
                        )
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE,
                                        getEditText()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            else {
                                MIUIActivity.safeSP.putAny(
                                    PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_NEGATIVE,
                                    getString(R.string.systemui_notif_redirect_dialog_negative_default)
                                )
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = notificationRedirectCustomBinding.binding.getRecv(1)
        )
        TextSummaryWithArrow(
            TextSummaryV(
                textId = R.string.systemui_notif_redirect_dialog_positive,
                onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.systemui_notif_redirect_dialog_positive)
                        setMessage(
                            "${activity.getString(R.string.dialog_default_value)}: ${getString(R.string.systemui_notif_redirect_dialog_positive_default)}, ${getString(R.string.systemui_notif_redirect_dialog_hint)}"
                        )
                        setEditText("",
                            "${activity.getString(R.string.dialog_current_value)}: ${
                                MIUIActivity.safeSP.getString(PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE, getString(R.string.systemui_notif_redirect_dialog_positive_default))
                            }"
                        )
                        setLButton(textId = R.string.button_cancel) {
                            dismiss()
                        }
                        setRButton(textId = R.string.button_ok) {
                            if (getEditText().isNotEmpty()) {
                                runCatching {
                                    MIUIActivity.safeSP.putAny(
                                        PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE,
                                        getEditText()
                                    )
                                }.onFailure {
                                    Toast.makeText(activity, activity.getString(R.string.invalid_input), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            else {
                                MIUIActivity.safeSP.putAny(
                                    PrefKey.SYSTEMUI_NOTIF_CHANNEL_DIALOG_POSITIVE,
                                    getString(R.string.systemui_notif_redirect_dialog_positive_default)
                                )
                            }
                            dismiss()
                        }
                    }.show()
                }),
            dataBindingRecv = notificationRedirectCustomBinding.binding.getRecv(1)
        )
        val mediaControlStyle: HashMap<Int, String> = hashMapOf<Int, String>().also {
            it[0] = getString(R.string.systemui_media_control_style_default)
            it[1] = getString(R.string.systemui_media_control_style_enhance)
            it[2] = getString(R.string.systemui_media_control_style_android)
            it[3] = getString(R.string.systemui_media_control_style_android_blur)
        }
        TextWithSpinner(
            TextV(textId = R.string.systemui_media_control_style),
            SpinnerV(
                mediaControlStyle[MIUIActivity.safeSP.getInt(
                    PrefKey.SYSTEMUI_MEDIA_CONTROL_STYLE,
                    0
                )].toString()
            ) {
                add(mediaControlStyle[0].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.SYSTEMUI_MEDIA_CONTROL_STYLE, 0)
                }
                add(mediaControlStyle[1].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.SYSTEMUI_MEDIA_CONTROL_STYLE, 1)
                }
                add(mediaControlStyle[2].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.SYSTEMUI_MEDIA_CONTROL_STYLE, 2)
                }
                add(mediaControlStyle[3].toString()) {
                    MIUIActivity.safeSP.putAny(PrefKey.SYSTEMUI_MEDIA_CONTROL_STYLE, 3)
                }
            }
        )
        Line()
        TitleText(textId = R.string.ui_title_systemui_lock_screen)
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_hide_unlock_tip),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_UNLOCK_TIP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_hide_disturb),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_HIDE_DISTURB)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_double_tap),
            SwitchV(PrefKey.SYSTEMUI_DOUBLE_TAP_TO_SLEEP)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_lock_screen_block_editor, tipsId = R.string.systemui_lock_screen_block_editor_tips),
            SwitchV(PrefKey.SYSTEMUI_LOCKSCREEN_BLOCK_EDITOR)
        )
    }
}