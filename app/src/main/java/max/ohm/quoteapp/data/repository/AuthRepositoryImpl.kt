package max.ohm.quoteapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import max.ohm.quoteapp.data.remote.dto.InsertUserProfileDto
import max.ohm.quoteapp.data.remote.dto.UpdateUserProfileDto
import max.ohm.quoteapp.data.remote.dto.UserProfileDto
import max.ohm.quoteapp.domain.model.User
import max.ohm.quoteapp.domain.repository.AuthRepository
import max.ohm.quoteapp.util.Constants
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val dataStore = context.authDataStore

    override val currentUser: Flow<User?> = supabaseClient.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> {
                val authUser = status.session.user
                authUser?.let {
                    User(
                        id = it.id,
                        email = it.email ?: "",
                        displayName = it.userMetadata?.get("display_name")?.toString()?.removeSurrounding("\"") ?: "",
                        avatarUrl = it.userMetadata?.get("avatar_url")?.toString()?.removeSurrounding("\""),
                        createdAt = it.createdAt?.toString() ?: ""
                    )
                }
            }
            else -> null
        }
    }

    override val isLoggedIn: Flow<Boolean> = supabaseClient.auth.sessionStatus.map { status ->
        status is SessionStatus.Authenticated
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Resource<User> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("display_name", displayName)
                }
            }
            
            val authUser = supabaseClient.auth.currentUserOrNull()
            if (authUser != null) {
                // Create user profile in database
                try {
                    supabaseClient.postgrest[Constants.TABLE_USER_PROFILES].insert(
                        InsertUserProfileDto(
                            id = authUser.id,
                            email = email,
                            displayName = displayName
                        )
                    )
                } catch (e: Exception) {
                    // Profile might already exist, continue
                }
                
                Resource.Success(
                    User(
                        id = authUser.id,
                        email = email,
                        displayName = displayName,
                        createdAt = authUser.createdAt?.toString() ?: ""
                    )
                )
            } else {
                Resource.Error("Sign up failed. Please check your email to confirm your account.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<User> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val authUser = supabaseClient.auth.currentUserOrNull()
            if (authUser != null) {
                Resource.Success(
                    User(
                        id = authUser.id,
                        email = authUser.email ?: "",
                        displayName = authUser.userMetadata?.get("display_name")?.toString()?.removeSurrounding("\"") ?: "",
                        avatarUrl = authUser.userMetadata?.get("avatar_url")?.toString()?.removeSurrounding("\""),
                        createdAt = authUser.createdAt?.toString() ?: ""
                    )
                )
            } else {
                Resource.Error("Sign in failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return try {
            supabaseClient.auth.signOut()
            dataStore.edit { it.clear() }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun resetPassword(email: String): Resource<Unit> {
        return try {
            supabaseClient.auth.resetPasswordForEmail(email)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Password reset failed")
        }
    }

    override suspend fun updateProfile(displayName: String?, avatarUrl: String?): Resource<User> {
        return try {
            val currentUser = supabaseClient.auth.currentUserOrNull()
                ?: return Resource.Error("Not authenticated")
            
            // Update user metadata
            supabaseClient.auth.updateUser {
                this.data = buildJsonObject {
                    displayName?.let { put("display_name", it) }
                    avatarUrl?.let { put("avatar_url", it) }
                }
            }
            
            // Update user profile in database
            try {
                supabaseClient.postgrest[Constants.TABLE_USER_PROFILES]
                    .update(UpdateUserProfileDto(displayName, avatarUrl)) {
                        filter { eq("id", currentUser.id) }
                    }
            } catch (e: Exception) {
                // Continue if profile update fails
            }
            
            val updatedUser = supabaseClient.auth.currentUserOrNull()
            Resource.Success(
                User(
                    id = updatedUser?.id ?: currentUser.id,
                    email = updatedUser?.email ?: currentUser.email ?: "",
                    displayName = displayName ?: "",
                    avatarUrl = avatarUrl,
                    createdAt = updatedUser?.createdAt?.toString() ?: ""
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Profile update failed")
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val authUser = supabaseClient.auth.currentUserOrNull()
            authUser?.let {
                User(
                    id = it.id,
                    email = it.email ?: "",
                    displayName = it.userMetadata?.get("display_name")?.toString()?.removeSurrounding("\"") ?: "",
                    avatarUrl = it.userMetadata?.get("avatar_url")?.toString()?.removeSurrounding("\""),
                    createdAt = it.createdAt?.toString() ?: ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun refreshSession(): Resource<Unit> {
        return try {
            supabaseClient.auth.refreshCurrentSession()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Session refresh failed")
        }
    }
}
