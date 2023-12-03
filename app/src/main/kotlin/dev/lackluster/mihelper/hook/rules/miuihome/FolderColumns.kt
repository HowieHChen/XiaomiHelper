package dev.lackluster.mihelper.hook.rules.miuihome

import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ListView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object FolderColumns : YukiBaseHooker() {
    private val value by lazy {
        Prefs.getInt(PrefKey.HOME_FOLDER_COLUMNS, 3)
    }
    private val defValue by lazy {
        if (Device.isPad) 4 else 3
    }
    private val noPadding by lazy {
        Prefs.getBoolean(PrefKey.HOME_FOLDER_NO_PADDING, false)
    }
    override fun onHook() {
        if (value == defValue && noPadding) {
            "com.miui.home.launcher.Folder".toClass()
                .method {
                    name = "bind"
                }
                .hookAll {
                    after {
                        val mContent = this.instance.current().field {
                            name = "mContent"
                        }.any() as GridView
                        mContent.setPadding(0, 0, 0, 0)
                        val layoutParams = mContent.layoutParams
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        mContent.layoutParams = layoutParams
                    }
                }
        }
        else if (value != defValue) {
            "com.miui.home.launcher.Folder".toClass()
                .method {
                    name = "bind"
                }
                .hookAll {
                    after {
                        val columns: Int = value
                        val mContent = this.instance.current().field {
                            name = "mContent"
                        }.any() as GridView
                        mContent.numColumns = columns
                        if (noPadding) {
                            mContent.setPadding(0, 0, 0, 0)
                            val layoutParams = mContent.layoutParams
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                            mContent.layoutParams = layoutParams
                        }
                        if (columns > defValue) {
                            val mBackgroundView = this.instance.current().field {
                                name = "mBackgroundView"
                            }.any() as LinearLayout
                            mBackgroundView.setPadding(
                                mBackgroundView.paddingLeft / 3,
                                mBackgroundView.paddingTop,
                                mBackgroundView.paddingRight / 3,
                                mBackgroundView.paddingBottom
                            )
                        }
                    }
                }
        }
    }
}