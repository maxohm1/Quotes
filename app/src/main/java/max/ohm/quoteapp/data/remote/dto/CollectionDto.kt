package max.ohm.quoteapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import max.ohm.quoteapp.domain.model.QuoteCollection

@Serializable
data class CollectionDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
) {
    fun toDomain(quoteIds: List<String> = emptyList()): QuoteCollection {
        return QuoteCollection(
            id = id,
            name = name,
            description = description,
            userId = userId,
            quoteIds = quoteIds,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

@Serializable
data class InsertCollectionDto(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("user_id")
    val userId: String
)

@Serializable
data class CollectionQuoteDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("collection_id")
    val collectionId: String = "",
    @SerialName("quote_id")
    val quoteId: String = "",
    @SerialName("created_at")
    val createdAt: String = ""
)

@Serializable
data class InsertCollectionQuoteDto(
    @SerialName("collection_id")
    val collectionId: String,
    @SerialName("quote_id")
    val quoteId: String
)
