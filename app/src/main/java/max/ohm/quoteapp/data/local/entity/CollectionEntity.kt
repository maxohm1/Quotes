package max.ohm.quoteapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import max.ohm.quoteapp.domain.model.QuoteCollection

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    val lastSynced: Long = System.currentTimeMillis()
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

    companion object {
        fun fromDomain(collection: QuoteCollection): CollectionEntity {
            return CollectionEntity(
                id = collection.id,
                name = collection.name,
                description = collection.description,
                userId = collection.userId,
                createdAt = collection.createdAt,
                updatedAt = collection.updatedAt
            )
        }
    }
}

@Entity(tableName = "collection_quotes", primaryKeys = ["collectionId", "quoteId"])
data class CollectionQuoteEntity(
    val collectionId: String,
    val quoteId: String,
    val addedAt: Long = System.currentTimeMillis()
)
