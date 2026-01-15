package max.ohm.quoteapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: String = "",
    val text: String = "",
    val author: String = "",
    val category: QuoteCategory = QuoteCategory.MOTIVATION,
    val createdAt: String = "",
    val isFavorite: Boolean = false
)

@Serializable
enum class QuoteCategory(val displayName: String) {
    MOTIVATION("Motivation"),
    LOVE("Love"),
    SUCCESS("Success"),
    WISDOM("Wisdom"),
    HUMOR("Humor"),
    LIFE("Life"),
    HAPPINESS("Happiness"),
    FRIENDSHIP("Friendship"),
    LEADERSHIP("Leadership"),
    CREATIVITY("Creativity"),
    COURAGE("Courage");

    companion object {
        fun fromString(value: String): QuoteCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MOTIVATION
        }
    }
}
