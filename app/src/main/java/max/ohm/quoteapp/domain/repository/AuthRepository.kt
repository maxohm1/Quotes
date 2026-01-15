package max.ohm.quoteapp.domain.repository

import kotlinx.coroutines.flow.Flow
import max.ohm.quoteapp.domain.model.User
import max.ohm.quoteapp.util.Resource

interface AuthRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Flow<Boolean>
    
    suspend fun signUp(email: String, password: String, displayName: String): Resource<User>
    suspend fun signIn(email: String, password: String): Resource<User>
    suspend fun signOut(): Resource<Unit>
    suspend fun resetPassword(email: String): Resource<Unit>
    suspend fun updateProfile(displayName: String?, avatarUrl: String?): Resource<User>
    suspend fun getCurrentUser(): User?
    suspend fun refreshSession(): Resource<Unit>
}
