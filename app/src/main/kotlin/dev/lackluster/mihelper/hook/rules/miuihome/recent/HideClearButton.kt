package dev.lackluster.mihelper.hook.rules.miuihome.recent

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.marginStart
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import dev.lackluster.mihelper.data.Pref
import dev.lackluster.mihelper.utils.Device
import dev.lackluster.mihelper.utils.Prefs
import dev.lackluster.mihelper.utils.factory.hasEnable

object HideClearButton : YukiBaseHooker() {
    private val memInfoClear = Prefs.getBoolean(Pref.Key.MiuiHome.RECENT_MEM_INFO_CLEAR, false)
    private val recentsContainerClass by lazy {
        "com.miui.home.recents.views.RecentsContainer".toClass()
    }
    private val cleanInRecentsMethod by lazy {
        recentsContainerClass.method {
            name = "cleanInRecents"
        }.give()
    }
    private val isClearContainerVisibleMethod by lazy {
        recentsContainerClass.method {
            name = "isClearContainerVisible"
        }.give()
    }
    private val cleanAllTaskMethod by lazy {
        recentsContainerClass.method {
            name = "cleanAllTask"
        }.give()
    }
    private val recentsDecorationsClass by lazy {
        "com.miui.home.recents.views.RecentsDecorations".toClass()
    }
    private val isClearContainerVisibleDMethod by lazy {
        recentsDecorationsClass.method {
            name = "isClearContainerVisible"
        }.give()
    }
    private val mClearAllViewJustClickedField by lazy {
        recentsDecorationsClass.field {
            name = "mClearAllViewJustClicked"
        }.give()
    }
    private val mResetClearAllViewClickableField by lazy {
        recentsDecorationsClass.field {
            name = "mResetClearAllViewClickable"
        }.give()
    }
    private val mRecentsContainerField by lazy {
        recentsDecorationsClass.field {
            name = "mRecentsContainer"
        }.give()
    }


    override fun onHook() {
        hasEnable(Pref.Key.MiuiHome.RECENT_HIDE_CLEAR_BUTTON) {
            if (Device.isPad) {
                recentsDecorationsClass.apply {
                    method {
                        name = "updateClearContainerVisible"
                    }.hook {
                        intercept()
                    }
                    method {
                        name = "findAndSetupViews"
                    }.hook {
                        after {
                            this.instance.current().field {
                                name = "mClearAllTaskContainerForPad"
                            }.cast<View>()?.apply {
                                isClickable = false
                                setOnClickListener(null)
                                visibility = View.GONE
                            }
                            if (memInfoClear) {
                                this.instance.current().field {
                                    name = "mTxtMemoryInfo1"
                                }.cast<TextView>()?.apply {
                                    isClickable = false
                                }
                                this.instance.current().field {
                                    name = "mTxtMemoryInfo2"
                                }.cast<TextView>()?.apply {
                                    isClickable = false
                                }
                                this.instance.current().field {
                                    name = "mSeparatorForMemoryInfo"
                                }.cast<View>()?.apply {
                                    isClickable = false
                                }
                                val mWorldContainer = this.instance.current().field {
                                    name = "mWorldContainer"
                                }.cast<FrameLayout>()
                                this.instance.current().field {
                                    name = "mTxtMemoryContainer"
                                }.cast<ViewGroup>()?.apply {
                                    mWorldContainer?.marginStart?.let {
                                        layoutParams = (layoutParams as MarginLayoutParams).apply {
                                            marginEnd = it
                                        }
                                    }
                                    isLongClickable = true
                                    setOnLongClickListener {
                                        val instance = this@after.instance as View
                                        if (mClearAllViewJustClickedField?.getBoolean(instance) == true) return@setOnLongClickListener true
                                        val isClearContainerVisible = isClearContainerVisibleDMethod?.invoke(instance) == true
                                        if (isClearContainerVisible) {
                                            mClearAllViewJustClickedField?.setBoolean(instance, true)
                                            val mResetClearAllViewClickable = mResetClearAllViewClickableField?.get(instance) as? Runnable
                                            instance.postDelayed(mResetClearAllViewClickable, 800L)
                                            val mRecentsContainer = mRecentsContainerField?.get(instance)
                                            cleanAllTaskMethod?.invoke(mRecentsContainer)
                                        }
                                        true
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                recentsContainerClass.apply {
                    method {
                        name = "onFinishInflate"
                    }.hook {
                        after {
                            this.instance.current().field {
                                name = "mClearAnimView"
                            }.cast<View>()?.apply {
                                isClickable = false
                                isLongClickable = false
                                setOnClickListener(null)
                                setOnLongClickListener(null)
                                visibility = View.INVISIBLE
                            }
                            if (memInfoClear) {
                                this.instance.current().field {
                                    name = "mTxtMemoryInfo1"
                                }.cast<TextView>()?.apply {
                                    isClickable = false
                                }
                                this.instance.current().field {
                                    name = "mTxtMemoryInfo2"
                                }.cast<TextView>()?.apply {
                                    isClickable = false
                                }
                                this.instance.current().field {
                                    name = "mSeparatorForMemoryInfo"
                                }.cast<View>()?.apply {
                                    isClickable = false
                                }
                                this.instance.current().field {
                                    name = "mTxtMemoryContainer"
                                }.cast<ViewGroup>()?.apply {
                                    isLongClickable = true
                                    setOnLongClickListener {
                                        val instance = this@after.instance
                                        val isClearContainerVisible = isClearContainerVisibleMethod?.invoke(instance) == true
                                        if (isClearContainerVisible) {
                                            cleanInRecentsMethod?.invoke(instance)
                                        }
                                        true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}