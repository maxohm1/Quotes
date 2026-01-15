package max.ohm.quoteapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.AccentColor
import max.ohm.quoteapp.domain.model.FontSize
import max.ohm.quoteapp.domain.model.ThemeMode
import max.ohm.quoteapp.domain.model.UserSettings
import max.ohm.quoteapp.domain.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())
    
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(themeMode = themeMode))
        }
    }
    
    fun updateAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(accentColor = accentColor))
        }
    }
    
    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(fontSize = fontSize))
        }
    }
    
    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(notificationEnabled = enabled))
        }
    }
    
    fun updateNotificationTime(time: String) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(notificationTime = time))
        }
    }
}
