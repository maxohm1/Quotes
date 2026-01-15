package max.ohm.quoteapp.presentation.collectiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCollection
import max.ohm.quoteapp.domain.repository.CollectionRepository
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

data class CollectionDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val collection: QuoteCollection? = null,
    val quotes: List<Quote> = emptyList()
)

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val quoteRepository: QuoteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CollectionDetailUiState())
    val uiState: StateFlow<CollectionDetailUiState> = _uiState.asStateFlow()
    
    private var currentCollectionId: String? = null
    
    fun loadCollection(collectionId: String) {
        currentCollectionId = collectionId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val collection = collectionRepository.getCollectionById(collectionId)
                if (collection != null) {
                    _uiState.update { 
                        it.copy(isLoading = false, collection = collection) 
                    }
                    
                    // Observe quotes in collection
                    collectionRepository.getQuotesInCollection(collectionId).collect { quotes ->
                        _uiState.update { it.copy(quotes = quotes) }
                    }
                } else {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "Collection not found") 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message ?: "Failed to load collection") 
                }
            }
        }
    }
    
    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            val newFavoriteStatus = !quote.isFavorite
            when (quoteRepository.toggleFavorite(quote.id, newFavoriteStatus)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            quotes = state.quotes.map {
                                if (it.id == quote.id) it.copy(isFavorite = newFavoriteStatus)
                                else it
                            }
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    fun removeFromCollection(quoteId: String) {
        val collectionId = currentCollectionId ?: return
        viewModelScope.launch {
            collectionRepository.removeQuoteFromCollection(collectionId, quoteId)
        }
    }
}
