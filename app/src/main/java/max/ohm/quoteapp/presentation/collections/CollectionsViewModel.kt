package max.ohm.quoteapp.presentation.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCollection
import max.ohm.quoteapp.domain.model.User
import max.ohm.quoteapp.domain.repository.AuthRepository
import max.ohm.quoteapp.domain.repository.CollectionRepository
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

data class CollectionsUiState(
    val isLoading: Boolean = true,
    val collections: List<QuoteCollection> = emptyList(),
    val selectedCollection: QuoteCollection? = null,
    val quotesInCollection: List<Quote> = emptyList(),
    val showCreateDialog: Boolean = false,
    val showAddToCollectionDialog: Boolean = false,
    val newCollectionName: String = "",
    val newCollectionDescription: String = "",
    val quoteToAdd: String? = null,
    val error: String? = null
)

sealed interface CollectionsEvent {
    data class ShowMessage(val message: String) : CollectionsEvent
}

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val quoteRepository: QuoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<CollectionsEvent>()
    val events = _events.asSharedFlow()
    
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    init {
        observeCollections()
    }
    
    private fun observeCollections() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    collectionRepository.getUserCollections(user.id).collect { collections ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                collections = collections
                            ) 
                        }
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            collections = emptyList()
                        ) 
                    }
                }
            }
        }
    }
    
    fun loadCollectionDetail(collectionId: String) {
        viewModelScope.launch {
            val collection = collectionRepository.getCollectionById(collectionId)
            _uiState.update { it.copy(selectedCollection = collection) }
            
            if (collection != null) {
                collectionRepository.getQuotesInCollection(collectionId).collect { quotes ->
                    _uiState.update { it.copy(quotesInCollection = quotes) }
                }
            }
        }
    }
    
    fun showCreateDialog() {
        _uiState.update { 
            it.copy(
                showCreateDialog = true,
                newCollectionName = "",
                newCollectionDescription = ""
            ) 
        }
    }
    
    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }
    
    fun updateNewCollectionName(name: String) {
        _uiState.update { it.copy(newCollectionName = name) }
    }
    
    fun updateNewCollectionDescription(description: String) {
        _uiState.update { it.copy(newCollectionDescription = description) }
    }
    
    fun createCollection() {
        val state = _uiState.value
        if (state.newCollectionName.isBlank()) {
            viewModelScope.launch {
                _events.emit(CollectionsEvent.ShowMessage("Please enter a collection name"))
            }
            return
        }
        
        viewModelScope.launch {
            when (val result = collectionRepository.createCollection(
                name = state.newCollectionName,
                description = state.newCollectionDescription
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(showCreateDialog = false) }
                    _events.emit(CollectionsEvent.ShowMessage("Collection created!"))
                }
                is Resource.Error -> {
                    _events.emit(CollectionsEvent.ShowMessage(result.message ?: "Failed to create collection"))
                }
                else -> {}
            }
        }
    }
    
    fun deleteCollection(collectionId: String) {
        viewModelScope.launch {
            when (val result = collectionRepository.deleteCollection(collectionId)) {
                is Resource.Success -> {
                    _events.emit(CollectionsEvent.ShowMessage("Collection deleted"))
                }
                is Resource.Error -> {
                    _events.emit(CollectionsEvent.ShowMessage(result.message ?: "Failed to delete"))
                }
                else -> {}
            }
        }
    }
    
    fun showAddToCollectionDialog(quoteId: String) {
        _uiState.update { 
            it.copy(
                showAddToCollectionDialog = true,
                quoteToAdd = quoteId
            ) 
        }
    }
    
    fun hideAddToCollectionDialog() {
        _uiState.update { 
            it.copy(
                showAddToCollectionDialog = false,
                quoteToAdd = null
            ) 
        }
    }
    
    fun addQuoteToCollection(collectionId: String) {
        val quoteId = _uiState.value.quoteToAdd
        android.util.Log.d("CollectionsVM", "addQuoteToCollection called - collectionId: $collectionId, quoteId: $quoteId")
        
        if (quoteId == null) {
            android.util.Log.e("CollectionsVM", "quoteToAdd is null!")
            return
        }
        
        viewModelScope.launch {
            android.util.Log.d("CollectionsVM", "Calling repository.addQuoteToCollection")
            when (val result = collectionRepository.addQuoteToCollection(collectionId, quoteId)) {
                is Resource.Success -> {
                    android.util.Log.d("CollectionsVM", "Successfully added quote to collection")
                    hideAddToCollectionDialog()
                    _events.emit(CollectionsEvent.ShowMessage("Added to collection"))
                }
                is Resource.Error -> {
                    android.util.Log.e("CollectionsVM", "Error adding: ${result.message}")
                    _events.emit(CollectionsEvent.ShowMessage(result.message ?: "Failed to add"))
                }
                else -> {}
            }
        }
    }
    
    fun removeQuoteFromCollection(collectionId: String, quoteId: String) {
        viewModelScope.launch {
            when (val result = collectionRepository.removeQuoteFromCollection(collectionId, quoteId)) {
                is Resource.Success -> {
                    _events.emit(CollectionsEvent.ShowMessage("Removed from collection"))
                }
                is Resource.Error -> {
                    _events.emit(CollectionsEvent.ShowMessage(result.message ?: "Failed to remove"))
                }
                else -> {}
            }
        }
    }
}
