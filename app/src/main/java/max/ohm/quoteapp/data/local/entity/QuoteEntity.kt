package max.ohm.quoteapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import max.ohm.quoteapp.domain.model.Quote
import max.ohm.quoteapp.domain.model.QuoteCategory

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val createdAt: String,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): Quote {
        return Quote(
            id = id,
            text = text,
            author = author,
            category = QuoteCategory.fromString(category),
            createdAt = createdAt,
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromDomain(quote: Quote): QuoteEntity {
            return QuoteEntity(
                id = quote.id,
                text = quote.text,
                author = quote.author,
                category = quote.category.name,
                createdAt = quote.createdAt,
                isFavorite = quote.isFavorite
            )
        }
    }
}
