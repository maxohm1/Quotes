package max.ohm.quoteapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import max.ohm.quoteapp.domain.model.User

@Serializable
data class UserProfileDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("display_name")
    val displayName: String = "",
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String = ""
) {
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            displayName = displayName,
            avatarUrl = avatarUrl,
            createdAt = createdAt
        )
    }
}

@Serializable
data class InsertUserProfileDto(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String,
    @SerialName("display_name")
    val displayName: String
)

@Serializable
data class UpdateUserProfileDto(
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)
