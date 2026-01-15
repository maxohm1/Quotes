package max.ohm.quoteapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCategory
import max.ohm.quoteapp.domain.model.UserSettings
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.domain.repository.SettingsRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val dailyQuote: Quote? = null,
    val quotes: List<Quote> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: QuoteCategory? = null,
    val categoryCounts: Map<QuoteCategory, Int> = emptyMap()
)

sealed interface HomeEvent {
    data class ShowMessage(val message: String) : HomeEvent
    data class NavigateToQuoteDetail(val quoteId: String) : HomeEvent
    data class NavigateToShare(val quoteId: String) : HomeEvent
    data class NavigateToCategory(val category: QuoteCategory) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())
    
    init {
        loadInitialData()
        observeQuotes()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Refresh quotes from server
            when (val result = quoteRepository.refreshQuotes()) {
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        ) 
                    }
                }
                else -> {}
            }
            
            // Load daily quote
            loadDailyQuote()
            
            // Calculate category counts
            calculateCategoryCounts()
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    private fun observeQuotes() {
        viewModelScope.launch {
            combine(
                quoteRepository.getAllQuotes(),
                _uiState
            ) { quotes, state ->
                val filteredQuotes = when {
                    state.searchQuery.isNotBlank() -> quotes.filter { quote ->
                        quote.text.contains(state.searchQuery, ignoreCase = true) ||
                        quote.author.contains(state.searchQuery, ignoreCase = true)
                    }
                    state.selectedCategory != null -> quotes.filter { 
                        it.category == state.selectedCategory 
                    }
                    else -> quotes
                }
                filteredQuotes
            }.collect { filteredQuotes ->
                _uiState.update { it.copy(quotes = filteredQuotes) }
            }
        }
    }
    
    private suspend fun loadDailyQuote() {
        when (val result = quoteRepository.getDailyQuote()) {
            is Resource.Success -> {
                _uiState.update { it.copy(dailyQuote = result.data) }
            }
            is Resource.Error -> {
                // Try to get a random quote as fallback
                val randomQuote = quoteRepository.getRandomQuote()
                _uiState.update { it.copy(dailyQuote = randomQuote) }
            }
            else -> {}
        }
    }
    
    private suspend fun calculateCategoryCounts() {
        val allQuotes = quoteRepository.getAllQuotes().first()
        val counts = QuoteCategory.entries.associateWith { category ->
            allQuotes.count { it.category == category }
        }
        _uiState.update { it.copy(categoryCounts = counts) }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            quoteRepository.refreshQuotes()
            loadDailyQuote()
            calculateCategoryCounts()
            
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, selectedCategory = null) }
    }
    
    fun selectCategory(category: QuoteCategory?) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
    }
    
    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            val newFavoriteStatus = !quote.isFavorite
            when (val result = quoteRepository.toggleFavorite(quote.id, newFavoriteStatus)) {
                is Resource.Error -> {
                    _events.emit(HomeEvent.ShowMessage(result.message ?: "Failed to update favorite"))
                }
                is Resource.Success -> {
                    if (newFavoriteStatus) {
                        _events.emit(HomeEvent.ShowMessage("Added to favorites"))
                    }
                }
                else -> {}
            }
            
            // Update daily quote if it was favorited
            if (_uiState.value.dailyQuote?.id == quote.id) {
                _uiState.update { 
                    it.copy(dailyQuote = it.dailyQuote?.copy(isFavorite = newFavoriteStatus)) 
                }
            }
        }
    }
    
    fun shareQuote(quoteId: String) {
        viewModelScope.launch {
            _events.emit(HomeEvent.NavigateToShare(quoteId))
        }
    }
    
    fun navigateToQuoteDetail(quoteId: String) {
        viewModelScope.launch {
            _events.emit(HomeEvent.NavigateToQuoteDetail(quoteId))
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
