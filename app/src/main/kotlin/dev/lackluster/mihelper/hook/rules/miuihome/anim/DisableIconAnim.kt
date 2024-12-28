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

package dev.lackluster.mihelper.hook.rules.miuihome.anim

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Prefs

object DisableIconAnim : YukiBaseHooker() {
    private val disableIconFolme = Prefs.getBoolean(Pref.Key.MiuiHome.ANIM_ICON_ZOOM, false)
    private val disableIconDarken = Prefs.getBoolean(Pref.Key.MiuiHome.ANIM_ICON_DARKEN, false)
    private val disableFolderFolme = Prefs.getBoolean(Pref.Key.MiuiHome.ANIM_FOLDER_ZOOM, false)
    private val disableFolderIconDarken = Prefs.getBoolean(Pref.Key.MiuiHome.ANIM_FOLDER_ICON_DARKEN, false)


    private val iconItemClass by lazy {
        "com.miui.home.launcher.ItemIcon".toClass()
    }
    private val shortcutIconClass by lazy {
        "com.miui.home.launcher.ShortcutIcon".toClass()
    }
    private val preViewTouchDelegateClass by lazy {
        "com.miui.home.launcher.folder.PreViewTouchDelegate".toClass()
    }
    private val folderIconClass by lazy {
        "com.miui.home.launcher.FolderIcon".toClass()
    }

    override fun onHook() {
        if (disableFolderIconDarken) {
            preViewTouchDelegateClass.apply {
                method {
                    name = "onTouchUp"
                }.hook {
                    intercept()
                }
                method {
                    name = "onTouchDown"
                }.hook {
                    intercept()
                }
            }
        }
        var newLauncher = false
        if (disableIconFolme || disableIconDarken || disableFolderFolme) {
            newLauncher = shortcutIconClass.method {
                name = "folmeDown"
            }.ignored().give() != null
        }
        if (newLauncher) {
            if (disableFolderFolme) {
                folderIconClass.apply {
                    method {
                        name = "folmeDown"
                    }.hook {
                        intercept()
                    }
                    method {
                        name = "folmeUp"
                    }.hook {
                        intercept()
                    }
                }
            }
            if (disableIconFolme) {
                shortcutIconClass.apply {
                    method {
                        name = "folmeDown"
                    }.hook {
                        intercept()
                    }
                    method {
                        name = "folmeUp"
                    }.hook {
                        intercept()
                    }
                }
            }
            if (disableIconDarken) {
                shortcutIconClass.apply {
                    constructor {
                        paramCount = 4
                    }.hook {
                        after {
                            this.instance.current().field {
                                name = "mEnableTouchMask"
                                superClass()
                            }.setFalse()
                        }
                    }
                    method {
                        name = "enableDrawTouchMask"
                    }.hook {
                        intercept()
                    }
                }
            }
        } else {
            if (disableFolderFolme || disableIconFolme) {
                iconItemClass.apply {
                    method {
                        name = "folmeDown"
                    }.hook {
                        before {
                            val shouldIntercept = if (shortcutIconClass.isInstance(this.instance)) {
                                disableIconFolme
                            } else if (folderIconClass.isInstance(this.instance)) {
                                disableFolderFolme
                            } else {
                                false
                            }
                            if (shouldIntercept) {
                                this.result = null
                            }
                        }
                    }
                    method {
                        name = "folmeUp"
                    }.hook {
                        before {
                            val shouldIntercept = if (shortcutIconClass.isInstance(this.instance)) {
                                disableIconFolme
                            } else if (folderIconClass.isInstance(this.instance)) {
                                disableFolderFolme
                            } else {
                                false
                            }
                            if (shouldIntercept) {
                                this.result = null
                            }
                        }
                    }
                }
            }
            if (disableIconDarken) {
                iconItemClass.apply {
                    constructor().hook {
                        after {
                            this.instance.current().field {
                                name = "mEnableTouchMask"
                                superClass()
                            }.setFalse()
                        }
                    }
                    method {
                        name = "enableDrawTouchMask"
                    }.hook {
                        intercept()
                    }
                }
            }
        }
    }
}