package dev.lackluster.mihelper.activity.pages.sub

import android.content.pm.ApplicationInfo
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.fragment.MIUIFragment
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextSummaryWithArrowV
import cn.fkj233.ui.activity.view.TextSummaryWithSwitchV
import cn.fkj233.ui.dialog.MIUIDialog
import com.github.promeg.pinyinhelper.Pinyin
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.data.Pref

@BMPage("disable_fixed_orientation")
class DisableFixedOrientationPage : BasePage() {
    init {
        skipLoadItem = true
    }
    override fun getTitle(): String {
        return activity.getString(R.string.page_disable_fixed_orientation)
    }
    override fun onCreate() {
    }

    override fun asyncInit(fragment: MIUIFragment) {
        initApplicationList(fragment)
    }

    private fun initApplicationList(fragment: MIUIFragment, keyword: String = "") {
        fragment.showLoading()
        fragment.clearAll()
        fragment.addItem(
            TextSummaryWithArrowV(TextSummaryV(
                textId = R.string.common_search
            ) {
                MIUIDialog(activity) {
                    setTitle(R.string.common_search)
                    setEditText("", keyword)
                    setLButton(R.string.button_clear) {
                        initApplicationList(fragment, "")
                        dismiss()
                    }
                    setRButton(R.string.button_ok) {
                        initApplicationList(fragment, getEditText())
                        dismiss()
                    }
                }.show()
            })
        )
        runCatching {
            val applicationsInfo =
                activity.packageManager.getInstalledApplications(0)
                    .filter {
                        (it.flags and ApplicationInfo.FLAG_SYSTEM) != 1
                    }
                    .filter {
                        val labelLowercase = it.loadLabel(activity.packageManager).toString().lowercase()
                        val packageNameLowercase = it.packageName.lowercase()
                        val keywordLowercase = keyword.lowercase()
                        labelLowercase.contains(keywordLowercase) || packageNameLowercase.contains(keywordLowercase)
                    }
                    .associateWith {
                        val label = it.loadLabel(activity.packageManager).toString()
                        Pinyin.toPinyin(label, "").lowercase()
                    }
                    .entries.sortedBy { it.value }.map { it.key }
            for (i in applicationsInfo) {
                fragment.addItem(
                    TextSummaryWithSwitchV(
                        TextSummaryV(
                            text = i.loadLabel(activity.packageManager).toString(), tips = i.packageName
                        ),
                        SwitchV(Pref.Key.Android.BLOCK_FIXED_ORIENTATION + "_" + i.packageName) { switchValue ->
                            val packagesInfo1 = MIUIActivity.activity.packageManager.getInstalledApplications(0)
                            val shouldDisableFixedOrientationList = mutableListOf<String>()
                            for (j in packagesInfo1) {
                                if ((j.flags and ApplicationInfo.FLAG_SYSTEM) != 1) {
                                    val packageName = j.packageName
                                    if (MIUIActivity.safeSP.getBoolean("disable_fixed_orientation_$packageName", false)) {
                                        shouldDisableFixedOrientationList.add(packageName)
                                    }
                                }
                            }
                            if (switchValue) {
                                val packageName = i.packageName
                                if (!shouldDisableFixedOrientationList.contains(packageName)) {
                                    shouldDisableFixedOrientationList.add(packageName)
                                }
                            } else {
                                shouldDisableFixedOrientationList.remove(i.packageName)
                            }
                            MIUIActivity.safeSP.mSP?.edit()?.putStringSet(
                                Pref.Key.Android.BLOCK_FIXED_ORIENTATION_LIST, shouldDisableFixedOrientationList.toSet()
                            )?.apply()
                        }
                    )
                )
            }
        }
        fragment.closeLoading()
        fragment.initData()
    }
}