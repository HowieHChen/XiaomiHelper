package dev.lackluster.mihelper.hook.rules.systemui.media.data

import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

data class MiuiMediaViewHolderWrapper(
    var innerHashCode: Int,
    var titleText: TextView,
    var artistText: TextView,
    var albumView: ImageView,
    var mediaBg: ImageView,
    var seamlessIcon: ImageView,
    var action0: ImageButton,
    var action1: ImageButton,
    var action2: ImageButton,
    var action3: ImageButton,
    var action4: ImageButton,
    var elapsedTimeView: TextView,
    var totalTimeView: TextView,
    var seekBar: SeekBar
)
