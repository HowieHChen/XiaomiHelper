package dev.lackluster.mihelper.app.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import kotlinx.coroutines.launch

class AppEnvViewModel(
    private val repo: GlobalPreferencesRepository
) : ViewModel() {
    val envState = repo.envStateFlow

    init {
        refreshEnvState()
    }

    fun refreshEnvState() {
        viewModelScope.launch {
            repo.checkEnvironment()
        }
    }
}