package dev.lackluster.mihelper.app.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lackluster.mihelper.app.manager.AppEnvironmentManager
import kotlinx.coroutines.launch

class AppEnvViewModel(
    private val envManager: AppEnvironmentManager
) : ViewModel() {

    val envState = envManager.envStateFlow
    val xposedState = envManager.xposedState

    init {
        refreshEnvState()
    }

    fun refreshEnvState() {
        viewModelScope.launch {
            envManager.checkRoot()
        }
    }
}