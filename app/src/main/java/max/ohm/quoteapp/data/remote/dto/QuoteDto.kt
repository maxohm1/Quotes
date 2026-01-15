package max.ohm.quoteapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCategory

@Serializable
data class QuoteDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("text")
    val text: String = "",
    @SerialName("author")
    val author: String = "",
    @SerialName("category")
    val category: String = "",
    @SerialName("created_at")
    val createdAt: String = ""
) {
    fun toDomain(isFavorite: Boolean = false): Quote {
        return Quote(
            id = id,
            text = text,
            author = author,
            category = QuoteCategory.fromString(category),
            createdAt = createdAt,
            isFavorite = isFavorite
        )
    }
}

@Serializable
data class InsertQuoteDto(
    @SerialName("text")
    val text: String,
    @SerialName("author")
    val author: String,
    @SerialName("category")
    val category: String
)
