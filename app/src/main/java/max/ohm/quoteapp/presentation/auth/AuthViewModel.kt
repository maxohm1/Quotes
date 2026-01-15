package max.ohm.quoteapp.presentation.auth

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
import max.ohm.quoteapp.domain.model.User
import max.ohm.quoteapp.domain.repository.AuthRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject
import android.content.Context
import android.net.Uri
import max.ohm.quoteapp.domain.repository.StorageRepository

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val displayNameError: String? = null,
    val resetEmailSent: Boolean = false,
    val showEditProfileDialog: Boolean = false,
    val newAvatarUrl: String = ""
)

sealed interface AuthEvent {
    data object NavigateToHome : AuthEvent
    data object NavigateToLogin : AuthEvent
    data class ShowMessage(val message: String) : AuthEvent
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()
    
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, error = null) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, error = null) }
    }
    
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, error = null) }
    }
    
    fun updateDisplayName(displayName: String) {
        _uiState.update { it.copy(displayName = displayName, displayNameError = null, error = null) }
    }
    
    fun signIn() {
        val state = _uiState.value
        
        // Validate
        var hasError = false
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email") }
            hasError = true
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (hasError) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.signIn(state.email, state.password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Sign in failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun signUp() {
        val state = _uiState.value
        
        // Validate
        var hasError = false
        if (state.displayName.isBlank()) {
            _uiState.update { it.copy(displayNameError = "Please enter your name") }
            hasError = true
        }
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email") }
            hasError = true
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords don't match") }
            hasError = true
        }
        if (hasError) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.signUp(state.email, state.password, state.displayName)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.ShowMessage("Account created! Please check your email to verify."))
                    _events.emit(AuthEvent.NavigateToLogin)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Sign up failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun resetPassword() {
        val state = _uiState.value
        
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.resetPassword(state.email)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            resetEmailSent = true
                        ) 
                    }
                    _events.emit(AuthEvent.ShowMessage("Password reset email sent!"))
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Password reset failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun updateProfile() {
        val state = _uiState.value
        
        if (state.displayName.isBlank()) {
            _uiState.update { it.copy(displayNameError = "Display name cannot be empty") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // We use the displayName from state, and assume a new field for avatarUrl or reuse existing logic if needed.
            // But wait, the state object doesn't have a specific `avatarUrl` field for editing distinct from the user's current one.
            // Let's add `newAvatarUrl` to the state first.
             when (val result = authRepository.updateProfile(state.displayName, state.newAvatarUrl)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            showEditProfileDialog = false
                        )
                    }
                    _events.emit(AuthEvent.ShowMessage("Profile updated successfully"))
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Profile update failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun showEditProfileDialog(currentUser: User?) {
        _uiState.update { 
            it.copy(
                showEditProfileDialog = true,
                displayName = currentUser?.displayName ?: "",
                newAvatarUrl = currentUser?.avatarUrl ?: "", // Using a new field for editing
                displayNameError = null
            ) 
        }
    }

    fun hideEditProfileDialog() {
        _uiState.update { it.copy(showEditProfileDialog = false) }
    }
    
    fun updateNewAvatarUrl(url: String) {
        _uiState.update { it.copy(newAvatarUrl = url) }
    }

    fun onAvatarPicked(uri: Uri) {
        val userId = currentUser.value?.id ?: return
        android.util.Log.d("AuthVM", "onAvatarPicked: $uri for user $userId")
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                
                if (bytes != null) {
                    android.util.Log.d("AuthVM", "Read ${bytes.size} bytes from image")
                    when (val result = storageRepository.uploadProfilePicture(userId, bytes)) {
                        is Resource.Success -> {
                            android.util.Log.d("AuthVM", "Upload success: ${result.data}")
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    newAvatarUrl = result.data ?: ""
                                )
                            }
                        }
                        is Resource.Error -> {
                            android.util.Log.e("AuthVM", "Upload failed: ${result.message}")
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = result.message ?: "Failed to upload image"
                                )
                            }
                        }
                        else -> {}
                    }
                } else {
                    android.util.Log.e("AuthVM", "Failed to read bytes from URI")
                    _uiState.update { it.copy(isLoading = false, error = "Failed to read image") }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthVM", "Exception processing image", e)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error processing image") }
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _events.emit(AuthEvent.NavigateToLogin)
        }
    }
    
    fun clearState() {
        _uiState.update { AuthUiState() }
    }
}
