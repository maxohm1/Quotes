package max.ohm.quoteapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.UserSettings
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.domain.repository.SettingsRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<Quote> = emptyList(),
    val error: String? = null
)

sealed interface FavoritesEvent {
    data class ShowMessage(val message: String) : FavoritesEvent
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<FavoritesEvent>()
    val events = _events.asSharedFlow()
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())
    
    init {
        observeFavorites()
    }
    
    private fun observeFavorites() {
        viewModelScope.launch {
            quoteRepository.getFavoriteQuotes().collect { favorites ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        favorites = favorites
                    ) 
                }
            }
        }
    }
    
    fun removeFavorite(quote: Quote) {
        viewModelScope.launch {
            when (val result = quoteRepository.toggleFavorite(quote.id, false)) {
                is Resource.Error -> {
                    _events.emit(FavoritesEvent.ShowMessage(result.message ?: "Failed to remove"))
                }
                is Resource.Success -> {
                    _events.emit(FavoritesEvent.ShowMessage("Removed from favorites"))
                }
                else -> {}
            }
        }
    }
}
