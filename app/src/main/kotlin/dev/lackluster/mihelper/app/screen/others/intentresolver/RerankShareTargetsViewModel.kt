package dev.lackluster.mihelper.app.screen.others.intentresolver

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
import kotlinx.coroutines.launch

data class ShareTargetUiItem(
    val pkgName: String,
    val icon: ImageIcon?,
    val appName: String,
    val index: Int
)

sealed interface AddDialogState {
    object Hidden : AddDialogState                              // 弹窗关闭
    object Idle : AddDialogState                                // 弹窗打开，等待输入
    object Loading : AddDialogState                             // 正在校验/保存
    data class Error(val message: UiText) : AddDialogState      // 校验失败，显示错误
}

class RerankShareTargetsViewModel(
    private val repo: GlobalPreferencesRepository,
    private val appInfoRepository: AppInfoRepository,
) : ViewModel() {
    private val _addDialogState = MutableStateFlow<AddDialogState>(AddDialogState.Hidden)
    val addDialogState = _addDialogState.asStateFlow()

    private val _draftMap = MutableStateFlow<Map<String, Int>>(emptyMap())

    val uiItems: StateFlow<List<ShareTargetUiItem>> = combine(
        _draftMap,
        appInfoRepository.appDetailCacheFlow
    ) { map, cache ->
        map.entries
            .sortedBy { it.value }
            .map { entry ->
                val pkg = entry.key
                val detail = cache[pkg]

                ShareTargetUiItem(
                    pkgName = pkg,
                    icon = detail?.icon?.toImageSource()?.let {
                        ImageIcon(source = it, size = IconSize.App)
                    },
                    appName = detail?.appName ?: pkg,
                    index = entry.value
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        _draftMap.onEach { map ->
            map.keys.forEach { pkg ->
                viewModelScope.launch(Dispatchers.IO) {
                    appInfoRepository.fetchAppNameIfNeeded(pkg)
                }
            }
        }.launchIn(viewModelScope)
        viewModelScope.launch(Dispatchers.Default) {
            loadInitialData()
        }
        viewModelScope.launch(Dispatchers.Default) {
            repo.preferenceUpdates.collect { updatedKey ->
                if (updatedKey == Preferences.MiIntentResolver.TARGETS_PKG_INDEX_MAP) {
                    loadInitialData()
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            repo.globalReloadEvent.collect {
                loadInitialData()
            }
        }
    }

    fun loadInitialData() {
        val rawSet = repo.get(Preferences.MiIntentResolver.TARGETS_PKG_INDEX_MAP)
        val initialMap = rawSet.mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val index = parts[1].toIntOrNull()
                if (index != null) parts[0] to index else null
            } else null
        }.toMap()

        _draftMap.value = initialMap
    }

    fun openAddDialog() {
        _addDialogState.value = AddDialogState.Idle
    }

    fun closeAddDialog() {
        _addDialogState.value = AddDialogState.Hidden
    }

    fun submitNewApp(pkg: String, index: String) {
        _addDialogState.value = AddDialogState.Loading

        viewModelScope.launch {
            val currentMap = _draftMap.value
            val parsedIndex = index.toIntOrNull()
            when {
                pkg.isBlank() -> {
                    _addDialogState.value = AddDialogState.Error(R.string.others_intent_resolver_rerank_toast_invalid_pkg.toUiText())
                }
                parsedIndex == null || parsedIndex !in 0..100 -> {
                    _addDialogState.value = AddDialogState.Error(R.string.others_intent_resolver_rerank_toast_invalid_index.toUiText())
                }
                currentMap.containsKey(pkg) -> {
                    _addDialogState.value = AddDialogState.Error(R.string.others_intent_resolver_rerank_toast_pkg_exist.toUiText(pkg))
                }
                currentMap.containsValue(parsedIndex) -> {
                    val occupiedPkg = currentMap.entries.find { it.value == parsedIndex }?.key ?: ""
                    _addDialogState.value = AddDialogState.Error(R.string.others_intent_resolver_rerank_toast_index_exist.toUiText(parsedIndex, occupiedPkg))
                }
                else -> {
                    addApp(pkg, parsedIndex)
                    _addDialogState.value = AddDialogState.Hidden
                }
            }
        }
    }

    private fun addApp(pkg: String, index: Int) {
        viewModelScope.launch {
            val newMap = _draftMap.value.toMutableMap().apply { put(pkg, index) }
            _draftMap.value = newMap
        }
    }

    fun removeApp(pkg: String) {
        viewModelScope.launch {
            val newMap = _draftMap.value.toMutableMap().apply { remove(pkg) }
            _draftMap.value = newMap
        }
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            val stringSet = _draftMap.value.map { "${it.key}:${it.value}" }.toSet()
            repo.update(Preferences.MiIntentResolver.TARGETS_PKG_INDEX_MAP, stringSet)
        }
    }

    fun discard() {
        viewModelScope.launch {
            loadInitialData()
        }
    }

    fun reset() {
        viewModelScope.launch {
            _draftMap.value = emptyMap()
        }
    }
}