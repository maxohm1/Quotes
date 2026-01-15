package max.ohm.quoteapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("quote_id")
    val quoteId: String = "",
    @SerialName("created_at")
    val createdAt: String = ""
)

@Serializable
data class InsertFavoriteDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("quote_id")
    val quoteId: String
)
