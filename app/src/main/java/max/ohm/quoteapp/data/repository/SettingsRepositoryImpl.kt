package max.ohm.quoteapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import max.ohm.quoteapp.domain.model.AccentColor
import max.ohm.quoteapp.domain.model.FontSize
import max.ohm.quoteapp.domain.model.ThemeMode
import max.ohm.quoteapp.domain.model.UserSettings
import max.ohm.quoteapp.domain.repository.SettingsRepository
import max.ohm.quoteapp.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_SETTINGS)

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val dataStore = context.settingsDataStore

    private object PrefsKeys {
        val THEME_MODE = stringPreferencesKey(Constants.KEY_THEME_MODE)
        val ACCENT_COLOR = stringPreferencesKey(Constants.KEY_ACCENT_COLOR)
        val FONT_SIZE = stringPreferencesKey(Constants.KEY_FONT_SIZE)
        val NOTIFICATION_ENABLED = booleanPreferencesKey(Constants.KEY_NOTIFICATION_ENABLED)
        val NOTIFICATION_TIME = stringPreferencesKey(Constants.KEY_NOTIFICATION_TIME)
    }

    override val settings: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            themeMode = prefs[PrefsKeys.THEME_MODE]?.let { 
                ThemeMode.valueOf(it) 
            } ?: ThemeMode.SYSTEM,
            accentColor = prefs[PrefsKeys.ACCENT_COLOR]?.let { 
                AccentColor.valueOf(it) 
            } ?: AccentColor.PURPLE,
            fontSize = prefs[PrefsKeys.FONT_SIZE]?.let { 
                FontSize.valueOf(it) 
            } ?: FontSize.MEDIUM,
            notificationEnabled = prefs[PrefsKeys.NOTIFICATION_ENABLED] ?: true,
            notificationTime = prefs[PrefsKeys.NOTIFICATION_TIME] ?: "08:00"
        )
    }

    override suspend fun updateSettings(settings: UserSettings) {
        dataStore.edit { prefs ->
            prefs[PrefsKeys.THEME_MODE] = settings.themeMode.name
            prefs[PrefsKeys.ACCENT_COLOR] = settings.accentColor.name
            prefs[PrefsKeys.FONT_SIZE] = settings.fontSize.name
            prefs[PrefsKeys.NOTIFICATION_ENABLED] = settings.notificationEnabled
            prefs[PrefsKeys.NOTIFICATION_TIME] = settings.notificationTime
        }
    }

    override suspend fun getSettings(): UserSettings {
        return settings.first()
    }

    override suspend fun syncSettings() {
        // Settings are stored locally, no sync needed for now
        // Could be extended to sync with Supabase user_settings table
    }
}
