package dev.lackluster.mihelper.hook.rules.systemui.media.data

import dev.lackluster.mihelper.hook.drawable.MediaControlBgDrawable
import dev.lackluster.mihelper.hook.rules.systemui.media.MediaControlBgFactory.defaultColorConfig

data class PlayerConfig(
    var mArtworkBoundId: Int = 0,
    var mArtworkNextBindRequestId: Int = 0,
    var mArtworkDrawable: MediaControlBgDrawable? = null,
    var mIsArtworkBound: Boolean = false,
    var mCurrentPkgName: String = "",

//    var mPrevColorConfig: MediaViewColorConfig = defaultColorConfig,
    var mCurrColorConfig: MediaViewColorConfig = defaultColorConfig,

    var lastWidth: Int = 0,
    var lastHeight: Int = 0,
)
