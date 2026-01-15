package max.ohm.quoteapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuoteCollection(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val userId: String = "",
    val quoteIds: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)
