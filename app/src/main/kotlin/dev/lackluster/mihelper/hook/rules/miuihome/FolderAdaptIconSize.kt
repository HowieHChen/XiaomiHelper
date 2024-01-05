package dev.lackluster.mihelper.hook.rules.miuihome

import android.view.View
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object FolderAdaptIconSize : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_FOLDER_ADAPT_SIZE) {
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
                            }
                            else {
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
                                }
                                else {
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
            }
            else {
                val folderIcon2x2 = "com.miui.home.launcher.folder.FolderIcon2x2".toClassOrNull()
                val folderIcon2x2p9 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_9".toClassOrNull()
                val folderIcon2x2p4 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_4".toClassOrNull()
                folderIcon2x2?.method {
                    name = "addItemOnclickListener"
                }?.hook {
                    before {
                        val realPvChildCount = this.instance.current().field {
                            name = "mPreviewContainer"
                            superClass()
                        }.any()?.current()?.method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }?.int() ?: return@before
                        val largeIconNum = this.instance.current().method {
                            name = "getMLargeIconNum"
                            superClass()
                        }.int()
                        if (largeIconNum == 3 || largeIconNum == 4) {
                            if (realPvChildCount < 5) {
                                this.instance.current().method {
                                    name = "setMItemsMaxCount"
                                    superClass()
                                }.call(4)
                                this.instance.current().method {
                                    name = "setMLargeIconNum"
                                    superClass()
                                }.call(4)
                            }
                            else {
                                this.instance.current().method {
                                    name = "setMItemsMaxCount"
                                    superClass()
                                }.call(7)
                                this.instance.current().method {
                                    name = "setMLargeIconNum"
                                    superClass()
                                }.call(3)
                            }
                        }
                        else if (largeIconNum == 8 || largeIconNum == 9) {
                            if (realPvChildCount < 10) {
                                this.instance.current().method {
                                    name = "setMItemsMaxCount"
                                    superClass()
                                }.call(9)
                                this.instance.current().method {
                                    name = "setMLargeIconNum"
                                    superClass()
                                }.call(9)
                            }
                            else {
                                this.instance.current().method {
                                    name = "setMItemsMaxCount"
                                    superClass()
                                }.call(12)
                                this.instance.current().method {
                                    name = "setMLargeIconNum"
                                    superClass()
                                }.call(8)
                            }
                        }
                    }
                }
                folderIcon2x2p9?.method {
                    name = "preSetup2x2"
                }?.hook {
                    before {
                        val realPvChildCount = this.instance.current().method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }.int()
                        if (realPvChildCount < 10) {
                            this.instance.current().method {
                                name = "setMItemsMaxCount"
                                superClass()
                            }.call(9)
                            this.instance.current().method {
                                name = "setMLargeIconNum"
                                superClass()
                            }.call(9)
                        }
                        else {
                            this.instance.current().method {
                                name = "setMItemsMaxCount"
                                superClass()
                            }.call(12)
                            this.instance.current().method {
                                name = "setMLargeIconNum"
                                superClass()
                            }.call(8)
                        }
                    }
                }
                folderIcon2x2p4?.method {
                    name = "preSetup2x2"
                }?.hook {
                    before {
                        val realPvChildCount = this.instance.current().method {
                            name = "getMRealPvChildCount"
                            superClass()
                        }.int()
                        if (realPvChildCount < 5) {
                            this.instance.current().method {
                                name = "setMItemsMaxCount"
                                superClass()
                            }.call(4)
                            this.instance.current().method {
                                name = "setMLargeIconNum"
                                superClass()
                            }.call(4)
                        }
                        else {
                            this.instance.current().method {
                                name = "setMItemsMaxCount"
                                superClass()
                            }.call(7)
                            this.instance.current().method {
                                name = "setMLargeIconNum"
                                superClass()
                            }.call(3)
                        }
                    }
                }
            }
        }
    }
}