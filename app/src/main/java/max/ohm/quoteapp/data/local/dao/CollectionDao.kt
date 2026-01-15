package max.ohm.quoteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import max.ohm.quoteapp.data.local.entity.CollectionEntity
import max.ohm.quoteapp.data.local.entity.CollectionQuoteEntity

@Dao
interface CollectionDao {
    @Query("SELECT DISTINCT c.* FROM collections c LEFT JOIN collection_quotes cq ON c.id = cq.collectionId WHERE c.userId = :userId ORDER BY c.updatedAt DESC")
    fun getCollectionsByUser(userId: String): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: String): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<CollectionEntity>)

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollection(id: String)

    @Query("UPDATE collections SET name = :name, description = :description, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCollection(id: String, name: String, description: String, updatedAt: String)

    // Collection Quotes
    @Query("SELECT quoteId FROM collection_quotes WHERE collectionId = :collectionId")
    fun getQuoteIdsInCollection(collectionId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuoteToCollection(collectionQuote: CollectionQuoteEntity)

    @Query("DELETE FROM collection_quotes WHERE collectionId = :collectionId AND quoteId = :quoteId")
    suspend fun removeQuoteFromCollection(collectionId: String, quoteId: String)

    @Query("DELETE FROM collection_quotes WHERE collectionId = :collectionId")
    suspend fun clearCollection(collectionId: String)

    @Transaction
    suspend fun deleteCollectionWithQuotes(collectionId: String) {
        clearCollection(collectionId)
        deleteCollection(collectionId)
    }
}
