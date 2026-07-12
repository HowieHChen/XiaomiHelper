package dev.lackluster.mihelper.app.screen.systemui.notif.pinned

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.hyperx.ui.component.IconSize
import dev.lackluster.hyperx.ui.component.ImageIcon
import dev.lackluster.mihelper.R
import dev.lackluster.mihelper.app.repository.AppInfoRepository
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import dev.lackluster.mihelper.app.state.UiText
import dev.lackluster.mihelper.app.utils.toImageSource
import dev.lackluster.mihelper.app.utils.toUiText
import dev.lackluster.mihelper.data.preference.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PinnedAppUiItem(
    val pkgName: String,
    val icon: ImageIcon?,
    val appName: String,
    val index: Int,
)

data class PinnedAppDialogState(
    val isVisible: Boolean = false,
    val isNew: Boolean = false,
    val inputPkg: String = "",
    val inputIndex: String = "0",
    val isLoading: Boolean = false,
    val error: UiText? = null,
)

class NotifPinnedAppsViewModel(
    private val repo: GlobalPreferencesRepository,
    private val appInfoRepository: AppInfoRepository,
) : ViewModel() {
    private val _dialogState = MutableStateFlow(PinnedAppDialogState())
    val dialogState = _dialogState.asStateFlow()

    private val _draftEnabled = MutableStateFlow(false)
    val draftEnabled = _draftEnabled.asStateFlow()

    private val _draftMap = MutableStateFlow<Map<String, Int>>(emptyMap())

    val uiItems: StateFlow<List<PinnedAppUiItem>> = combine(
        _draftMap,
        appInfoRepository.appDetailCacheFlow,
    ) { map, cache ->
        map.entries.sortedWith(compareBy<Map.Entry<String, Int>> { it.value }.thenBy { it.key }).map { entry ->
            val detail = cache[entry.key]
            PinnedAppUiItem(
                pkgName = entry.key,
                icon = detail?.icon?.toImageSource()?.let { ImageIcon(source = it, size = IconSize.App) },
                appName = detail?.appName ?: entry.key,
                index = entry.value,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    init {
        _draftMap.onEach { map ->
            map.keys.forEach { pkg ->
                viewModelScope.launch(Dispatchers.IO) {
                    appInfoRepository.fetchAppNameIfNeeded(pkg)
                }
            }
        }.launchIn(viewModelScope)
        viewModelScope.launch(Dispatchers.Default) { loadInitialData() }
        viewModelScope.launch(Dispatchers.Default) {
            repo.preferenceUpdates.collect { updatedKey ->
                if (
                    updatedKey == Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ENABLED ||
                    updatedKey == Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ORDER
                ) {
                    loadInitialData()
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            repo.globalReloadEvent.collect { loadInitialData() }
        }
    }

    fun loadInitialData() {
        _draftEnabled.value = repo.get(Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ENABLED)
        _draftMap.value = repo.get(Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ORDER)
            .mapNotNull { rule ->
                val parts = rule.split(":", limit = 2)
                val pkg = parts.getOrNull(0)?.takeIf { it.isNotBlank() }
                val index = parts.getOrNull(1)?.toIntOrNull()?.takeIf { it >= 0 }
                if (pkg != null && index != null) pkg to index else null
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, indexes) -> indexes.minOrNull() ?: 0 }
    }

    fun updateEnabled(enabled: Boolean) {
        _draftEnabled.value = enabled
    }

    fun openDialog(initialPkg: String = "", initialIndex: String = "0") {
        _dialogState.value = PinnedAppDialogState(
            isVisible = true,
            isNew = initialPkg.isBlank(),
            inputPkg = initialPkg,
            inputIndex = initialIndex,
        )
    }

    fun closeDialog() {
        _dialogState.value = PinnedAppDialogState()
    }

    fun updateInputPkg(pkg: String) {
        _dialogState.update { it.copy(inputPkg = pkg, error = null) }
    }

    fun updateInputIndex(index: String) {
        _dialogState.update { it.copy(inputIndex = index, error = null) }
    }

    fun submitApp() {
        val state = _dialogState.value
        _dialogState.update { it.copy(isLoading = true, error = null) }

        val pkg = state.inputPkg.trim()
        val index = state.inputIndex.toIntOrNull()
        when {
            pkg.isBlank() -> showDialogError(R.string.systemui_notif_lr_pinned_apps_invalid_pkg.toUiText())
            index == null || index < 0 -> showDialogError(R.string.systemui_notif_lr_pinned_apps_invalid_index.toUiText())
            state.isNew && _draftMap.value.containsKey(pkg) -> {
                showDialogError(R.string.systemui_notif_lr_pinned_apps_pkg_exists.toUiText(pkg))
            }
            else -> {
                _draftMap.update { it + (pkg to index) }
                closeDialog()
            }
        }
    }

    private fun showDialogError(message: UiText) {
        _dialogState.update { it.copy(isLoading = false, error = message) }
    }

    fun removeApp(pkg: String) {
        _draftMap.update { it - pkg }
    }

    fun reset() {
        _draftEnabled.value = false
        _draftMap.value = emptyMap()
    }

    fun save() {
        val rules = _draftMap.value.map { "${it.key}:${it.value}" }.toSet()
        val enabled = _draftEnabled.value
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.update(Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ORDER, rules)
                repo.update(Preferences.SystemUI.NotifCenter.LR_OPT_PINNED_APPS_ENABLED, enabled)
            }
        }
    }

    fun discard() {
        loadInitialData()
    }
}
