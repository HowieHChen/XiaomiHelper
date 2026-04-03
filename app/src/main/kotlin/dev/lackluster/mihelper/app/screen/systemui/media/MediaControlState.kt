package dev.lackluster.mihelper.app.screen.systemui.media

data class MediaControlState(
    val background: MediaBackgroundState = MediaBackgroundState(),
    val layout: MediaLayoutState = MediaLayoutState(),
    val element: MediaElementState = MediaElementState()
)

data class MediaBackgroundState(
    val style: Int = 0,
    val blurRadius: Int = 10,
    val allowReverse: Boolean = false,
    val ambientLightOpt: Boolean = false,
    val colorAnim: Boolean = false,

    val ambientLight: Boolean = false,     // 整合了通知中心的开关和灵动岛的逻辑判断
    val ambientLightType: Int = 0,         // 灵动岛特有
    val alwaysDark: Boolean = false        // 通知中心特有
) {
    val effectiveLightOpt: Boolean
        get() = ambientLight && ambientLightType == 2 && ambientLightOpt
}

data class MediaLayoutState(
    val album: Int = 0,
    val unlockAction: Boolean = false,
    val leftActions: Boolean = false,
    val actionsOrder: Int = 0,
    val hideTime: Boolean = false,
    val hideSeamless: Boolean = false,
    val headerTopMargin: Float = 21.0f,
    val headerPadding: Float = 4.0f
)

data class MediaElementState(
    val customTextSize: Boolean = false,
    val titleSize: Float = 18.0f,
    val artistSize: Float = 12.0f,
    val timeSize: Float = 12.0f,
    val progressStyle: Int = 0,
    val progressWidth: Float = 6.0f,
    val progressComet: Boolean = false,
    val thumbStyle: Int = 0,
    val progressRound: Boolean = false,

    val albumShadow: Boolean = true,       // 通知中心特有
    val albumFlip: Boolean = true          // 通知中心特有
)