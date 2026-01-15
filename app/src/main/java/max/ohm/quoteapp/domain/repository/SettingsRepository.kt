package max.ohm.quoteapp.domain.repository

import kotlinx.coroutines.flow.Flow
import max.ohm.quoteapp.domain.model.UserSettings

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun getSettings(): UserSettings
    suspend fun syncSettings(): Unit
}
