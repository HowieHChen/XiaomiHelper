package dev.lackluster.mihelper.hook.rules.miuihome

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.PrefKey
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs.hasEnable

object FolderAdaptIconSize : YukiBaseHooker() {
    override fun onHook() {
        hasEnable(PrefKey.HOME_FOLDER_ADAPT_SIZE, extraCondition = { !Device.isPad }) {
            val folderIcon2x2 = "com.miui.home.launcher.folder.FolderIcon2x2".toClassOrNull()
            val folderIcon2x2p9 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_9".toClassOrNull()
            val folderIcon2x2p4 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2_4".toClassOrNull()
            // val folderIcon2x2t4 = "com.miui.home.launcher.folder.FolderIcon2x2_4".toClassOrNull()
            // val folderIcon2x2tp4 = "com.miui.home.launcher.folder.FolderIconPreviewContainer2X2".toClassOrNull()
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