package max.ohm.quoteapp.presentation.quotedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

data class QuoteDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val quote: Quote? = null
)

@HiltViewModel
class QuoteDetailViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuoteDetailUiState())
    val uiState: StateFlow<QuoteDetailUiState> = _uiState.asStateFlow()
    
    fun loadQuote(quoteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val quote = quoteRepository.getQuoteById(quoteId)
                if (quote != null) {
                    _uiState.update { 
                        it.copy(isLoading = false, quote = quote) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "Quote not found") 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Failed to load quote") 
                }
            }
        }
    }
    
    fun toggleFavorite() {
        val currentQuote = _uiState.value.quote ?: return
        
        viewModelScope.launch {
            val newFavoriteStatus = !currentQuote.isFavorite
            when (quoteRepository.toggleFavorite(currentQuote.id, newFavoriteStatus)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(quote = currentQuote.copy(isFavorite = newFavoriteStatus)) 
                    }
                }
                is Resource.Error -> {
                    // Silently fail, could show a snackbar
                }
                else -> {}
            }
        }
    }
}
