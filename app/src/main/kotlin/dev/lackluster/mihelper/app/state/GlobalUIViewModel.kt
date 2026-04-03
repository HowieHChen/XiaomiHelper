package dev.lackluster.mihelper.app.state

import androidx.lifecycle.ViewModel
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository

class GlobalUIViewModel(
    private val repo: GlobalPreferencesRepository
) : ViewModel() {
    val configFlow = repo.uiConfigFlow
}