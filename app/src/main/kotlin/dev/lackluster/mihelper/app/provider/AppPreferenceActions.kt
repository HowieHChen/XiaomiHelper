package dev.lackluster.mihelper.app.provider

import dev.lackluster.hyperx.ui.preference.core.PreferenceActions
import dev.lackluster.hyperx.ui.preference.core.PreferenceKey
import dev.lackluster.mihelper.app.repository.GlobalPreferencesRepository
import kotlinx.coroutines.flow.Flow

class AppPreferenceActions(
    private val repo: GlobalPreferencesRepository
) : PreferenceActions {
    override val preferenceUpdates: Flow<PreferenceKey<*>>
        get() = repo.preferenceUpdates

    override fun <T : Any> get(key: PreferenceKey<T>): T {
        return repo.get(key)
    }

    override fun <T : Any> update(key: PreferenceKey<T>, value: T) {
        repo.update(key, value)
    }
}