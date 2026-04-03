package dev.lackluster.mihelper.app.screen.systemui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val islandKeys = setOf(
    Preferences.SystemUI.MediaControl.DynamicIsland.BG_AMBIENT_LIGHT_TYPE,

    Preferences.SystemUI.MediaControl.Shared.LYT_UNLOCK_ACTION,
    Preferences.SystemUI.MediaControl.Shared.ELM_ALBUM_FLIP,

    Preferences.SystemUI.MediaControl.Shared.BG_STYLE.island,
    Preferences.SystemUI.MediaControl.Shared.BG_BLUR_RADIUS.island,
    Preferences.SystemUI.MediaControl.Shared.BG_ALLOW_REVERSE.island,
    Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.island,
    Preferences.SystemUI.MediaControl.Shared.BG_COLOR_ANIM.island,

    Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.island,
    Preferences.SystemUI.MediaControl.Shared.LYT_LEFT_ACTIONS.island,
    Preferences.SystemUI.MediaControl.Shared.LYT_ACTIONS_ORDER.island,
    Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_TIME.island,
    Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_SEAMLESS.island,
    Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_TOP_MARGIN.island,
    Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_PADDING.island,

    Preferences.SystemUI.MediaControl.Shared.ELM_CUSTOM_TEXT_SIZE.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_TITLE_SIZE.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_ARTIST_SIZE.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_TIME_SIZE.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_STYLE.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_WIDTH.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_COMET.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_THUMB_STYLE.island,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_ROUND.island
)

private val normalKeys = setOf(
    Preferences.SystemUI.MediaControl.NotifCenter.BG_AMBIENT_LIGHT,
    Preferences.SystemUI.MediaControl.NotifCenter.BG_ALWAYS_DARK,
    Preferences.SystemUI.MediaControl.NotifCenter.ELM_ALBUM_SHADOW,

    Preferences.SystemUI.MediaControl.Shared.LYT_UNLOCK_ACTION,
    Preferences.SystemUI.MediaControl.Shared.ELM_ALBUM_FLIP,

    Preferences.SystemUI.MediaControl.Shared.BG_STYLE.notif,
    Preferences.SystemUI.MediaControl.Shared.BG_BLUR_RADIUS.notif,
    Preferences.SystemUI.MediaControl.Shared.BG_ALLOW_REVERSE.notif,
    Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.notif,
    Preferences.SystemUI.MediaControl.Shared.BG_COLOR_ANIM.notif,

    Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.notif,
    Preferences.SystemUI.MediaControl.Shared.LYT_LEFT_ACTIONS.notif,
    Preferences.SystemUI.MediaControl.Shared.LYT_ACTIONS_ORDER.notif,
    Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_TIME.notif,
    Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_SEAMLESS.notif,
    Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_TOP_MARGIN.notif,
    Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_PADDING.notif,

    Preferences.SystemUI.MediaControl.Shared.ELM_CUSTOM_TEXT_SIZE.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_TITLE_SIZE.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_ARTIST_SIZE.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_TIME_SIZE.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_STYLE.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_WIDTH.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_COMET.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_THUMB_STYLE.notif,
    Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_ROUND.notif
)

class MediaControlViewModel(
    private val repo: GlobalPreferencesRepository
) : ViewModel() {
    private val _normalState = MutableStateFlow(loadConfig(isIsland = false))
    val normalState = _normalState.asStateFlow()

    private val _islandState = MutableStateFlow(loadConfig(isIsland = true))
    val islandState = _islandState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repo.preferenceUpdates.collect { updatedKey ->
                if (updatedKey in islandKeys) {
                    _islandState.update { loadConfig(isIsland = true) }
                }
                if (updatedKey in normalKeys) {
                    _normalState.update { loadConfig(isIsland = false) }
                }
            }
        }
    }

    private fun loadConfig(isIsland: Boolean): MediaControlState {
        val ambientLight = if (isIsland) {
            repo.get(Preferences.SystemUI.MediaControl.DynamicIsland.BG_AMBIENT_LIGHT_TYPE) != 1
        } else {
            repo.get(Preferences.SystemUI.MediaControl.NotifCenter.BG_AMBIENT_LIGHT)
        }

        val ambientLightType = if (isIsland) {
            repo.get(Preferences.SystemUI.MediaControl.DynamicIsland.BG_AMBIENT_LIGHT_TYPE)
        } else { 2 }

        val backgroundState = MediaBackgroundState(
            style = repo.get(Preferences.SystemUI.MediaControl.Shared.BG_STYLE.get(isIsland)),
            blurRadius = repo.get(Preferences.SystemUI.MediaControl.Shared.BG_BLUR_RADIUS.get(isIsland)),
            allowReverse = repo.get(Preferences.SystemUI.MediaControl.Shared.BG_ALLOW_REVERSE.get(isIsland)),
            ambientLightOpt = repo.get(Preferences.SystemUI.MediaControl.Shared.BG_AMBIENT_LIGHT_OPT.get(isIsland)),
            colorAnim = repo.get(Preferences.SystemUI.MediaControl.Shared.BG_COLOR_ANIM.get(isIsland)),

            ambientLight = ambientLight,
            ambientLightType = ambientLightType,
            alwaysDark = if (isIsland) true else repo.get(Preferences.SystemUI.MediaControl.NotifCenter.BG_ALWAYS_DARK)
        )

        val layoutState = MediaLayoutState(
            album = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_ALBUM.get(isIsland)),
            leftActions = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_LEFT_ACTIONS.get(isIsland)),
            actionsOrder = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_ACTIONS_ORDER.get(isIsland)),
            hideTime = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_TIME.get(isIsland)),
            hideSeamless = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_HIDE_SEAMLESS.get(isIsland)),
            headerTopMargin = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_TOP_MARGIN.get(isIsland)),
            headerPadding = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_HEADER_PADDING.get(isIsland)),

            unlockAction = repo.get(Preferences.SystemUI.MediaControl.Shared.LYT_UNLOCK_ACTION)
        )

        val elementState = MediaElementState(
            customTextSize = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_CUSTOM_TEXT_SIZE.get(isIsland)),
            titleSize = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_TITLE_SIZE.get(isIsland)),
            artistSize = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_ARTIST_SIZE.get(isIsland)),
            timeSize = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_TIME_SIZE.get(isIsland)),
            progressStyle = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_STYLE.get(isIsland)),
            progressWidth = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_WIDTH.get(isIsland)),
            progressComet = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_COMET.get(isIsland)),
            thumbStyle = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_THUMB_STYLE.get(isIsland)),
            progressRound = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_PROGRESS_ROUND.get(isIsland)),

            albumShadow = if (isIsland) false else repo.get(Preferences.SystemUI.MediaControl.NotifCenter.ELM_ALBUM_SHADOW),
            albumFlip = repo.get(Preferences.SystemUI.MediaControl.Shared.ELM_ALBUM_FLIP)
        )

        return MediaControlState(
            background = backgroundState,
            layout = layoutState,
            element = elementState
        )
    }
}