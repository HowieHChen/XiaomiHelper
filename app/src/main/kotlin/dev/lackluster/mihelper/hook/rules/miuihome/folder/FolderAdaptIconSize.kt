/*
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file is part of XiaomiHelper project
 * Copyright (C) 2023 HowieHChen, howie.dev@outlook.com

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lackluster.mihelper.hook.rules.miuihome.folder

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.factory.hasEnable

object FolderAdaptIconSize : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.FOLDER_ADAPT_SIZE, extraCondition = { !Device.isPad }) {
            if (Device.isPad) {
                val folderIcon2x2t4 = "com.miui.home.launcher.folder.FolderIcon2x2_4".toClassOrNull()
                val folderIcon2x2tp4 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2".toClassOrNull()
                folderIcon2x2t4?.method {
                    name = "addItemOnclickListener"
                }?.hook {
                    replaceUnit {
                        val pvChildList = this.instance.current().field {
                            name = "mPreviewContainer"
                            superClass()
                        }.any()?.current()?.method {
                            name = "getMPvChildList"
                            superClass()
                        }?.call() as? List<*> ?: return@replaceUnit
                        val realPvChildCount = pvChildList.size
                        if (realPvChildCount == 0) return@replaceUnit
                        val mLargePreviewIconItemsMax = if (realPvChildCount == 4) { 4 } else { 3 }
                        for (i in pvChildList.indices) {
                            pvChildList[i]?.current()?.method { name = "setViewClickListener"; superClass() }?.call(
                                if (i < mLargePreviewIconItemsMax) { this.instance } else { null }
                            )
                            pvChildList[i]?.current()?.method { name = "setClickable"; superClass() }?.call(
                               i < mLargePreviewIconItemsMax
                            )
                        }
                    }
                }
                folderIcon2x2tp4?.method {
                    name = "onPause"
                }?.hook {
                    replaceUnit {
                        val pvChildList = this.instance.current().method {
                            name = "getMPvChildList"
                            superClass()
                        }.call() as List<*>
                        val realPvChildCount = pvChildList.size
                        val mLargePreviewIconItemsMax = if (realPvChildCount == 4) { 4 } else { 3 }
                        for (previewIconView in pvChildList.take(mLargePreviewIconItemsMax)) {
                            if (previewIconView == null) continue
                            "com.miui.home.launcher.graphics.drawable.MamlCompat".toClass().method {
                                name = "onPause"
                                modifiers { isStatic }
                            }.get().call(
                                previewIconView.current().method { name = "getDrawable"; superClass() }.call()
                            )
                        }
                    }
                }
                folderIcon2x2tp4?.method {
                    name = "onResume"
                }?.hook {
                    replaceUnit {
                        val pvChildList = this.instance.current().method {
                            name = "getMPvChildList"
                            superClass()
                        }.call() as List<*>
                        val realPvChildCount = pvChildList.size
                        val mLargePreviewIconItemsMax = if (realPvChildCount == 4) { 4 } else { 3 }
                        for (previewIconView in pvChildList.take(mLargePreviewIconItemsMax)) {
                            if (previewIconView == null) continue
                            "com.miui.home.launcher.graphics.drawable.MamlCompat".toClass().method {
                                name = "onResume"
                                modifiers { isStatic }
                            }.get().call(
                                previewIconView.current().method { name = "getDrawable"; superClass() }.call()
                            )
                            this.instance.current().method { name = "postDelayed"; superClass() }.call(
                                "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2\$onResume\$1\$1".toClass().constructor().get().call(
                                    previewIconView
                                ),
                                this.instance.current().field { name = "mRefreshSyscTimeDelay"; superClass() }.long()
                            )
                        }
                    }
                }
                folderIcon2x2tp4?.method {
                    name = "preSetup2x2"
                }?.hook {
                    before {
                        val pvChildList = this.instance.current().method {
                            name = "getMPvChildList"
                            superClass()
                        }.call() as List<*>
                        val realPvChildCount = pvChildList.size
                        if (realPvChildCount == 4) {
                            val isLayoutRtl = "com.miui.home.launcher.DeviceConfig".toClass().method {
                                name = "isLayoutRtl"
                                modifiers { isStatic }
                            }.get().boolean()
                            val instanceView = this.instance as View
                            val mSmallItemWith = this.instance.current().field { name = "mSmallItemWith" }.int()
                            val mSmallItemHeight = this.instance.current().field { name = "mSmallItemHeight" }.int()
                            val mLargeItemWith = this.instance.current().field { name = "mLargeItemWith" }.int()
                            val mLargeItemHeight = this.instance.current().field { name = "mLargeItemHeight" }.int()
                            val mLarge2x2ItemMergeEdgeHor = this.instance.current().field { name = "mLarge2x2ItemMergeEdgeHor" }.int()
                            val mLarge2x2ItemMergeEdgeVer = this.instance.current().field { name = "mLarge2x2ItemMergeEdgeVer" }.int()
                            val i2: Int
                            val mSmall2x2ItemMergeInner: Int
                            if (isLayoutRtl) {
                                i2 = instanceView.paddingEnd + mLarge2x2ItemMergeEdgeHor + mLargeItemWith +
                                        this.instance.current().field { name = "mLarge2x2ItemMergeInnerHor" }.int()
                                mSmall2x2ItemMergeInner = this.instance.current().field { name = "mSmall2x2ItemMergeInner" }.int()
                            } else {
                                i2 = instanceView.paddingStart + mLarge2x2ItemMergeEdgeHor
                                mSmall2x2ItemMergeInner = 0
                            }
                            var i4 = i2
                            var paddingTop = instanceView.paddingTop + mLarge2x2ItemMergeEdgeVer
                            var i5 = 0
                            for (i in 0 until 7) {
                                if (i < 4) {
                                    val largeViewPreSetup2x2 =
                                        this.instance.current().method { name = "largeViewPreSetup2x2" }.call(
                                            i4, paddingTop, i2, instanceView.paddingTop + mLarge2x2ItemMergeEdgeVer, mLargeItemWith, mLargeItemHeight, i
                                        ) as IntArray
                                    paddingTop = largeViewPreSetup2x2[1]
                                    i5 = largeViewPreSetup2x2[0]
                                    if (isLayoutRtl && i == 2) {
                                        i5 += (mSmallItemWith + mSmall2x2ItemMergeInner)
                                    }
                                    i4 = i5
                                } else {
                                    val smallViewPreSetup2x2 =
                                        this.instance.current().method { name = "smallViewPreSetup2x2" }.call(
                                            i4, paddingTop, i5, mSmallItemWith, mSmallItemHeight, i
                                        ) as IntArray
                                    i4 = smallViewPreSetup2x2[0]
                                    paddingTop = smallViewPreSetup2x2[1]
                                }
                            }
                            this.result = null
                        }
                    }
                }
                folderIcon2x2tp4?.method {
                    name = "onMeasureChild2x2"
                }?.hook {
                    before {
                        val pvChildList = this.instance.current().method {
                            name = "getMPvChildList"
                            superClass()
                        }.call() as List<*>
                        val realPvChildCount = pvChildList.size
                        if (realPvChildCount == 4) {
                            val iconPreviewNum = this.instance.current().method { name = "getChildCount"; superClass() }.int().coerceAtMost(4)
                            for (i in 0 until iconPreviewNum) {
                                val mLargeItemWith = this.instance.current().field { name = "mLargeItemWith" }.int()
                                val mLargeItemHeight = this.instance.current().field { name = "mLargeItemHeight" }.int()
                                val previewIcon = (this.instance.current().field { name = "mPvChildList"; superClass() }.any() as List<*>)[i]
                                val previewIconView = previewIcon as View
                                previewIconView.measure(
                                    View.MeasureSpec.makeMeasureSpec(mLargeItemWith, View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(mLargeItemHeight, View.MeasureSpec.EXACTLY)
                                )
                                if (
                                    "com.miui.home.launcher.DeviceConfig".toClass().method {
                                        name = "isNewIcons"
                                        modifiers { isStatic }
                                    }.get().boolean()
                                ) {
                                    previewIcon.current().method { name = "setItemPadding"; superClass() }.call(
                                        "com.miui.home.launcher.DeviceConfig".toClass().method {
                                            name = "getIconImageViewPadding"
                                            paramCount = 2
                                            modifiers { isStatic }
                                        }.get().int(
                                            previewIconView.resources, previewIconView.measuredHeight
                                        )
                                    )
                                }
                            }
                            this.result = null
                        }
                    }
                }
            } else {
                val folderIcon2x2 = "com.miui.home.launcher.folder.FolderIcon2x2".toClass()
                val baseFolderPreview = "com.miui.home.launcher.folder.BaseFolderIconPreviewContainer2X2".toClass()
                val folderIcon2x2p9 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_9".toClass()
                val folderIcon2x2p4 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_4".toClass()
                val setMItemsMaxCountMethod = folderIcon2x2.method {
                    name = "setMItemsMaxCount"
                }.give() ?: return@hasEnable
                val setMLargeIconNumMethod = folderIcon2x2.method {
                    name = "setMLargeIconNum"
                }.give() ?: return@hasEnable

                val getMRealPvChildCountPreview = baseFolderPreview.method {
                    name = "getMRealPvChildCount"
                }.give() ?: return@hasEnable
                val setMItemsMaxCountPreview = baseFolderPreview.method {
                    name = "setMItemsMaxCount"
                }.give() ?: return@hasEnable
                val getMItemsMaxCount = baseFolderPreview.method {
                    name = "getMItemsMaxCount"
                }.give() ?: return@hasEnable
                val setMLargeIconNumPreview = baseFolderPreview.method {
                    name = "setMLargeIconNum"
                }.give() ?: return@hasEnable
                val getMLargeIconNumPreview = baseFolderPreview.method {
                    name = "getMLargeIconNum"
                }.give() ?: return@hasEnable

                folderIcon2x2.method {
                    name = "addItemOnclickListener"
                }.hook {
                    before {
                        val realPvChildCount = this.instance.current().field {
                            name = "mPreviewContainer"
                            superClass()
                        }.any()?.let {
                            getMRealPvChildCountPreview.invoke(it) as? Int
                        }?: return@before
                        val largeIconNum = this.instance.current().method {
                            name = "getMLargeIconNum"
                            superClass()
                        }.int()
                        if (largeIconNum == 3 || largeIconNum == 4) {
                            if (realPvChildCount < 5) {
                                setMItemsMaxCountMethod.invoke(this.instance, 4)
                                setMLargeIconNumMethod.invoke(this.instance, 4)
                            } else {
                                setMItemsMaxCountMethod.invoke(this.instance, 7)
                                setMLargeIconNumMethod.invoke(this.instance, 3)
                            }
                        } else if (largeIconNum == 8 || largeIconNum == 9) {
                            if (realPvChildCount < 10) {
                                setMItemsMaxCountMethod.invoke(this.instance, 9)
                                setMLargeIconNumMethod.invoke(this.instance, 9)
                            } else {
                                setMItemsMaxCountMethod.invoke(this.instance, 12)
                                setMLargeIconNumMethod.invoke(this.instance, 8)
                            }
                        }
                    }
                }
                baseFolderPreview.apply {
                    method {
                        name = "addPreView"
                    }.hook {
                        before {
                            if (this.args(0).cast<View>() == null) return@before
                            val mLargeIconNum = getMLargeIconNumPreview.invoke(this.instance) as Int
                            if (mLargeIconNum == 4) {
                                setMItemsMaxCountPreview.invoke(this.instance, 7)
                                setMLargeIconNumPreview.invoke(this.instance, 3)
                            } else if (mLargeIconNum == 9) {
                                setMItemsMaxCountPreview.invoke(this.instance, 12)
                                setMLargeIconNumPreview.invoke(this.instance, 8)
                            }
                        }
                    }
                    method {
                        name = "removeLastPreView"
                    }.hook {
                        before {
                            val lastView = this.instance.current().method {
                                name = "getLastView"
                                superClass()
                            }.call()
                            if (lastView !is View) {
                                return@before
                            }
                            val mRealPvChildCount = getMRealPvChildCountPreview.invoke(this.instance) as Int
                            val mLargeIconNum = getMLargeIconNumPreview.invoke(this.instance) as Int
                            if (mLargeIconNum == 3 && mRealPvChildCount == 5) {
                                setMItemsMaxCountPreview.invoke(this.instance, 4)
                                setMLargeIconNumPreview.invoke(this.instance, 4)
                            } else if (mLargeIconNum == 8 && mRealPvChildCount == 10) {
                                setMItemsMaxCountPreview.invoke(this.instance, 9)
                                setMLargeIconNumPreview.invoke(this.instance, 9)
                            }
                        }
                    }
                    method {
                        name = "isPreViewContainerOverload"
                    }.hook {
                        before {
                            val mItemsMaxCount = getMItemsMaxCount.invoke(this.instance) as Int
                            if (mItemsMaxCount == 4 || mItemsMaxCount == 9) {
                                this.result = false
                            }
                        }
                    }
                }
                folderIcon2x2p9.method {
                    name = "preSetup2x2"
                }.hook {
                    before {
                        val realPvChildCount = getMRealPvChildCountPreview.invoke(this.instance) as? Int ?: return@before
                        if (realPvChildCount < 10) {
                            setMItemsMaxCountPreview.invoke(this.instance, 9)
                            setMLargeIconNumPreview.invoke(this.instance, 9)
                        } else {
                            setMItemsMaxCountPreview.invoke(this.instance, 12)
                            setMLargeIconNumPreview.invoke(this.instance, 8)
                        }
                    }
                }
                folderIcon2x2p4.method {
                    name = "preSetup2x2"
                }.hook {
                    before {
                        val realPvChildCount = getMRealPvChildCountPreview.invoke(this.instance) as? Int ?: return@before
                        if (realPvChildCount < 5) {
                            setMItemsMaxCountPreview.invoke(this.instance, 4)
                            setMLargeIconNumPreview.invoke(this.instance, 4)
                        } else {
                            setMItemsMaxCountPreview.invoke(this.instance, 7)
                            setMLargeIconNumPreview.invoke(this.instance, 3)
                        }
                    }
                }
            }
        }
    }
}